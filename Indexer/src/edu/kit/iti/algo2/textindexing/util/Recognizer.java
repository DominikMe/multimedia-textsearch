package edu.kit.iti.algo2.textindexing.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
//import java.util.Map;

import org.json.simple.*;

public class Recognizer {
	
	final String url = "http://www.google.com/speech-api/v1/recognize?" +
			"xjerr=1&client=G&lang=en-US";
	
	//timeout for connection
	final int timeout = 60000;
	final String filetype = "audio/x-flac";
	final String client_name = "text_indexing";
	
	//sample rate -1 for automatically detecting sample rate
	// noch nicht implementiert
	int rate;
	int max_result;
	
	public Recognizer() {
		rate = -1; 
		max_result = 5;
	}
	
	public Recognizer(int rate) {
		this();
		this.rate = rate;
	}
	
	//sample rate & number of results returned
	public Recognizer(int rate, int max_result) {
		this(rate);
		this.max_result = max_result;
	}
	
	//receive a file name, return a list or results with confidential 
	//parameter is the absolute filename (include path)
	public List<String> speech2text(String audio) {
		List<String> transcripts = new ArrayList<String>();
		try {
			URL obj = new URL(url + "&maxresults=" + max_result);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setInstanceFollowRedirects(false);
			con.setConnectTimeout(timeout);

			//add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", filetype + "; rate=" + rate);
			con.setRequestProperty("User-Agent", client_name);
		 
			// Send post request
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			Path path = Paths.get(audio);
			byte[] data = Files.readAllBytes(path);
			wr.write(data);
				
			wr.flush();
			wr.close();
		 
			int responseCode = con.getResponseCode();
			if (!("" + responseCode).startsWith("20")) {
				System.out.println("Error response Code : " + responseCode);
			}
				
			BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
		 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			JSONObject json = (JSONObject)JSONValue.parse(response.toString());
			for (Object hyp: (JSONArray)json.get("hypotheses")) {
				transcripts.add((String)((JSONObject)hyp).get("utterance"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return transcripts;
	}
	
	public static void main(String[] args) {
		Recognizer test = new Recognizer(48000);
		for (String s:test.speech2text("/home/easy0924/Workspace/multimedia-textsearch/google_demo/test2.flac")) {
			System.out.println("Hyp: ");
			System.out.println(s);
		}
	}
}
