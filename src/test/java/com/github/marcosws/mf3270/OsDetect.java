package com.github.marcosws.mf3270;

import org.junit.jupiter.api.Test;

public class OsDetect {
	
	
	@Test
	public void osDetect() {
		System.out.println("OS Name: " + System.getProperty("os.name"));
	}

}
