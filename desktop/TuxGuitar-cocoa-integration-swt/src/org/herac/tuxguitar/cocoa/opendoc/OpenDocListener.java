package org.herac.tuxguitar.cocoa.opendoc;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
//import org.eclipse.swt.internal.Callback;
//import org.herac.tuxguitar.cocoa.TGCocoa;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

import java.awt.Desktop;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.io.File;
import java.io.IOException;

public class OpenDocListener {
	
	private class MyOpenFilesHandler implements OpenFilesHandler {
	    public void openFiles(OpenFilesEvent e) {
			System.out.println("MyOpenFilesHandler  !!!!!!!  ()");
			
			// try to open 1st file of list
			if (!e.getFiles().isEmpty()) {
				TuxGuitar.getInstance().getPlayer().reset();
				TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGReadURLAction.NAME);
				try {
					tgActionProcessor.setAttribute(TGReadURLAction.ATTRIBUTE_URL, e.getFiles().get(0).toURI().toURL());
					tgActionProcessor.process();
				} catch (Throwable e1) {
					e1.printStackTrace();
				}

			}
			
			for (File f : e.getFiles()) {
				System.out.printf("file name : %s\n", f.getName());
				try {
					System.out.printf("canonical path : %s\n", f.getCanonicalPath().toString());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

	    }
	}

	
	//public static final long sel_application_openFile_ = TGCocoa.sel_registerName("application:openFile:");
	
	private boolean enabled;
	
	public OpenDocListener(){
		this.enabled = false;
	}
	
	public void OpenMyAppFilesHandler(OpenFilesEvent e) {
		System.out.println("OpenFileHandler()");
	 }


	public void init() throws Throwable{
		if (Desktop.isDesktopSupported()) {
			System.out.println("desktop is supported, setting file handler");
			Desktop.getDesktop().setOpenFileHandler(new MyOpenFilesHandler());
		}

		System.out.println("Test INIT OK");
		
		/*
		long cls = TGCocoa.objc_lookUpClass ("SWTApplicationDelegate");
		if( cls != 0 ){
			Callback callback = TGCocoa.newCallback( this , "callbackProc64" , "callbackProc32", 4 );
			System.out.println("Test INit");
			long callbackProc = TGCocoa.getCallbackAddress( callback );
			if( callbackProc != 0 ){
				TGCocoa.class_addMethod(cls, sel_application_openFile_, callbackProc , "B:@@");
				System.out.println("Test INit OK");
			}
		}*/

		
	}
	/*
	public long callbackProc(long id, long sel,long arg0, long arg1) {
		System.out.println("Test callbackProc");
		if( this.isEnabled() ){
			if (sel == sel_application_openFile_) {
				try {
					String filename = TGCocoa.getNSStringValue(arg1);
					if( filename.length() > 0 ){
						OpenDocAction.saveAndOpen( filename );
					}
				}catch (Throwable throwable){
					throwable.printStackTrace();
				}
			}
		}
		return TGCocoa.noErr;
	}
	
	public long callbackProc64(long id, long sel,long arg0, long arg1) {
		System.out.println("Test callbackProc64");
		return this.callbackProc(id, sel, arg0, arg1);
	}
	
	public int callbackProc32(int id, int sel,int arg0, int arg1) {
		System.out.println("Test callbackProc32");
		return (int)this.callbackProc( (long)id, (long)sel, (long)arg0, (long)arg1);
	}
	
	public boolean isEnabled() {
		System.out.println("Test isEnabled");

		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {	
		this.enabled = enabled;
	}
	*/
}
