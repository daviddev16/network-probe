package com.networkprobe.alterdata;

import java.io.IOException;
import java.util.Objects;

import com.networkprobe.utils.Utilities;

public final class AlterdataAPI {

	private static final String CLIENTEBD_HKEY_PATH = "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\TurboPower\\FlashFiler\\Client Configuration";
	
	public static void updateClienteBDProperty(ClienteBDPropertyType clienteBDPropertyType, String value) throws IOException {
		Objects.requireNonNull(clienteBDPropertyType, "ClienteBD Property is null.");
		if (Utilities.isNullOrEmpty(value))
			throw new NullPointerException(String.format("The new value for \"%s\" is null. It will not be changed.",
					clienteBDPropertyType.getKey().toUpperCase()));
		
		Utilities.updateRegSubkey(CLIENTEBD_HKEY_PATH, clienteBDPropertyType.getKey(), value);
	}
	
}
