package com.networkprobe.networking;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.management.InstanceAlreadyExistsException;

/* não implementado */
public final class NetworkSecurity {

	public static final int REQUEST_COUNT_LIMIT_PER_CLIENT = 10;

	private static NetworkSecurity instance;

	private Map<InetAddress, Metric> metrics;

	public NetworkSecurity() throws InstanceAlreadyExistsException {

		if (instance == null)
			throw new InstanceAlreadyExistsException("A instance of NetworkSecurity already exists.");

		instance = this;
	}

	public Map<InetAddress, Metric> getMetrics() {
		return metrics;
	}

	public boolean allow(InetAddress address) {
		if (!getMetrics().containsKey(address)) {
			return true;
		}
		return (getMetrics().get(address).getDeltaTime() <= TimeUnit.SECONDS.toMillis(2)) && 
				getMetrics().get(address).getRequestCount() >= REQUEST_COUNT_LIMIT_PER_CLIENT; /* verificar de hora em hora para limpar metricas */
	}
	
	public void clearMetrics() {
		getMetrics().clear();
	}

	public static class Metric {

		private int requestCount = 0;
		private int lastTimeRequested = 0;
		private int deltaTime = 0;

		public Metric() {}

		public void countRequest( int currentMilis ) {
			deltaTime = currentMilis - lastTimeRequested;
			lastTimeRequested = currentMilis;
			requestCount++;
		}

		public int getDeltaTime() {
			return deltaTime;
		}

		public int getRequestCount() {
			return requestCount;
		}

		public int getLastTimeRequested() {
			return lastTimeRequested;
		}

	}

}
