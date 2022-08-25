package com.networkprobe.networking;


import java.net.DatagramSocket;

public interface NetworkSubject {

	NetworkSubjectType getType();

	DatagramSocket getSocket();
	
	void execute();

	void close();

}
