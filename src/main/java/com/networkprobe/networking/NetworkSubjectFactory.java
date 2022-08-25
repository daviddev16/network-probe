package com.networkprobe.networking;

import java.util.NoSuchElementException;

import java.util.Objects;

import javax.management.InstanceAlreadyExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkprobe.subject.NetworkClient;
import com.networkprobe.subject.NetworkServer;

public class NetworkSubjectFactory {

	private static NetworkSubjectFactory instance;
	
	private static final Logger LOG = LoggerFactory.getLogger(NetworkSubjectFactory.class);

	private NetworkSubjectFactory() throws InstanceAlreadyExistsException {
		if (instance != null)
			throw new InstanceAlreadyExistsException("A instance of NetworkSubjectFactory already exists.");
		
		LOG.info("Ready.");
		instance = this;
	}
	
	public static void create() throws InstanceAlreadyExistsException {
		new NetworkSubjectFactory();
	}
	
	public NetworkSubject getSubjectOf(NetworkSubjectType subjectType) throws NoSuchElementException {
		Objects.requireNonNull(subjectType, "Subject type is null.");

		switch (subjectType) {
			case CLIENT:
				return new NetworkClient();
			case SERVER:
				return new NetworkServer();
		}

		throw new NoSuchElementException("There is no network subject for this type.");
	}
	
	public static NetworkSubjectFactory getFactory() {
		return instance;
	}

}
