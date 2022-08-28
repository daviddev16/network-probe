package com.networkprobe.networking.subject;


import java.net.DatagramSocket;

import org.apache.commons.cli.CommandLine;

public interface NetworkSubject {

	NetworkSubjectType getType();

	DatagramSocket getSocket();
	
	void execute(CommandLine commandLine);

	void close();

}
