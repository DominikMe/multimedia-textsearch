package edu.kit.iti.algo2.textindexing.gui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class PreviewDialogVlc extends JDialog {

	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private String file;

	static {
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),
				"C:/Program Files/VideoLAN/VLC");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	}

	public PreviewDialogVlc(String multimediaFile) {
		this.file = multimediaFile;

		setLayout(new BorderLayout());
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		add(mediaPlayerComponent);
	}

	public void start() {

		MediaPlayer mp = mediaPlayerComponent.getMediaPlayer();
		mp.playMedia(this.file, ":start-time=30");
		mp.play();
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				String file = "C:/Users/weigla/Desktop/Akte X Der Film.avi";
				java.io.File f = new java.io.File(file);
				System.out.println(f.exists());

				if (args.length > 0) {
					file = args[0];
				}

				file = "file://" + file;
				PreviewDialogVlc dialogVlc = new PreviewDialogVlc(file);
				dialogVlc.setSize(400, 400);
				dialogVlc.setVisible(true);

				dialogVlc.mediaPlayerComponent.getMediaPlayer()
						.addMediaPlayerEventListener(
								new MediaPlayerEventAdapter() {
									@Override
									public void error(MediaPlayer mediaPlayer) {
										System.out.println(mediaPlayer);
									};
								});

				dialogVlc.mediaPlayerComponent.getMediaPlayer()
						.startMedia(file);

			}
		});
	}
}
