package com.networkprobe.subject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.management.AttributeNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.Constants;
import com.networkprobe.command.CommandExecutor;
import com.networkprobe.command.CommandManager;
import com.networkprobe.command.CommandType;
import com.networkprobe.command.response.EligibleResponse;
import com.networkprobe.networking.NetworkEnvironment;
import com.networkprobe.networking.subject.NetworkSubject;
import com.networkprobe.networking.subject.NetworkSubjectType;
import com.networkprobe.utils.Utilities;

public class NetworkServer implements NetworkSubject {

	private static final Logger LOG = LoggerFactory.getLogger(NetworkServer.class);

	private DatagramSocket socket;

	public NetworkServer() {
	}

	public void execute() {
		try {
			socket = new DatagramSocket(Constants.PORT);
		} catch (Exception e1) {
			Utilities.logException(LOG, e1, true);
		}
		LOG.info("Listening on port {}", Constants.PORT);

		CommandManager commandManager = CommandManager.getManager();

		while (true) {
			try {
				/* recebendo comando do CLIENT */
				byte[] recBuf = new byte[Constants.EXCHANGE_DATA_MAX_LENGTH];
				DatagramPacket requestPacket = new DatagramPacket(recBuf, recBuf.length);
				getSocket().receive(requestPacket);

				/* Respondendo comando */
				Integer commandId = Integer.parseInt(new String(requestPacket.getData()).trim());
				if (!commandManager.getCommands().containsKey(commandId))
					throw new AttributeNotFoundException("Comando não achado.");

				LOG.info("Command '{}' requested by '{}'", CommandType.REQUEST_SERVER_IP.name(),
						requestPacket.getAddress().getHostAddress());

				CommandExecutor executor = commandManager.get(commandId);
				
				/* Retorna uma resposta do servidor/comando em byte array */
				EligibleResponse response = executor.execute();

				byte[] buffer = response.getBuffer();
				NetworkEnvironment.getEnvironment().sendAsyncPacket(buffer, buffer.length, getSocket(),
						requestPacket.getAddress(), requestPacket.getPort());

				LOG.info("{} sent to \"{}\"", String.format("[Response=%s]", new String(buffer)),
						String.format("%s:%s", requestPacket.getAddress().getHostAddress(), requestPacket.getPort()));

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
		return NetworkSubjectType.SERVER;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

}
