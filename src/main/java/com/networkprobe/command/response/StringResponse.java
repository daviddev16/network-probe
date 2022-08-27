package com.networkprobe.command.response;

class StringResponse implements EligibleResponse {

	private final byte[] buffer;
	
	public StringResponse(String response) {
		this.buffer = response.getBytes();
	}
	
	@Override
	public byte[] getBuffer() {
		return this.buffer;
	}

}
