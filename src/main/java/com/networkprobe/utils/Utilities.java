package com.networkprobe.utils;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;

public final class Utilities {
	
	/** Verificar se a classe do IP da m�quina local � a mesma que a classe do InetAddress informado. */
	public static boolean checkCIDR(InetAddress inetAddress) {
		try {
			return InetAddress.getLocalHost().getHostAddress().split("\\.")[0]
					.equals(inetAddress.getHostAddress().split("\\.")[0]);
		} catch (UnknownHostException e) { /* ignore */ }
		return false;
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
		
		Option enableLoggingOpt = Option.builder("l")
				.longOpt("enableLogging")
				.hasArg()
				.required(false)
				.desc("Set the logger state to ON/OFF")
				.build();

		options.addOption(subjectTypeOpt);
		options.addOption(enableLoggingOpt);

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
	
	public static void logException(Logger logger, Exception exception, boolean exit) {
		logger.error("~~~~~~~~~ EXCEPTION ~~~~~~~~~");
		logger.error(exception.getMessage());
		for (StackTraceElement element : exception.getStackTrace()) {
			logger.error(element.toString());
		}
		logger.error("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		exception.printStackTrace();
		if(exit) System.exit(-1);
	}
	
	public static String getDataAsString(byte[] data) throws IOException {
		try {
			return new String(data).trim();
		} catch (Exception e) {
			throw new IOException("Invalid data.");
		}
	}
	
	public static boolean isNullOrEmpty(String str) {
		return (str == null || str.isEmpty());
	}

}
