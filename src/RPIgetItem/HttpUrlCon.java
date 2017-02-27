package RPIgetItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class HttpUrlCon {
	
	private final String APPKEY = "AIzaSyDPtZaudeIDJ0nkWN4jNXgRxmBCBrDLiug";
	private final String ENGINE_ID = "003189030946525348290:0qt1aedkh78";

	private final String USER_AGENT = "Mozilla/5.0";
	
	private static HttpUrlCon instance;
	
	
	public HttpUrlCon() {
	}

	public static HttpUrlCon getInstance() {
		if (HttpUrlCon.instance == null) {
			HttpUrlCon.instance = new HttpUrlCon();
		}
		return HttpUrlCon.instance;
	}


	/*public String getProduktName() throws Exception {

		return sendRequest("9006900207182");
	}*/
	
	
	// HTTP GET request
	public String getProduktName(long barcode) throws Exception {
		
		String url = "https://www.googleapis.com/customsearch/v1?key="+APPKEY+"&cx="+ENGINE_ID+"&q="+barcode;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		//System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return jsonToObject(response.toString());

	}
	
	// HTTP GET request
	public void sendImage(String parameter) throws Exception {

		String taskcode = parameter.substring(0,3);
		String param = parameter.substring(3,parameter.length());


		try {

			Client client = Client.create();

			WebResource webResource = client
			   .resource("http://176.28.55.242:8080/Webserver/rest/hello/sendPics");



			ClientResponse response = webResource.type("application/json")
			   .post(ClientResponse.class, param);

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
				     + response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		  } catch (Exception e) {

			e.printStackTrace();

		  }

	}

	
	public String jsonToObject(String jSON)
	{
		//System.out.println(jSON.toString());
		String produktName = "";
				
		//Parsing the JsonObject to get the Productname
		try{
		JsonElement jelement = new JsonParser().parse(jSON);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    JsonArray array = jobject.getAsJsonArray("items");
	    JsonObject object = array.get(0).getAsJsonObject();
	    jobject = object.getAsJsonObject("pagemap");
	    array = jobject.getAsJsonArray("metatags");
	    object = array.get(0).getAsJsonObject();   
	    produktName =  object.get("og:title").toString();
		}catch(NullPointerException e){
			//System.out.println("Product not Found online");
		}
		//System.out.println("Name: "+produktName);
	    
	    return produktName;
 	    
		
	}

}
