package com.networkprobe.command.response;

import com.networkprobe.utils.Constants;

public abstract interface EligibleResponse {

	default int getLength() {
		return getBuffer() != null ? getBuffer().length : Constants.EXCHANGE_DATA_MAX_LENGTH;
	}

	public byte[] getBuffer();
	
}
