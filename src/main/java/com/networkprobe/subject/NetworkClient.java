package com.networkprobe.subject;

import java.net.DatagramSocket;

import javax.management.AttributeNotFoundException;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.Constants;
import com.networkprobe.command.CommandType;
import com.networkprobe.networking.NetworkBroadcast;
import com.networkprobe.networking.NetworkEnvironment;
import com.networkprobe.networking.subject.NetworkSubject;
import com.networkprobe.networking.subject.NetworkSubjectType;
import com.networkprobe.utils.Utilities;

import br.com.alterdata.AlterdataAPI;
import br.com.alterdata.ClienteBDPropertyType;

public class NetworkClient implements NetworkSubject {

	private static final Logger LOG = LoggerFactory.getLogger(NetworkClient.class);

	private DatagramSocket socket;

	public void execute(CommandLine args) {
		LOG.info("Executing Client");
		try {
			socket = new DatagramSocket();
		} catch (Exception e) {
			Utilities.logException(LOG, e, true);
		}

		try {
			LOG.info("Requesting IP address to the network");

			/* Sending a broadcast to all Network interfaces broadcast IP. */
			NetworkBroadcast.broadcast((address) -> 
			{
				byte[] buffer = CommandType.REQUEST_SERVER_IP.getIdAsString().getBytes(); /*"9xaxai".getBytes()*/;
				NetworkEnvironment.getEnvironment().sendAsyncPacket(buffer, buffer.length, getSocket(), address, Constants.PORT);

			}, false);

			/* adicionar timer de resposta */

			LOG.info("Waiting for the server response [!]");

			byte[] receivedData = NetworkEnvironment.getEnvironment()
					.receive(socket).getData();

			/* depois que recebe a resposta de uma rede, ele deve ignorar as outras. */
			NetworkEnvironment.getEnvironment().clearAllPackets();

			String serverAddress = new String(receivedData).trim();
			LOG.info("Recebido: " + serverAddress);
			LOG.info("Atualizando ip do clienteBD para " + serverAddress);

			if (args.hasOption("enableLogging") && 
					args.getOptionValue("enableLogging").toUpperCase().equals("OFF")){
				System.out.print(serverAddress);
			} else {

				AlterdataAPI.updateClienteBDProperty(ClienteBDPropertyType.SERVER_ADDRESS, serverAddress);
			}
			getSocket().close();
			Runtime.getRuntime().exit(-1);

		} catch (Exception e) {
			if (e instanceof ArithmeticException || e instanceof AttributeNotFoundException) {
				Utilities.logException(LOG, e, false);
			}
			Utilities.logException(LOG, e, true);
		}
	}

	public void close() {
		getSocket().close();
	}

	public NetworkSubjectType getType() {
		return NetworkSubjectType.CLIENT;
	}

	public synchronized DatagramSocket getSocket() {
		return socket;
	}

}