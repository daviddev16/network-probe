package com.networkprobe.networking;

public enum NetworkSubjectType {

	CLIENT,
	SERVER;
	
	@Override
	public String toString() {
		return "NetworkSubjectType@" + name();
	}
	
}
