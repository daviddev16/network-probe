package com.networkprobe.subject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.management.AttributeNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.Constants;
import com.networkprobe.commands.CommandType;
import com.networkprobe.networking.NetworkSubject;
import com.networkprobe.networking.NetworkSubjectType;
import com.networkprobe.utils.Utilities;

public class NetworkClient implements NetworkSubject {

	private static final Logger LOG = LoggerFactory.getLogger(NetworkServer.class);

	private DatagramSocket socket;

	private final List<String> broadcastAddresses = Collections.synchronizedList(new ArrayList<String>());

	public void execute() {

		LOG.info("Loading as client...");
		try {

			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback())
					continue;
				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					if(interfaceAddress.getBroadcast() != null) {
						broadcastAddresses.add(interfaceAddress.getBroadcast().getHostAddress());
					}
				}
			}
			
			if(broadcastAddresses.isEmpty())
				broadcastAddresses.add(Constants.CLASSFUL_BROADCAST_ADDRESS);

			socket = new DatagramSocket();
		} catch (Exception e1) {
			Utilities.logException(LOG, e1);
			System.exit(-1);
		}

		LOG.info("Ready.");

		while (true) {
			try {
				LOG.info("Requesting server IP address...");

				for (final String bcAddress : broadcastAddresses) {
					new Thread(new Runnable() {
						public void run() {
							try {
								/* enviando requisição de comando */
								byte[] requestBuf = CommandType.REQUEST_SERVER_IP.getIdAsString().getBytes();
								DatagramPacket requestPacket = new DatagramPacket(requestBuf, requestBuf.length, InetAddress.getByName(bcAddress), Constants.PORT);
								getSocket().send(requestPacket);
							} catch (Exception e) {
								Utilities.logException(LOG, e);
							}	
						}
					}).start();
				}
				LOG.info("Waiting for the server answer...");

				byte[] serverResponseBuf = new byte[Constants.EXCHANGE_DATA_MAX_LENGTH];
				DatagramPacket serverResponsePacket = new DatagramPacket(serverResponseBuf, serverResponseBuf.length);
				getSocket().receive(serverResponsePacket);

				String serverIpAddress = new String(serverResponseBuf).trim();
				LOG.info("Recebido: " + serverIpAddress);
				LOG.info("Atualizando ip do clienteBD para " + serverIpAddress);
				
				/* atualiza o registro do cliente BD */
				Utilities.updateClientBDAddress(serverIpAddress);
				
				socket.close();
				Runtime.getRuntime().exit(-1);

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