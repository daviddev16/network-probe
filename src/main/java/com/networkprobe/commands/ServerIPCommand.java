package com.networkprobe.commands;

import java.net.InetAddress;

import java.net.UnknownHostException;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.utils.Utilities;

public class ServerIPCommand implements Function<Integer, String> {

	private static final Logger LOG = LoggerFactory.getLogger(ServerIPCommand.class);

	public String apply(Integer commandId) {
		try {
			return InetAddress.getLocalHost().getHostAddress().trim();
		} catch (UnknownHostException e) {
			Utilities.logException(LOG, e);
			System.exit(-1);
		}
		return "?";
	}

}
