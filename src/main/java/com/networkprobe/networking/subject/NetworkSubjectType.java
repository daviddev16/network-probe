package com.networkprobe.networking.subject;

public enum NetworkSubjectType {

	CLIENT,
	SERVER;
	
	@Override
	public String toString() {
		return "NetworkSubjectType@" + name();
	}
	
}
