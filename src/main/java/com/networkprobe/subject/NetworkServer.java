package com.networkprobe.subject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.management.AttributeNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.Constants;
import com.networkprobe.commands.CommandType;
import com.networkprobe.commands.DumpCommand;
import com.networkprobe.commands.ServerIPCommand;
import com.networkprobe.networking.NetworkSubject;
import com.networkprobe.networking.NetworkSubjectType;
import com.networkprobe.utils.Utilities;

public class NetworkServer implements NetworkSubject {

	private static final Logger LOG = LoggerFactory.getLogger(NetworkServer.class);

	private DatagramSocket socket;

	private Map<Integer, Function<Integer, String>> commands = Collections.synchronizedMap(
			new HashMap<Integer, Function<Integer, String>>()
			);

	public NetworkServer() {}

	public void execute() {

		LOG.info("Loading as server...");

		getCommands().put(CommandType.REQUEST_SERVER_IP.getCommandId(), new ServerIPCommand());
		getCommands().put(CommandType.UNKNOWN.getCommandId(), new DumpCommand());
		
		try {
			socket = new DatagramSocket(Constants.PORT);
		} catch (Exception e1) {
			Utilities.logException(LOG, e1);
			System.exit(-1);
		}
		
		LOG.info("Listening on port {}", Constants.PORT);
		
		while (true) {
			try {
				/* recebendo comando do CLIENT */
				byte[] recBuf = new byte[Constants.EXCHANGE_DATA_MAX_LENGTH];
				DatagramPacket requestPacket = new DatagramPacket(recBuf, recBuf.length);
				getSocket().receive(requestPacket);

				/* Respondendo comando */
				Integer commandId =  Integer.parseInt(new String(requestPacket.getData()).trim());
				if (!getCommands().containsKey(commandId))
					throw new AttributeNotFoundException("Comando não achado.");
				
				LOG.info("Command \"{}\" requested by \"{}\"", CommandType.REQUEST_SERVER_IP.name(), requestPacket.getAddress().getHostAddress());
				
				Function<Integer, String> commandFunction = getCommands().get(commandId);
				String responseMessage = commandFunction.apply(commandId);
				byte[] responseMessageBuf = responseMessage.getBytes();
				
				/* respondendo a requisição */
				DatagramPacket responsePacket = new DatagramPacket(responseMessageBuf, responseMessageBuf.length, 
						requestPacket.getAddress(), requestPacket.getPort());
	            getSocket().send(responsePacket);
	            LOG.info("{} sent to \"{}\"", String.format("[Response=%s]", responseMessage), 
	            		String.format("%s:%s", requestPacket.getAddress().getHostAddress(), requestPacket.getPort()));
	            
			} catch (Exception e) {
				if (e instanceof ArithmeticException || e instanceof AttributeNotFoundException) {
					Utilities.logException(LOG, e);
					continue;
				}
				Utilities.logException(LOG, e);
				System.exit(-1);
			}
		}
	}

	public Map<Integer, Function<Integer, String>> getCommands() {
		return commands;
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
