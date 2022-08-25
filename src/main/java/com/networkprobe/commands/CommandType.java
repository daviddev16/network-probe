package com.networkprobe.commands;

public enum CommandType {

	UNKNOWN(-1),
	REQUEST_SERVER_IP(0);
	
	private int commandId;
	
	private CommandType(int commandId) {
		this.commandId = commandId;
	}
	
	public int getCommandId() {
		return commandId;
	}

	public String getIdAsString() {
		return String.valueOf(commandId);
	}
	
	public static CommandType getCommandTypeById(int commandId) {
		for(CommandType commandType : values())
			if (commandType.getCommandId() == commandId)
				return commandType;
		return CommandType.UNKNOWN;
	}
	
}
