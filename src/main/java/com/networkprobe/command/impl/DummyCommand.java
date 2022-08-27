package com.networkprobe.command.impl;

import static com.networkprobe.command.response.ResponseBuilder.response;

import com.networkprobe.command.CommandExecutor;
import com.networkprobe.command.response.EligibleResponse;

public class DummyCommand implements CommandExecutor {

	@Override
	public EligibleResponse execute() {
		return response("Unknown command!");
	}

}
