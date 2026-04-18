package com.github.marcosws.mf3270.utils.viewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.marcosws.mf3270.enums.EmulatorMode;
import com.github.marcosws.mf3270.utils.component.ScreenContent;

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
		if (emulatorMode == EmulatorMode.TERMINAL_VIEWER || emulatorMode == EmulatorMode.TERMINAL_VIEWER_WITH_LOG_DEBUG) {
			terminalViewer = new TerminalViewer();
			terminalViewer.show();
		}
	}
	
	protected void updateScreenViewer() {
		
		ScreenContent screenContent = this.getScreenContent();
		switch (emulatorMode) {
			case HEADLESS_WITH_LOG_DEBUG:
				logger.debug("Current screen:\n{}", screenContent.getScreen());
				break;
			case TERMINAL_VIEWER_WITH_LOG_DEBUG:
				logger.debug("Current screen:\n{}", screenContent.getScreen());
				 // Intencionalmente sem break para reutilizar a lógica de atualização do Terminal Viewer
			case TERMINAL_VIEWER:
				this.terminalViewer.updateScreen(screenContent.getScreen());
				break;
			case HEADLESS_ONLY:
				return;
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
