package com.networkprobe.networking;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.subject.NetworkClient;
import com.networkprobe.utils.Utilities;

public final class NetworkBroadcast {

	private static final Logger LOG = LoggerFactory.getLogger(NetworkClient.class);

	public static final  String DEFAULT_BROADCAST_ADDRESS = "255.255.255.255";
	
	private static Set<InetAddress> broadcastAddresses = Collections.synchronizedSet(new HashSet<InetAddress>());

	public static void registerAllBroadcastAddresses() throws SocketException {
		
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isLoopback())
				continue;
			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
				if(interfaceAddress.getBroadcast() != null) {
					getAllBroadcastAddresses().add(interfaceAddress.getBroadcast());
				}
			}
		}
		if(getAllBroadcastAddresses().isEmpty())
			try {
				getAllBroadcastAddresses().add(InetAddress.getByName(DEFAULT_BROADCAST_ADDRESS));
			} catch (UnknownHostException e) {
				LOG.info("{} could not register any broadcast address.", NetworkBroadcast.class.getSimpleName());
				Utilities.logException(LOG, e, true);
			}
	}

	public static void broadcast(Consumer<InetAddress> consumer, boolean isSameIPClass) {
		Objects.requireNonNull(consumer, "Broadcast consumer cannot be null.");
		getAllBroadcastAddresses().stream()
			.filter(bAddress -> !isSameIPClass || Utilities.checkCIDR(bAddress))
			.forEach(consumer);
	}
	
	public static Set<InetAddress> getAllBroadcastAddresses() {
		return broadcastAddresses;
	}

}
