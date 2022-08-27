package com.networkprobe.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.utils.Utilities;

class NetworkPacketSender implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(NetworkPacketSender.class);

	private final Thread threadSender;
	private final DatagramSocket socket;
	private final InetAddress address;
	private final int port;
	private final byte[] buf;
	private final int length;
	private AtomicBoolean doneReference;

	public NetworkPacketSender(byte[] buf, int length, DatagramSocket socket, InetAddress address, int port) {
		this.threadSender = new Thread(this);
		this.threadSender.setDaemon(true);
		this.socket = socket;
		this.address = address;
		this.port = port;
		this.buf = buf;
		this.length = length;
		this.doneReference = new AtomicBoolean(false);
	}

	public void run() {
		try {
			LOG.info("Sending a data packet to {}", getAddress().getHostAddress());
			getSocket().send(new DatagramPacket(getBuffer(), getLength(), getAddress(), getPort()));
		} catch (IOException e) {
			Utilities.logException(LOG, e, true);
		} finally {
			doneReference.set(true);
		}
	}
	
	public void destroy() throws InterruptedException {
		if (!getThreadSender().isInterrupted())
			getThreadSender().join();
	}
	
	public int getLength() {
		return (length <= 0) ? getBuffer().length : length;
	}
	
	public void startSender() {
		getThreadSender().start();
	}
	
	public boolean isDone() {
		return doneReference.get();
	}

	public Thread getThreadSender() {
		return threadSender;
	}

	public byte[] getBuffer() {
		return buf;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

}
