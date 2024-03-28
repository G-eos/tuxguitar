package org.herac.tuxguitar.cocoa.opendoc;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

import java.awt.Desktop;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OpenDocListener {
	
	private BufferedWriter debugWriter = null;
	
	public class MyOpenFilesHandler implements OpenFilesHandler {
	    public void openFiles(OpenFilesEvent e) {
			debugLog("MyOpenFilesHandler event received:\n");
			debugLog(e.toString() + "\n");
			// try to open 1st file of list
			if (!e.getFiles().isEmpty()) {
				debugLog("event is not empty\n");
				TuxGuitar.getInstance().getPlayer().reset();
				TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGReadURLAction.NAME);
				try {
					tgActionProcessor.setAttribute(TGReadURLAction.ATTRIBUTE_URL, e.getFiles().get(0).toURI().toURL());
					debugLog("launching actionProcessor for file: " + e.getFiles().get(0).getName() + "\n");
					tgActionProcessor.process();
					debugLog("launched actionProcessor\n");
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
	    }
	}
	
	public OpenDocListener(){
		try {
			this.debugWriter = new BufferedWriter(new FileWriter("/tmp/tuxguitar-opendoc-debug.log"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		debugLog("----------\n");
		if (Desktop.isDesktopSupported()) {
			debugLog("desktop is supported, setting openFileHandler\n");
			Desktop.getDesktop().setOpenFileHandler(new MyOpenFilesHandler());
		}
		debugLog("OpenDocListener created\n");
	}
	
	private void debugLog(String str) {
		try {
			this.debugWriter.append(str);
			this.debugWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			this.debugWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
