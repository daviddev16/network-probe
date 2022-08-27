package com.networkprobe.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.directory.AttributeInUseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommandManager {

	private static CommandManager instance;

	private static final Logger LOG = LoggerFactory.getLogger(CommandManager.class);

	private Map<Integer, CommandExecutor> commands;

	public CommandManager() throws InstanceAlreadyExistsException {
		if (instance != null)
			throw new InstanceAlreadyExistsException("A instance of CommandManager already exists.");

		LOG.info("Ready.");
		instance = this;
		commands = Collections.synchronizedMap(new HashMap<Integer, CommandExecutor>());
	}

	public void register(CommandType commandType, CommandExecutor commandExecutor) throws AttributeInUseException {
		Objects.requireNonNull(commandType, "Command type cannot be null");
		Objects.requireNonNull(commandExecutor, "Command executor cannot be null");

		if (!getCommands().containsKey(commandType.getId())) {
			getCommands().put(commandType.getId(), commandExecutor);
			return;
		}
		
		throw new AttributeInUseException("This command is already registered.");
	}
	
	public CommandExecutor get(CommandType commandType) {
		Objects.requireNonNull(commandType, "Command type cannot be null");
		return get(commandType.getId());
	}
	
	public CommandExecutor get(int commandId) {
		return getCommands().getOrDefault(commandId, null);
	}

	public Map<Integer, CommandExecutor> getCommands() {
		return commands;
	}
	
	public static void create() throws InstanceAlreadyExistsException {
		new CommandManager();
	}
	
	public static CommandManager getManager() {
		return instance;
	}
}
