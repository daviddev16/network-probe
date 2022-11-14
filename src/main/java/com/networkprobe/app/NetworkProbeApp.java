package com.networkprobe.app;

import java.util.NoSuchElementException;

import java.net.SocketException;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.directory.AttributeInUseException;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.command.CommandManager;
import com.networkprobe.command.CommandType;
import com.networkprobe.command.impl.DummyCommand;
import com.networkprobe.command.impl.ServerIPCommand;
import com.networkprobe.networking.NetworkBroadcast;
import com.networkprobe.networking.NetworkEnvironment;
import com.networkprobe.networking.subject.NetworkSubject;
import com.networkprobe.networking.subject.NetworkSubjectFactory;
import com.networkprobe.networking.subject.NetworkSubjectType;
import com.networkprobe.utils.Utilities;

import static com.networkprobe.utils.Constants.VERSION;
import static com.networkprobe.utils.Utilities.createCommandLine;

public class NetworkProbeApp {

	private static final Logger LOG = LoggerFactory.getLogger(NetworkProbeApp.class);

	public static void main(String[] args) throws SocketException {

		try {
			CommandLine commandLine = createCommandLine(args);

			if (commandLine.hasOption("enableLogging") && 
					commandLine.getOptionValue("enableLogging").toUpperCase().equals("OFF")){
				org.apache.log4j.Logger.getRootLogger().setLevel(Level.OFF);
			}
				
			LOG.info("NetworkProbe {}", VERSION);
			LOG.info("CommandLine loaded");

			String networkSubjectName = commandLine.getOptionValue("subjectTypeName").toUpperCase();
			NetworkSubjectType subjectType = NetworkSubjectType.valueOf(networkSubjectName);;
			LOG.info("NetworkSubject (network-side): {}", subjectType);

			if (subjectType == NetworkSubjectType.CLIENT) {
				LOG.info("Registering all broadcast addresses.");
				NetworkBroadcast.registerAllBroadcastAddresses();
			}
			else if (subjectType == NetworkSubjectType.SERVER) {
				LOG.info("Loading command manager...");
				CommandManager.create();

				LOG.info("Loading commands....");
				CommandManager.getManager().register(CommandType.REQUEST_SERVER_IP, new ServerIPCommand());
				CommandManager.getManager().register(CommandType.UNKNOWN, new DummyCommand());
			}

			LOG.info("Loading NetworkSubjectFactory");
			NetworkSubjectFactory.create();

			LOG.info("Loading NetworkEnvironment");
			NetworkEnvironment.create();

			LOG.info("Setting up NetworkSubject wrapper");
			NetworkSubject networkSubject = NetworkSubjectFactory.getFactory()
					.getSubjectOf(subjectType);

			networkSubject.execute(commandLine);

		} catch (Exception e) {
			if (e instanceof InstanceAlreadyExistsException) {
				LOG.error("Tried to instantiate twice a single-instance class.");	
			} else if (e instanceof IllegalArgumentException || e instanceof NoSuchElementException) {
				LOG.error("Tried to retrieve a invalid NetworkSubjectType from command args.");
			} else if (e instanceof AttributeInUseException) {
				LOG.error("Unable to register all commands.");	
			}
			Utilities.logException(LOG, e, true);
		}
	}

}
