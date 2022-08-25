package com.networkprobe.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;

import com.networkprobe.Constants;

public final class Utilities {
	
	public static InetAddress getBroadcastInetAddress() throws SocketException, UnknownHostException {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isLoopback())
				continue;
			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
				if (interfaceAddress.getBroadcast() != null)
					return interfaceAddress.getBroadcast();
			}
		}
		return InetAddress.getByName(Constants.CLASSFUL_BROADCAST_ADDRESS);
	}
	
	public static boolean isNullOrEmpty(String str) {
		return (str == null || str.isEmpty());
	}
	
	public static CommandLine createCommandLine(String[] args) {

		Options options = new Options();
		CommandLine commandLine = null;

		Option subjectTypeOpt = Option.builder("s")
				.longOpt("subjectTypeName")
				.hasArg()
				.required(true)
				.desc("Subject type for the current machine")
				.build();

		options.addOption(subjectTypeOpt);

		try {
			DefaultParser parser = new DefaultParser();
			commandLine = parser.parse(options, args);
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			System.out.println(e.getMessage());
			formatter.printHelp("network-manager", options);
			System.exit(-1);
		}

		return commandLine;
	}
	
	public static void updateClientBDAddress(String address) throws IOException {
		Runtime.getRuntime().exec(String.format("reg add \"%s\" /v %s /t REG_SZ /d %s /f", 
				"HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\TurboPower\\FlashFiler\\Client Configuration",
				"Server",
				address));
		
		System.out.println("Done.");
	}
	
	public static void logException(Logger logger, Exception exception) {
		logger.error("~~~~~~~~~ EXCEPTION ~~~~~~~~~");
		logger.error(exception.getMessage());
		for (StackTraceElement element : exception.getStackTrace()) {
			logger.error(element.toString());
		}
		logger.error("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

}
