import config.Config;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import org.bitcoinj.core.Block;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
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


        BitcoinJSONRPCClient bitcoin = new BitcoinJSONRPCClient(url);

        String blockHash="00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048";



        while(port>0) {
            BitcoindRpcClient.Block block = bitcoin.getBlock(blockHash);
            ArrayList<String> TxList = (ArrayList<String>) block.tx();



            for (String TxHash : TxList) {
                BitcoindRpcClient.RawTransaction Tx = bitcoin.getRawTransaction(TxHash);

                List<BitcoindRpcClient.RawTransaction.Out> out = (List<BitcoindRpcClient.RawTransaction.Out>) Tx.vOut();
                List<BitcoindRpcClient.RawTransaction.In> in = (List<BitcoindRpcClient.RawTransaction.In>) Tx.vIn();

                //get the input address and its details
                in=null;


            }












            blockHash = bitcoin.getBlock(blockHash).nextHash();



        }
















        RPCClient r = new RPCClient();




        ArrayList<String> param = new ArrayList<String>();
        ArrayList<String> param1 = new ArrayList<String>();

        param.add("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f");


        while (true) {
            JSONObject obj = r.getInfo("getblock", param);
            JSONObject obj1 = (JSONObject) obj.get("result");
            JSONArray obj2 = (JSONArray) obj1.get("tx");

            int i = 0;
            if(obj2.size()>1){
                System.out.println("YYYYYY********************************");

            }
            while (i < obj2.size()) {
                System.out.println("TX ID---------------> "+obj2.get(i));

                //get the transaction.

                param1.clear();
                param1.add((String) obj2.get(i) );
                param1.add("1");

                JSONObject TxDetails = r.getInfo("getrawtransaction", param1);





                i++;
            }

            String nextBlockHash = String.valueOf(obj1.get("nextblockhash"));


            param.clear();
            param.add(nextBlockHash);
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
