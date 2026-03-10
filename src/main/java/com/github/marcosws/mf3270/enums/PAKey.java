package com.github.marcosws.mf3270.enums;

/**
 * Enum representing the PA keys (Program Attention keys) used in 3270 terminal sessions.
 */
public enum PAKey {
	
    PA1(1),
    PA2(2),
    PA3(3);

    private final int value;

    PAKey(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
