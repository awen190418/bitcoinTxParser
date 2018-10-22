import com.fasterxml.jackson.databind.ObjectMapper;
import config.Config;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.bitcoinj.core.Block;

public class Main {
    public static Properties properties;

    public static void main(String args[]){
        Config c = new Config();
        Main.properties=c.getConfig();


        System.out.println(properties.get("rpcUser"));
        System.out.println("Hello World Gradle JAVA");
        RPCClient r = new RPCClient();

        ArrayList<String> param = new ArrayList<String>();
        param.add("00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048");




        while(true) {


            JSONObject obj=r.getInfo("getblock", param);


            ObjectMapper mapper = new ObjectMapper();
            try {
                Block b =mapper.readValue(obj.get("result").toString(), Block.class);
                System.out.println("temp");
            } catch (IOException e) {
                e.printStackTrace();
            }

//            Block b = JSON.par

            JSONObject obj1 = (JSONObject) obj.get("result");
            String nextBlockHash = String.valueOf(obj1.get("nextblockhash"));

            //            JSONArray ja =  obj.get("result");
            param.clear();
//            String t = obj.get("result").get("nextblockhash");
            param.add(nextBlockHash);
            System.out.println(obj.toJSONString());
        }
    }
}
