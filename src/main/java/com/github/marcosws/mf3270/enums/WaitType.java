package com.github.marcosws.mf3270.enums;

/**
 * Enum representing the different wait types that can be used in 3270 terminal sessions.
 */
public enum WaitType {
	
	T3270_MODE("3270Mode"),
	NVT_MODE("NVTMode"),
	DISCONNECT("Disconnect"),
	INPUT_FIELD("InputField"),
	OUTPUT("Output"),
	UNLOCK("Unlock");
	
	private final String value;

	WaitType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
