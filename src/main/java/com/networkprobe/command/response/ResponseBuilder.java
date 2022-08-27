package com.networkprobe.command.response;

public final class ResponseBuilder {

	public static EligibleResponse response(String str) {
		return new StringResponse(str);
	}
	
	public static EligibleResponse empty() {
		return response("");
	}
	
}
