package net.floodlightcontroller.iotfilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.projectfloodlight.openflow.types.DatapathId;

import sun.net.www.http.HttpClient;
//import org.apache.http.HttpResponse;

public class IoTStatisticsCollector {
	
	
	//DatapathId dpid, long sensorId
	public static String getCaputeredPackets() throws IOException{
		String url = "http://127.0.0.1:8080/wm/core/switch/all/flow/json";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		//con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		
		return response.toString(); 
	}
}
