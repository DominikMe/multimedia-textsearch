import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//import javax.net.ssl.HttpsURLConnection;

public class Main {


	public static void main(String[] args) throws Exception {

		final String LANG_DE = "de-de";
		final String LANG_EN = "en-us";
		final String AUDIOFILE = "fragment13.flac";
		final String AUDIOFILE2 = "test2.flac";
		final int RATE = 16000;//48000;

		String url = "http://www.google.com/speech-api/v1/recognize?"
				+ "xjerr=1&client=G&lang=" + LANG_DE + "&maxresults=10";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setInstanceFollowRedirects(false);
		con.setConnectTimeout(60000);

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "audio/x-flac; rate=" + RATE);
		con.setRequestProperty("User-Agent", "G");

		// Send post request

		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		Path path = Paths.get(AUDIOFILE);
		byte[] data = Files.readAllBytes(path);
		System.out.println(data.length);
		wr.write(data);

		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("Response Code : " + responseCode);

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

	}

}