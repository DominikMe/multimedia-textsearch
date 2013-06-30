package edu.kit.iti.algo2.textindexing.video;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

public class ConversionOptions {
    public static final String COMMAND_EXTRACT_AUDIO = ("ffmpeg -i ${src} -vn -ar 44100 -ac 2 -ab 192 -f wav {out}.wav");
    public static final String COMMAND_EXTRACT_VIDEO = ("ffmpeg -y -i ${src} -r ${framerate}  -f image2 ${dir}/%07d.png");
    public static final String COMMAND_TESSERACT = ("tesseract ${input} ${out} -l ${lang} -psm ${psm}");

    static File tesserectExecutable;
    static File ffmpegExecutable;

    File videoFile;
    File pictureFolder;
    File audioTargetFile;
    File targetTimedDocument;

    float framerate = 1 / 50f;
    int psm = 3;
    String language = "eng";

    public ConversionOptions(File videoFile, File targetTimedDocument)
	    throws IOException {
	this.videoFile = videoFile;
	this.targetTimedDocument = targetTimedDocument;

	pictureFolder = Files.createTempDirectory("picture").toFile();
	audioTargetFile = Files.createTempFile("audio", ".flac").toFile();

	System.out.println("Created temp folder: " + pictureFolder);
	System.out.println("Created audio file: " + audioTargetFile);
    }

    public float getFramerate() {
	return framerate;
    }

    public void setFramerate(float framerate) {
	this.framerate = framerate;
    }

    public File getVideoFile() {
	return videoFile;
    }

    public void setVideoFile(File audioFile) {
	this.videoFile = audioFile;
    }

    public File getPictureFolder() {
	return pictureFolder;
    }

    public void setPictureFolder(File pictureFolder) {
	this.pictureFolder = pictureFolder;
    }

    public File getAudioTargetFile() {
	return audioTargetFile;
    }

    public void setAudioTargetFile(File audioTargetFile) {
	this.audioTargetFile = audioTargetFile;
    }

    public int getPsm() {
	return psm;
    }

    public void setPsm(int psm) {
	this.psm = psm;
    }

    public String getLanguage() {
	return language;
    }

    public void setLanguage(String language) {
	this.language = language;
    }

    public StrSubstitutor substitue() {
	Map<String, String> map = mapping();
	StrSubstitutor str = new StrSubstitutor(map, "%(", ")");
	return str;
    }

    Map<String, String> mapping() {
	Map<String, String> map = new HashMap<>();
	map.put("videoFile", videoFile.getAbsolutePath());
	map.put("pictureFile", pictureFolder.getAbsolutePath());
	map.put("audioTargetFile", audioTargetFile.getAbsolutePath());
	map.put("framerate", "" + framerate);
	map.put("framerate", "" + psm);
	map.put("language", language);
	
	map.put("tesserect", tesserectExecutable.getAbsolutePath());
	map.put("ffmpeg", ffmpegExecutable.getAbsolutePath());
	return map;
    }

    public String getCommandExtractAudio() {
	return substitue().replace(COMMAND_EXTRACT_AUDIO);
    }

    public String getCommandExtractPictures() {
	return substitue().replace(COMMAND_EXTRACT_VIDEO);
    }

    public String getCommandTesserect(File picture, File target) {
	Map<String, String> map = mapping();
	map.put("picture", picture.getAbsolutePath());
	map.put("target", target.getAbsolutePath());
	StrSubstitutor str = new StrSubstitutor(map, "%(", ")sF");
	return str.replace(COMMAND_TESSERACT);
    }
}
