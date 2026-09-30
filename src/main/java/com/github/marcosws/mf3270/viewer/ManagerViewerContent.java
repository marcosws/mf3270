package com.github.marcosws.mf3270.viewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.marcosws.mf3270.components.ScreenContent;
import com.github.marcosws.mf3270.enums.EmulatorMode;
import com.github.marcosws.mf3270.interfaces.IScreenContent;

public abstract class ManagerViewerContent implements IScreenContent {
	
	private static final Logger logger = LoggerFactory.getLogger(ManagerViewerContent.class);
	
	private TerminalViewer terminalViewer;
	private EmulatorMode emulatorMode;
	
	public EmulatorMode getEmulatorMode() {
		return emulatorMode;
	}

	public ManagerViewerContent(EmulatorMode mode) {
		this.emulatorMode = mode;
	}
	
	protected void showViewer() {
		if (emulatorMode == EmulatorMode.VIEWER_MODE) {
			terminalViewer = new TerminalViewer();
			terminalViewer.show();
		}
	}
	
	protected void updateScreenViewer() {
		
		ScreenContent screenContent = this.getScreenContent();
		switch (emulatorMode) {
			case HEADLESS_MODE:
				logger.debug("Current screen:\n{}", screenContent.getContent());
				break;
			case VIEWER_MODE:
				logger.debug("Current screen:\n{}", screenContent.getContent());
				this.terminalViewer.updateScreen(screenContent.getContent());
				break;
			default:
				throw new IllegalArgumentException("Modo de emulador desconhecido: " + emulatorMode);
		}
		
	}
	
	protected void closeViewer() {
		if (terminalViewer != null) {
			terminalViewer.close();
			terminalViewer = null;
		}
	}
	

}
