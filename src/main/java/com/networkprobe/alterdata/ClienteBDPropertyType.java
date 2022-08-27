package com.networkprobe.alterdata;

import java.util.Arrays;

public enum ClienteBDPropertyType {

	DATABASE_NAME              ("Database"       , "ALTERDATA_{SISTEMA}"),
	SERVER_ADDRESS             ("Server"         , "localhost"),
	
	CHANGE_DATABASE_PROVIDER   ("ProviderName"   , "PostgreSQL", "SQL Server"),
	CHANGE_SERVER_ADDRESS      ("ShowConnection" , "S", "N"),
	CHANGE_CONNECTION_DATABASE ("ShowDataBase"   , "S", "N");
	
	private final String[] defaults;
	private final String key;
	
	private ClienteBDPropertyType(String key, String... defaults) {
		this.defaults = Arrays.copyOf(defaults, defaults.length);
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String[] getDefaults() {
		return defaults;
	}
	
}
