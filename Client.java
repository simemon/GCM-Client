package com;

import java.util.*;
import java.io.*;
import java.net.*;
import java.net.ProtocolException;

//import org.apache.http.HttpResponse;
import org.apache.http.*;
import org.apache.http.util.EntityUtils;


public class Client {

	String device_id;
	String api_id;
	String Msg;
	String ResultJSON;
	
	private String GCM_URI = "https://android.googleapis.com/gcm/send";
	
	HttpURLConnection gcmRequest = null;
	HttpResponse gcmResponse = null;
	
	Client()
	{
		//default constructor...do nothing
	}
	
	Client(String dev, String auth)
	{
		this.device_id = dev;
		this.api_id = auth;
		
	}
	
	public String Send(String message) throws IOException
    {
        // Escape condition
        if (device_id == null || api_id == null)
        {
            return "[ERROR] Device Token or API Key has not been set";
        }

        InitGCMClient();
        PostPayload(message);
        	
        try {
        	System.out.println(gcmRequest);
			ResultJSON = gcmRequest.getResponseMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return ResultJSON;
    }
	
	public String ReadResponse(HttpResponse response)
    {
        //StreamReader responseReader = new StreamReader(response.GetResponseStream());
        //return responseReader.ReadToEnd();
        
        try {
			String responseString = EntityUtils.toString(response.getEntity());
			return responseString;
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
	
	private void InitGCMClient()
    {
		
		URL obj = null;
		try {
			obj = new URL(GCM_URI);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			gcmRequest = (HttpURLConnection) obj.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		gcmRequest.setRequestProperty("Content-Type", "application/json");
		gcmRequest.setRequestProperty("User-Agent", "Android GCM Message Sender Client 1.0");
		gcmRequest.setRequestProperty("Authorization", "key=" + api_id);
		
		try {
			gcmRequest.setRequestMethod("POST");
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        // Credential info
        // gcmRequest.Headers.Add("Authorization", "key=" + APIKey);
    }
	
	public void PostPayload(String message) throws IOException
    {
        String payloadString = AssembleJSONPayload(device_id, message);        
        byte[] payloadByte = payloadString.getBytes("UTF-8");
        
        //gcmRequest.ContentLength = payloadByte.length;
        String len = new String(payloadByte);
        gcmRequest.setRequestProperty("Content-Length", len);
        
        gcmRequest.setDoOutput(true);
        gcmRequest.setDoInput(true);
        
        OutputStream payloadStream = gcmRequest.getOutputStream();
        
        payloadStream.write(payloadByte, 0, payloadByte.length);
        payloadStream.close();
        
    }
	
	public String AssembleJSONPayload(String gcmDeviceToken, String gcmBody)
    {
	
		String payloadFormatJSON =
		        "{" +
		                "\"registration_ids\" : [\"" + gcmDeviceToken + "\"]," +
		                "\"data\" : {" +
		                " " + gcmBody + " " +
		                "}" +
		                "}";
		
        String payload = String.format(payloadFormatJSON, gcmDeviceToken, gcmBody);
        System.err.println("payload : " + payload);
        return payload;
    }
		
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String dev = "<Your device ID>";
		String auth = "<Your auth Id>";
		Client  cl = new Client(dev, auth);
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			
			System.out.println("\nSend your msg\n");
			String msg = sc.nextLine();
			cl.Send(msg);
		}
	}
}
