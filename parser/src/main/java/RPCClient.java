
import java.io.IOException;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;

public class RPCClient {

    private JSONObject invokeRPC(String id, String method, List<String> params) {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("method", method);
        if (null != params) {
            JSONArray array = new JSONArray();
            array.addAll(params);
            json.put("params", params);
        }
        JSONObject responseJsonObj = null;
        try {

            int port = Integer.valueOf(Main.properties.getProperty("rpcPort"));
            String server = String.valueOf(Main.properties.getProperty("server"));
            httpclient.getCredentialsProvider().setCredentials(new AuthScope(server, port),
                    new UsernamePasswordCredentials(Main.properties.getProperty("rpcUser"), Main.properties.getProperty("rpcPassword")));
            StringEntity myEntity = new StringEntity(json.toJSONString());
//            System.out.println(json.toString());

            String url = "http://"+server+":"+ String.valueOf(port);

            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(myEntity);





//            System.out.println("executing request" + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

//            System.out.println("----------------------------------------");
//            System.out.println(response.getStatusLine());
            if (entity != null) {
//                System.out.println("Response content length: " + entity.getContentLength());
                // System.out.println(EntityUtils.toString(entity));
            }
            JSONParser parser = new JSONParser();
            responseJsonObj = (JSONObject) parser.parse(EntityUtils.toString(entity));
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
        return responseJsonObj;
    }

    public JSONObject getInfo(String command, List<String> params) {
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), command, params);
        JSONObject ja = new JSONObject();
        // populate the array
        ja.put("result",json.get("result"));
        return ja;
    }
}