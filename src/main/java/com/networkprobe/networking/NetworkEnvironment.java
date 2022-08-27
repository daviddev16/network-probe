package com.networkprobe.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.management.InstanceAlreadyExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.Constants;

/**
 * it manages the data traffic 
 */
public final class NetworkEnvironment {

	private static final Logger LOG = LoggerFactory.getLogger(NetworkEnvironment.class);

	private static NetworkEnvironment instance;

	private List<NetworkPacketSender> packetSenders;

	private NetworkEnvironment() throws InstanceAlreadyExistsException {
		if (instance != null)
			throw new InstanceAlreadyExistsException("A instance of NetworkEnvironment already exists.");

		LOG.info("Ready.");
		instance = this;
		packetSenders = Collections.synchronizedList(new ArrayList<>());
	}

	public void sendAsyncPacket(byte[] buf, int length, DatagramSocket socket, InetAddress address, int port) {
		NetworkPacketSender packetSender = new NetworkPacketSender(buf, length, socket, address, port);
		packetSender.startSender();
		getPacketSenders().add(packetSender);
	}

	/** get the thread blocked until it receives something */
	public synchronized DatagramPacket receive(DatagramSocket socket) throws IOException {
		byte[] buffer = new byte[Constants.EXCHANGE_DATA_MAX_LENGTH];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet); 
		return packet;
	}

	public synchronized void clearPacketSender(NetworkPacketSender packetSender) {
		Objects.requireNonNull(packetSender, "Packet sender cannot be nulll.");
		try {
			packetSender.destroy();
			if (getPacketSenders().contains(packetSender)) {
				getPacketSenders().remove(packetSender);
			}
		} catch (InterruptedException e) {/* ignore */}
	}

	public synchronized void clearAllPackets() {
		for (NetworkPacketSender packetSender : getPacketSenders()) {
			if(!packetSender.isDone())
				continue;
			if(packetSender != null && getPacketSenders().contains(packetSender))
				clearPacketSender(packetSender);
		}
		LOG.info("Packet senders cleared.");
	}

	public List<NetworkPacketSender> getPacketSenders() {
		return packetSenders;
	}

	public static void create() throws InstanceAlreadyExistsException {
		new NetworkEnvironment();
	}

	public static NetworkEnvironment getEnvironment() {
		return instance;
	}
}
