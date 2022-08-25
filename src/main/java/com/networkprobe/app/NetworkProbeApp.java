package com.networkprobe.app;


import java.net.SocketException;
import java.util.NoSuchElementException;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.directory.AttributeInUseException;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.networking.NetworkSubject;
import com.networkprobe.networking.NetworkSubjectFactory;
import com.networkprobe.networking.NetworkSubjectType;
import com.networkprobe.utils.Utilities;

import static com.networkprobe.Constants.VERSION;
import static com.networkprobe.utils.Utilities.createCommandLine;

public class NetworkProbeApp {

	private static final Logger LOG = LoggerFactory.getLogger(NetworkProbeApp.class);

	public static void main(String[] args) throws SocketException {

		try {
			LOG.info("NetworkProbe {}", VERSION);

			CommandLine commandLine = createCommandLine(args);
			LOG.info("CommandLine loaded");

			String networkSubjectName = commandLine.getOptionValue("subjectTypeName").toUpperCase();
			NetworkSubjectType subjectType = NetworkSubjectType.valueOf(networkSubjectName);;
			LOG.info("NetworkSubject (network-side): {}", subjectType);

			LOG.info("Loading NetworkSubjectFactory...");
			NetworkSubjectFactory.create();

			LOG.info("Setting up NetworkSubject wrapper...");
			NetworkSubject networkSubject = NetworkSubjectFactory.getFactory()
					.getSubjectOf(subjectType);

			networkSubject.execute();

		} catch (Exception e) {
			if (e instanceof InstanceAlreadyExistsException) {
				LOG.error("Tried to instantiate twice a single-instance class.");	
			} else if (e instanceof IllegalArgumentException || e instanceof NoSuchElementException) {
				LOG.error("Tried to retrieve a invalid NetworkSubjectType from command args.");
			} else if (e instanceof AttributeInUseException) {
				LOG.error("Unable to register all commands.");	
			}
			Utilities.logException(LOG, e);
			System.exit(-1);
		}
	}
}
