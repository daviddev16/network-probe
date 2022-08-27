package com.networkprobe.subject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.management.AttributeNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.Constants;
import com.networkprobe.command.CommandType;
import com.networkprobe.networking.NetworkBroadcast;
import com.networkprobe.networking.NetworkEnvironment;
import com.networkprobe.networking.subject.NetworkSubject;
import com.networkprobe.networking.subject.NetworkSubjectType;
import com.networkprobe.utils.Utilities;

public class NetworkClient implements NetworkSubject {

	private static final Logger LOG = LoggerFactory.getLogger(NetworkClient.class);

	private DatagramSocket socket;

	public void execute() {

		LOG.info("Executing Client");
		try {
			socket = new DatagramSocket();
		} catch (Exception e) {
			Utilities.logException(LOG, e, true);
		}
		LOG.info("Running");
		while (true) {
			try {
				LOG.info("Requesting server IP address...");

				NetworkBroadcast.broadcast((address) -> {
					synchronized (socket) 
					{
						byte[] buffer = CommandType.REQUEST_SERVER_IP.getIdAsString().getBytes();
						NetworkEnvironment.getEnvironment().sendAsyncPacket(buffer, buffer.length, getSocket(), address, Constants.PORT);
					}
				}, false);


				LOG.info("Waiting for the server answer...");

				/*byte[] serverResponseBuf = new byte[Constants.EXCHANGE_DATA_MAX_LENGTH];
				DatagramPacket serverResponsePacket = new DatagramPacket(serverResponseBuf, serverResponseBuf.length);
				getSocket().receive(serverResponsePacket);
				 */
				
				byte[] receivedData = NetworkEnvironment.getEnvironment()
						.receive(socket).getData();

				/* depois que recebe a resposta de uma rede, ele deve ignorar as outras. */
				NetworkEnvironment.getEnvironment().clearAllPackets();

				String serverIpAddress = new String(receivedData).trim();
				LOG.info("Recebido: " + serverIpAddress);
				LOG.info("Atualizando ip do clienteBD para " + serverIpAddress);

				/* atualiza o registro do cliente BD */
				//Utilities.updateClientBDAddress(serverIpAddress);

				socket.close();
				Runtime.getRuntime().exit(-1);

			} catch (Exception e) {
				if (e instanceof ArithmeticException || e instanceof AttributeNotFoundException) {
					Utilities.logException(LOG, e, false);
					continue;
				}
				Utilities.logException(LOG, e, true);
			}
		}
	}

	public void close() {
		getSocket().close();
	}

	public NetworkSubjectType getType() {
		return NetworkSubjectType.CLIENT;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

}