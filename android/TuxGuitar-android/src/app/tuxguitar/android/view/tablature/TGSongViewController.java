package app.tuxguitar.android.view.tablature;

import java.util.List;

import app.tuxguitar.android.graphics.TGResourceFactoryImpl;
import app.tuxguitar.android.transport.TGTransport;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.graphics.control.TGController;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGLayoutStyles;
import app.tuxguitar.graphics.control.TGLayoutVertical;
import app.tuxguitar.graphics.control.TGResourceBuffer;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UIResourceFactory;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGSongViewController implements TGController {

	public static final float EMPTY_SCALE = 0f;

	private boolean disposed;
	private TGContext context;
	private UIResourceFactory resourceFactory;
	private TGLayout layout;
	private TGSongViewStyles songStyles;
	private TGSongViewBufferController bufferController;
	private TGSongViewLayoutPainter layoutPainter;
	private TGSongViewAxisSelector axisSelector;
	private TGSongViewSmartMenu smartMenu;
	private TGCaret caret;
	private TGScroll scroll;
	private TGSongView songView;
	private float scalePreview;

	public TGSongViewController(TGContext context) {
		this.context = context;
		this.songStyles = new TGSongViewStyles();
		this.resourceFactory = new TGResourceFactoryImpl();
		this.bufferController = new TGSongViewBufferController(this);
		this.layoutPainter = new TGSongViewLayoutPainter(this);
		this.layout = new TGLayoutVertical(this, TGLayout.DISPLAY_TABLATURE | TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_COMPACT | TGLayout.HIGHLIGHT_PLAYED_BEAT);
		this.caret = new TGCaret(this);
		this.scroll = new TGScroll();
		this.smartMenu = new TGSongViewSmartMenu(this);
		this.axisSelector = new TGSongViewAxisSelector(this);

		this.resetCaret();
		this.resetScroll();
		this.updateTablature();
		this.appendListeners();
	}

	public void appendListeners() {
		TGSongViewEventListener listener = new TGSongViewEventListener(this);
		TGEditorManager.getInstance(this.context).addRedrawListener(listener);
		TGEditorManager.getInstance(this.context).addUpdateListener(listener);
		TGEditorManager.getInstance(this.context).addDestroyListener(listener);
	}

	public void resetCaret() {
		this.getCaret().update(1, TGDuration.QUARTER_TIME, 1);
	}

	public void resetScroll() {
		this.getScroll().getX().reset(false, 0, 0, 0);
		this.getScroll().getY().reset(true, 0, 0, 0);
	}

	public void disposeUnregisteredResources() {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				getResourceBuffer().disposeUnregisteredResources();
			}
		});
	}

	public void updateTablature() {
		this.getLayout().updateSong();
		this.getCaret().update();
		this.disposeUnregisteredResources();
	}

	public void updateMeasures(List<Integer> numbers) {
		this.getLayout().updateMeasureNumbers(numbers);
		this.getCaret().update();
		this.disposeUnregisteredResources();
	}

	public void updateScroll(UIRectangle bounds) {
		this.getScroll().getX().setMaximum(Math.max((this.getLayout().getWidth() - bounds.getWidth()), 0));
		this.getScroll().getY().setMaximum(Math.max((this.getLayout().getHeight() - bounds.getHeight()), 0));
	}

	public void updateSelection() {
		this.bufferController.updateSelection();
		this.layoutPainter.refreshBuffer();
	}

	public void scale(float scale) {
		this.getLayout().loadStyles(scale);
		this.setScalePreview(EMPTY_SCALE);
	}

	public void redraw() {
		if( this.getSongView() != null ) {
			this.getSongView().redraw();
		}
	}

	public void redrawPlayingMode() {
		if( this.getSongView() != null && !this.getSongView().isPainting() && MidiPlayer.getInstance(this.context).isRunning()) {
			this.redraw();
		}
	}

	public TGSongManager getSongManager() {
		return TGDocumentManager.getInstance(getContext()).getSongManager();
	}

	public TGSong getSong() {
		return TGDocumentManager.getInstance(getContext()).getSong();
	}

	public UIResourceFactory getResourceFactory() {
		return this.resourceFactory;
	}

	public TGResourceBuffer getResourceBuffer() {
		return this.bufferController.getResourceBuffer();
	}

	public TGSongViewLayoutPainter getLayoutPainter() {
		return layoutPainter;
	}

	public TGLayoutStyles getStyles() {
		return this.songStyles;
	}

	public TGLayout getLayout() {
		return layout;
	}

	public TGCaret getCaret() {
		return caret;
	}

	public TGContext getContext() {
		return this.context;
	}

	public TGScroll getScroll() {
		return scroll;
	}

	public TGSongViewAxisSelector getAxisSelector() {
		return axisSelector;
	}

	public TGSongViewSmartMenu getSmartMenu() {
		return smartMenu;
	}

	public TGSongView getSongView() {
		return songView;
	}

	public void setSongView(TGSongView tgSongView) {
		this.songView = tgSongView;
	}

	public float getScalePreview() {
		return scalePreview;
	}

	public void setScalePreview(float scalePreview) {
		this.scalePreview = scalePreview;
	}

	public int getTrackSelection() {
		if ((getLayout().getStyle() & TGLayout.DISPLAY_MULTITRACK) == 0) {
			return getCaret().getTrack().getNumber();
		}
		return -1;
	}

	public boolean isRunning(TGBeat beat) {
		return (isRunning(beat.getMeasure()) && TGTransport.getInstance(this.context).getCache().isPlaying(beat.getMeasure(), beat));
	}

	public boolean isRunning(TGMeasure measure) {
		return (measure.getTrack().equals(getCaret().getTrack()) && TGTransport.getInstance(this.context).getCache().isPlaying(measure));
	}

	public boolean isLoopSHeader(TGMeasureHeader measureHeader) {
		MidiPlayerMode pm = MidiPlayer.getInstance(this.context).getMode();
		return (pm.isLoop() && pm.getLoopSHeader() == measureHeader.getNumber());
	}

	public boolean isLoopEHeader(TGMeasureHeader measureHeader) {
		MidiPlayerMode pm = MidiPlayer.getInstance(this.context).getMode();
		return (pm.isLoop() && pm.getLoopEHeader() == measureHeader.getNumber());
	}

	public boolean isScaleActionAvailable() {
		return (!TGEditorManager.getInstance(getContext()).isLocked() && !MidiPlayer.getInstance(getContext()).isRunning());
	}

	public boolean isScrollActionAvailable() {
		return (!TGEditorManager.getInstance(getContext()).isLocked());
	}

	public boolean isDisposed() {
		return this.disposed;
	}

	public void dispose() {
		this.getCaret().dispose();
		this.getLayout().disposeLayout();
		this.getResourceBuffer().disposeAllResources();
		this.disposed = true;
	}

	public static TGSongViewController getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGSongViewController.class.getName(), new TGSingletonFactory<TGSongViewController>() {
			public TGSongViewController createInstance(TGContext context) {
				return new TGSongViewController(context);
			}
		});
	}
}
