package app.tuxguitar.app.view.dialog.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.player.base.MidiDevice;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGChannelSettingsHandlerManager {

	private TGContext context;
	private List<TGChannelSettingsHandler> channelSettingsHandler;

	public TGChannelSettingsHandlerManager(TGContext context){
		this.context = context;
		this.channelSettingsHandler = new ArrayList<TGChannelSettingsHandler>();
	}

	public void addChannelSettingsHandler(TGChannelSettingsHandler handler){
		if(!this.channelSettingsHandler.contains( handler ) ){
			this.channelSettingsHandler.add( handler );
		}
	}

	public void removeChannelSettingsHandler(TGChannelSettingsHandler handler){
		if( this.channelSettingsHandler.contains( handler ) ){
			this.channelSettingsHandler.remove( handler );
		}
	}

	public TGChannelSettingsHandler findSupportedChannelSettingsHandler(){
		MidiDevice midiDevice = getMidiDevice();

		Iterator<TGChannelSettingsHandler> it = this.channelSettingsHandler.iterator();
		while( it.hasNext() ){
			TGChannelSettingsHandler channelSettingsHandler = (TGChannelSettingsHandler)it.next();
			if( channelSettingsHandler.isMidiDeviceSupported(midiDevice) ){
				return channelSettingsHandler;
			}
		}
		return null;
	}

	public TGChannelSettingsDialog findChannelSettingsDialog(TGChannel channel) {
		TGChannelSettingsHandler channelSettingsHandler = findSupportedChannelSettingsHandler();
		if( channelSettingsHandler != null ){
			return channelSettingsHandler.createChannelSettingsDialog(getMidiDevice() ,channel, getSong());
		}
		return null;
	}

	public boolean isChannelSettingsHandlerAvailable(){
		return ( findSupportedChannelSettingsHandler() != null );
	}

	private MidiDevice getMidiDevice(){
		return MidiPlayer.getInstance(this.context).getOutputPort();
	}

	private TGSong getSong(){
		return TGDocumentManager.getInstance(this.context).getSong();
	}
}
