import config.Config;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import org.bitcoinj.core.Block;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.time.Duration;
import java.time.Period;
import java.util.*;

public class Main {
    public static Properties properties;

    public static void main(String args[]) {
        Config c = new Config();
        Main.properties = c.getConfig();

        int port = Integer.valueOf(Main.properties.getProperty("rpcPort"));
        String server = String.valueOf(Main.properties.getProperty("server"));

//        String url = "http://"+server+":"+ String.valueOf(port);
        URL url = null;
        try {
            url = new URL("http://" + Main.properties.getProperty("rpcUser") + ':' + Main.properties.getProperty("rpcPassword") + "@" + server + ":" + port + "/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BitcoinJSONRPCClient RPCclient = new BitcoinJSONRPCClient(url);

        //Hash of the first block. Block 0 is not parseable therefore not used here in the system.
        String blockHash="00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048";
//        String blockHash="0000000000000000011388415eedad7b48c85d96c24522c72195da90d7e1ff59";


        int threadCount = 5000;
        ArrayList<BlockParser> parsers = new ArrayList<BlockParser>();
        //This will sacan the block for always.  just for the while loop.
        while(port>0) {
            //1. Block payo
            BitcoindRpcClient.Block block = RPCclient.getBlock(blockHash);

            if(parsers.size() >= threadCount){
                while(!parsers.isEmpty()){
                    try {
                        parsers.remove(parsers.size()-1).join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            parsers.add(0, new BlockParser(RPCclient, block, blockHash));
            parsers.get(0).start();

            blockHash = RPCclient.getBlock(blockHash).nextHash();
        }

    }

    public static void printJsonObject(JSONObject jsonObj) {
        for (Object key : jsonObj.keySet()) {
            //based on you key types
            String keyStr = (String) key;
            Object keyvalue = jsonObj.get(keyStr);

            //Print key and value
            System.out.println("key: " + keyStr + " value: " + keyvalue);

            //for nested objects iteration if required
            if (keyvalue instanceof JSONObject)
                printJsonObject((JSONObject) keyvalue);
        }
    }

}
