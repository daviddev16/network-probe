package com.networkprobe.command.impl;

import java.net.InetAddress;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.command.CommandExecutor;
import com.networkprobe.command.response.EligibleResponse;
import com.networkprobe.command.response.ResponseBuilder;
import com.networkprobe.utils.Utilities;

import static com.networkprobe.command.response.ResponseBuilder.response;

public class ServerIPCommand implements CommandExecutor {

	private static final Logger LOG = LoggerFactory.getLogger(ServerIPCommand.class);

	@Override
	public EligibleResponse execute() {
		try {
			return  response(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			Utilities.logException(LOG, e, true);
		}
		return ResponseBuilder.empty();
	}

}
