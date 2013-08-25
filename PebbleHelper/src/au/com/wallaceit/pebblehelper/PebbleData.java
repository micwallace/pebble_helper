package au.com.wallaceit.pebblehelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

public class PebbleData {
	private DefaultHttpClient httpclient;
    static JSONObject jObj = null;
    static String json = "";
    public static final UUID QUOTES_UUID = UUID.fromString("7209a7d5-f36b-4253-a8db-e882e056606c");
    public static final int CMD_KEY = 0x00;
    public static final int CMD_REFRESH = 0x01;
    public static final int QUOTE_TXT = 0x05;
    public static final int QUOTE_SRC = 0x06;
    public static final int QUOTE_CLEAR = 0x07;
    public static final int QUOTE_END = 0x08;
	
	public boolean isgettingquote = false;
    PebbleData(){
	}
    
    public void updateQuote(boolean clear, final Context context){
		class NewQuoteTask extends AsyncTask<Boolean, Void, Void> {
		    protected Void doInBackground(Boolean... clear) {
		    	JSONObject jsonobj = getJSONFromUrl("http://www.iheartquotes.com/api/v1/random?format=json&max_characters=300");
		    	
		    	String quote;
		    	String qsrc;
		    	try {
		    		quote = jsonobj.getString("quote");
		    		qsrc = jsonobj.getString("source");
		    	} catch (JSONException e) {
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
		    		quote = "error getting web data :(";
		    		qsrc = "";
		    	}
		    	System.out.println(quote+" LENGTH:"+quote.length());
		    	// send quote to pebble
		    	// Build up a Pebble dictionary containing the weather icon and the current temperature in degrees celsius
		    	PebbleDictionary data = new PebbleDictionary();
		    	if (clear[0]){
		    		data.addInt32(QUOTE_CLEAR, QUOTE_CLEAR);
		    	}
		    	data.addString(QUOTE_TXT, quote.substring(0, (quote.length()>100?100:quote.length())));
		    	//data.addString(QUOTE_SRC, qsrc);
		    	// Send the assembled dictionary to the weather watch-app; this is a no-op if the app isn't running or is not
		    	// installed
		    	PebbleKit.sendDataToPebble(context, QUOTES_UUID, data);
		    	System.out.println("Message sent: data");
		    	if (quote.length()>100){
		    		String[] bits = splitstr(quote.substring(100), 100);
		    		for (int i=0; i<bits.length; i++){
		    			data = new PebbleDictionary();
		    			data.addString(QUOTE_TXT, bits[i]);
		    			if (i == (bits.length-1)){
		    				data.addInt32(QUOTE_END, QUOTE_END);
		    				System.out.println("last message"); // last message
		    			}
		    			try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    			PebbleKit.sendDataToPebble(context, QUOTES_UUID, data);
		    			System.out.println("Message sent: extra-data "+i);
		    		}
		    	}
		    		
		    	isgettingquote = false;
		    	return null;
		    }
		}
		NewQuoteTask task = new NewQuoteTask();
		task.execute(clear);
	}
    
    // Create Http/s client
 	private DefaultHttpClient createHttpClient(){
 	    HttpParams params = new BasicHttpParams();
 	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
 	    HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
 	    HttpProtocolParams.setUseExpectContinue(params, true);
 	    HttpConnectionParams.setConnectionTimeout(params, 12000);
 	    HttpConnectionParams.setSoTimeout(params, 10000);
 	    SchemeRegistry schReg = new SchemeRegistry();
 	    schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
 	    schReg.register(new Scheme("https", org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory(), 443));
 	    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
 	    return new DefaultHttpClient(conMgr, params);
 	}
    
 	// HTTP Get Request
 	public JSONObject getJSONFromUrl(String url) {
 		// create null object to return on errors
 		jObj = new JSONObject();
 		// create client if null
 		if (httpclient == null){
 			httpclient = createHttpClient();
 		}
 		InputStream is;
         // Making HTTP request
         try {
             HttpGet httpget = new HttpGet(url);
             HttpResponse httpResponse = httpclient.execute(httpget);
             HttpEntity httpEntity = httpResponse.getEntity();
             is = httpEntity.getContent();          
  
         } catch (UnsupportedEncodingException e) {
         	e.printStackTrace();
         	return jObj;
         } catch (ClientProtocolException e) {
         	e.printStackTrace();
         	return jObj;
         } catch (IOException e) {
         	e.printStackTrace();
         	return jObj;
         }
         // read data
         try {
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                     is, "iso-8859-1"), 8);
             StringBuilder sb = new StringBuilder();
             String line = null;
             while ((line = reader.readLine()) != null) {
                 sb.append(line + "\n");
             }
             is.close();
             json = sb.toString();
         } catch (Exception e) {
             //Log.e("Buffer Error", "Error converting result " + e.toString());
         	e.printStackTrace();
         	return jObj;
         }
         // try parse the string to a JSON object
         try {
             jObj = new JSONObject(json);
         } catch (JSONException e) {
             //Log.e("JSON Parser", "Error parsing data " + e.toString());
         	e.printStackTrace();
         	return jObj;
         }
         System.out.println("Download complete");
         // return JSON String
         return jObj;
     }
 	
 	public static String[] splitstr(String src, int len) {
	    String[] result = new String[(int)Math.ceil((double)src.length()/(double)len)];
	    for (int i=0; i<result.length; i++)
	        result[i] = src.substring(i*len, Math.min(src.length(), (i+1)*len));
	    return result;
	}
}
