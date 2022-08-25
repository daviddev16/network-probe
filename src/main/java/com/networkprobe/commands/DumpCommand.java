package com.networkprobe.commands;

import java.util.function.Function;


public class DumpCommand implements Function<Integer, String> {

	public String apply(Integer commandId) {
		return "Hi, I'm a unknown command.";
	}

}
