package com.github.marcosws.mf3270.test;

import com.github.marcosws.mf3270.exceptions.S3270SessionException;

public class Teste {

	public static void main(String[] args) {
		
		System.out.println("Test: " + (-1 >= 0 ? "true" : "false"));
		
		String label = "Password  ===>"; 
		String text = "SYS1";
		int offsetRow = 0;
		int offsetCol = -3;
		String screen = "";
		
		screen =  "data:  ------------------------------- TSO/E LOGON -----------------------------------\n"
				+ "data:                                                                                 \n"
				+ "data:                                                                                 \n"
				+ "data:     Enter LOGON parameters below:                   RACF LOGON parameters:      \n"
				+ "data:                                                                                 \n"
				+ "data:     Userid    ===> IBMUSER                                                      \n"
				+ "data:                                                                                 \n"
				+ "data:     Password  ===>                                  New Password ===>           \n"
				+ "data:                                                                                 \n"
				+ "data:     Procedure ===> DBSPROC                          Group Ident  ===>           \n"
				+ "data:                                                                                 \n"
				+ "data:     Acct Nmbr ===> ACCT#                                                        \n"
				+ "data:                                                                                 \n"
				+ "data:     Size      ===> 4096                                                         \n"
				+ "data:                                                                                 \n"
				+ "data:     Perform   ===>                                                              \n"
				+ "data:                                                                                 \n"
				+ "data:     Command   ===> ispf                                                         \n"
				+ "data:                                                                                 \n"
				+ "data:     Enter an 'S' before each option desired below:                              \n"
				+ "data:             -Nomail         -Nonotice        -Reconnect        -OIDcard         \n"
				+ "data:                                                                                 \n"
				+ "data:  PF1/PF13 ==> Help    PF3/PF15 ==> Logoff    PA1 ==> Attention    PA2 ==> Reshow\n"
				+ "data:  You may request specific help information by entering a '?' in any entry field \n"
				+ "U F U C(192.168.15.20) I 4 24 80 7 19 0x0 0.001\n"
				+ "ok\n";
		
		int targetRow = 0;
		int targetCol = 0;

		int[] pos = findField(screen.replaceAll("data:", ""), label);
		
		targetRow = (offsetRow >= 0 ? Math.abs(pos[0] - 1) + Math.abs(offsetRow) : Math.abs(pos[0] - 1) - Math.abs(offsetRow));
		targetCol = (offsetCol >= 0 ? Math.abs(pos[1] - 1) + Math.abs(offsetCol) : Math.abs(pos[1] - 1) - (label.length() + Math.abs(offsetCol)));

		System.out.println("Posição do campo Row: " + targetRow + ", Col: " + targetCol);


	}
	
	public static int[] findField(String screen, String label) {
		String[] lines = screen.split("\n");
		for (int i = 0; i < lines.length; i++) {
			int col = lines[i].indexOf(label);
			if (col >= 0) {    
				return new int[]{i + 1, col + label.length() + 1}; 
			}
		}
		throw new S3270SessionException("Label '" + label + "' not found in screen.");
	}

}
