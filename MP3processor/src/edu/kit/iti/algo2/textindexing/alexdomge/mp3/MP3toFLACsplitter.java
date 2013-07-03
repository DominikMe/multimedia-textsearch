package edu.kit.iti.algo2.textindexing.alexdomge.mp3;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

import com.google.code.mp3fenge.Mp3Fenge;

public class MP3toFLACsplitter {

	private static final String VLC = "C:/Program Files (x86)/VideoLAN/VLC/vlc.exe";
	
	static final FilenameFilter MP3FILTER = new FilenameFilter() {
		@Override
		public boolean accept(File arg0, String arg1) {
			return arg1.endsWith(".mp3");
		}
	};

	public void split(String mp3, String outputDir) {

		Mp3Fenge splitter = new Mp3Fenge(new File(mp3));
		new File(outputDir).mkdir();
		int durationSec = getTrackLength(mp3);
		System.out.println("mp3 is " + durationSec + " sec long.");
		int durationMs = durationSec * 1000;
		int fragmentMsSize = 20 * 1000;
		int overlapMs = 1000;
		int i = 0;
		for (int time = 0; time < durationMs; time += (fragmentMsSize - overlapMs)) {
			splitter.generateNewMp3ByTime(
					new File(String.format("%s/fragment%02d.mp3", outputDir, i)),
					time, time + Math.min(fragmentMsSize, durationMs - time));
			i++;
		}
	}

	public int getTrackLength(String mp3File) {
		MP3File mp3;
		try {
			mp3 = new MP3File(mp3File);
			MP3AudioHeader header = (MP3AudioHeader) mp3.getAudioHeader();
			return header.getTrackLength();
		} catch (IOException | TagException | ReadOnlyFileException
				| InvalidAudioFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public void deleteMP3chunks(String directory) {
		File dir = new File(directory);
		for (File f : dir.listFiles(MP3FILTER)) {
			f.delete();
		}
	}

	public void convertToFLAC(String directory) {
		File dir = new File(directory);
		int i = 0;
		for (String f : dir.list(MP3FILTER)) {
			if (++i > 10)
				break;
			convertMP3toFLAC(dir + "/" + f);
		}
	}

	private void convertMP3toFLAC(String file) {
		String vlc = VLC;
		ProcessBuilder pb = new ProcessBuilder(vlc, file, "-I dummy",
				"--sout=\"#transcode{acodec=flac,samplerate=16000}"
						+ String.format(
								":std{access=file,mux=raw,dst=\"%s.flac\"}\" ",
								file.substring(0, file.length() - 4)),
				"vlc://quit");
		for (String s : pb.command()) {
			System.out.println(s);
		}
		Process proc;
		try {
			proc = pb.start();
			proc.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		final String outdir = "testdata/cre175_hackerbrausen_fragments";
		final String mp3 = "testdata/cre175_hackerbrausen.mp3";
		
		MP3toFLACsplitter splitter = new MP3toFLACsplitter();
		// System.out.println(String.format("Print %s to the console.", "FOO"));
		// splitter.split(mp3, outdir);
		splitter.convertToFLAC(outdir);
		splitter.deleteMP3chunks(outdir);
	}

}
