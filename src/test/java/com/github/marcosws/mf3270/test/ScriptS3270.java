package com.github.marcosws.mf3270.test;


import org.junit.Test;

import com.github.marcosws.mf3270.S3270Session;
import com.github.marcosws.mf3270.enums.PFKey;
import com.github.marcosws.mf3270.enums.WaitType;

public class ScriptS3270 {

	
	private static S3270Session session;
	
	@Test
	public void testScriptS3270() {
		
		session = new S3270Session();
			
		System.out.println("==== S3270 Session Script =====================");
		System.out.println(session.connect("192.168.15.20", "23", false));
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.waitSeconds(1));
		System.out.println(session.getScreen());
		
		System.out.println("==== S3270 Enter LOGON IBMUSER ================");
		System.out.println(session.sendString("LOGON IBMUSER"));
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());
		System.out.println("==== S3270 Enter ==============================");
		System.out.println(session.enter());
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());
		System.out.println("==== S3270 sendTextByLabel ====================");
		System.out.println(session.sendTextByField("Password  ===>", "SYS1"));
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());
		System.out.println("==== S3270 Enter =============================");
		System.out.println(session.enter());
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());
		System.out.println("==== S3270 Enter =============================");
		System.out.println(session.enter());
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());

		session.waitFor(WaitType.INPUT_FIELD, 3);
		session.sendTextByField("Option ===>", "3");
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());
		System.out.println("==== S3270 Enter ==============================");
		System.out.println(session.enter());
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());
		
		session.waitFor(WaitType.INPUT_FIELD, 3);
		session.sendTextByField("Option ===>", "4");
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());
		System.out.println("==== S3270 Enter ==============================");
		System.out.println(session.enter());
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());
		
		session.waitFor(WaitType.INPUT_FIELD, 3);
		session.sendTextByField("Option ===>", "4");
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());
		System.out.println("==== S3270 Enter ==============================");
		System.out.println(session.enter());
		System.out.println("==== S3270 Screen =============================");
		
		session.waitSeconds(3);
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());
		
		
		System.out.println("==== S3270 PF3 ================================");
		System.out.println(session.pressPF(PFKey.PF3));
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());
		
		System.out.println("==== S3270 PF3 ================================");
		System.out.println(session.pressPF(PFKey.PF3));
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());
		
		System.out.println("==== S3270 PF3 ================================");
		System.out.println(session.pressPF(PFKey.PF3));
		System.out.println("==== S3270 Screen =============================");
		System.out.println(session.asciiScreen());

		
	
		System.out.println("==== S3270 Disconnect =========================");
		System.out.println(session.disconnect());
		System.out.println("==== S3270 Wait Disconnect =========================");
		System.out.println(session.waitFor(WaitType.DISCONNECT, 3));
		System.out.println("==== S3270 Quit ===============================");
		System.out.println(session.quit());
			
			
	}

}
