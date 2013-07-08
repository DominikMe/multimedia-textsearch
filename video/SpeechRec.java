import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SpeechRec {
	final static int maxTries = 100;

	public static void main(String[] args) throws Exception {

		String language = "en-us";
		if(args.length > 1)
			language = args[1];
		final int RATE = 16000;

		googleSpeechRecognition(language, args[0], RATE);
	}

	private static void googleSpeechRecognition(final String language,
			final String audiofile, final int rate)
			throws MalformedURLException, IOException, ProtocolException {
		int i = 0;
		while (!requestGoogle(language, audiofile, rate) && i < maxTries)
			i++;
	}

	private static boolean requestGoogle(final String language,
			final String audiofile, final int rate)
			throws MalformedURLException, IOException, ProtocolException {
		String url = "http://www.google.com/speech-api/v1/recognize?"
				+ "xjerr=1&client=G&lang=" + language + "&maxresults=1";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setInstanceFollowRedirects(false);
		con.setConnectTimeout(60000);

		// add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "audio/x-flac; rate=" + rate);
		con.setRequestProperty("User-Agent", "G");

		// Send post request

		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		Path path = Paths.get(audiofile);
		byte[] data = Files.readAllBytes(path);
		// System.out.println(data.length);
		wr.write(data);

		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		// System.out.println("Response Code : " + responseCode);
		if (responseCode != 200)
			return false;

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());
		return true;
	}

}