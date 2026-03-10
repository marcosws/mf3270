package com.github.marcosws.mf3270.enums;


/**
 * Enumeração para as teclas PF (Program Function) do terminal 3270.
 * Cada tecla PF é associada a um número de 1 a 24.
 */
public enum PFKey {
	
    PF1(1), 
    PF2(2), 
    PF3(3), 
    PF4(4),
    PF5(5), 
    PF6(6), 
    PF7(7), 
    PF8(8),
    PF9(9), 
    PF10(10), 
    PF11(11), 
    PF12(12),
    PF13(13), 
    PF14(14), 
    PF15(15), 
    PF16(16),
    PF17(17), 
    PF18(18), 
    PF19(19), 
    PF20(20),
    PF21(21), 
    PF22(22), 
    PF23(23), 
    PF24(24);

    private final int value;

    PFKey(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
