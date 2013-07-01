package edu.kit.iti.algo2.textindexing.video;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.javatuples.Pair;
import org.javatuples.Tuple;

public class Video2Text {

    private ConversionOptions co;

    public Video2Text(ConversionOptions co) {
	this.co = co;

	if (!co.videoFile.exists()) {
	    throw new IllegalArgumentException("videoFile does not exists");
	}
    }

    public static List<Pair<Integer, String>> run(ConversionOptions co)
	    throws IOException {
	Video2Text v2t = new Video2Text(co);
	v2t.extractAudio();
	v2t.extractVideo();
	try {
	    return v2t.doOCR();
	} catch (InterruptedException e) {
	    return null;
	}
    }

    private Process execute(String command) throws IOException {
	return Runtime.getRuntime().exec(command);
    }

    public File extractAudio() throws IOException {
	String cmd = co.getCommandExtractAudio();
	Process e = execute(cmd);
	IOUtils.copy(e.getInputStream(), System.out);
	return co.getAudioTargetFile();
    }

    public void extractVideo() throws IOException {
	String cmd = co.getCommandExtractPictures();
	Process e = execute(cmd);
	IOUtils.copy(e.getInputStream(), System.out);
	// return gatherImages();
    }

    public List<Pair<Integer, File>> gatherImages() {
	File picFolder = co.getPictureFolder();
	List<Pair<Integer, File>> pictures = new ArrayList<>();

	for (File pic : picFolder.listFiles()) {
	    String n = pic.getName();
	    int time = Integer.parseInt(n.substring(0, n.indexOf('.')));
	    Pair<Integer, File> p = new Pair<>(time, pic);
	    pictures.add(p);
	}
	return pictures;
    }

    public List<Pair<Integer, String>> doOCR() throws InterruptedException {
	ExecutorService exec = Executors.newCachedThreadPool();
	List<Pair<Integer, String>> result = Collections
		.synchronizedList(new ArrayList<Pair<Integer, String>>());

	for (Pair<Integer, File> i : gatherImages()) {
	    exec.submit(new OCRTask(i, result));
	}

	exec.shutdown();
	exec.awaitTermination(30, TimeUnit.MINUTES);
	return result;
    }

    class OCRTask implements Callable<Pair<Integer, String>> {

	private File file;
	private Integer time;
	private List<Pair<Integer, String>> target;

	public OCRTask(Pair<Integer, File> src,
		List<Pair<Integer, String>> result) {
	    target = result;
	    time = src.getValue0();
	    file = src.getValue1();
	}

	@Override
	public Pair<Integer, String> call() throws Exception {
	    File cfile = File.createTempFile("", ".txt");
	    String cmd = co.getCommandTesserect(file, cfile);
	    Process p = execute(cmd);
	    p.waitFor();
	    String content = FileUtils.readFileToString(cfile);
	    final Pair<Integer, String> e = new Pair<Integer, String>(time,
		    content);
	    target.add(e);
	    return e;
	}
    }
}
