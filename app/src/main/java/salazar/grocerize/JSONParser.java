package salazar.grocerize;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Salazar on 24-11-2016.
 */
public class JSONParser {
    String charset = "UTF-8";
    HttpURLConnection conn;
    DataOutputStream wr;
    StringBuilder result;
    URL urlObj;
    JSONArray jArray = null;
    JSONArray jsonArrayRet=null;
    StringBuilder sbParams;
    String paramsString;

    public JSONArray makeHttpRequest(String url, String method,
                                      String params) {

        sbParams = new StringBuilder();
        /*
        int i = 0;
        for (String key : params[])) {
            try {
                if (i != 0){
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), charset));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }
        */

        if (method.equals("POST")) {
            // request method is POST
            try {
                urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.connect();
                paramsString = sbParams.toString();
                wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(paramsString);
                wr.flush();
                wr.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("GET")){

            if (sbParams.length() != 0) {
                url += "?" + sbParams.toString();
            }

            try {
                urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setConnectTimeout(15000);
                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.d("JSON Parser", "result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.disconnect();

        try {
            jArray = new JSONArray(result.toString());
            jsonArrayRet=jArray.getJSONArray(0);
            /*
            for(int i=0; i < jArray.length(); i++) {

                JSONObject jObject = jArray.getJSONObject(i);
                Iterator<String> keys=jObject.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    String value = jObject.getString(key);
                    Log.d("JSON Parser", key + value);
                }

            }*/
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jsonArrayRet;
    }
}
