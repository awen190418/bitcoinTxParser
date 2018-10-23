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

        //This will sacan the block for always.  just for the while loop.
        while(port>0) {
            //1. Block payo
            BitcoindRpcClient.Block block = RPCclient.getBlock(blockHash);
            System.out.println(
                    "\n\n\n\n Block Received"+
                            "Block Hash: " + blockHash+"\n"+
                            "Block number: " + block.height()+"\n"

            );

            //2. Block le duni chongu Transaction na paye jula]
            ArrayList<String> TxList = (ArrayList<String>) block.tx();

            //Coinbase Transaction jula la ki output jaka server le cho.
            BitcoindRpcClient.RawTransaction Tx = RPCclient.getRawTransaction(TxList.get(0));

            List<BitcoindRpcClient.RawTransaction.Out> out = (List<BitcoindRpcClient.RawTransaction.Out>) Tx.vOut();
            //Out ta parse yayu function dayeki mani.

            //Tho IP coinbase kha aaye juya thake ta chu na yaye mwa.
            List<BitcoindRpcClient.RawTransaction.In> in = (List<BitcoindRpcClient.RawTransaction.In>) Tx.vIn();

            // Input ta parse yagu pati.

            if(TxList.size()>1) {
                for (String TxHash : TxList.subList(1, TxList.size())) {
                    Tx = RPCclient.getRawTransaction(TxHash);
                    System.out.println("Transaction No : " + TxList.indexOf(TxHash) );

                    out = (List<BitcoindRpcClient.RawTransaction.Out>) Tx.vOut();
                    in = (List<BitcoindRpcClient.RawTransaction.In>) Tx.vIn();


                    //Input parsing
                    int i=0;
                    while(i<in.size()){
                        String soruceTXId= in.get(i).txid();
                        int sourceVoutId= in.get(i).vout();


                        BitcoindRpcClient.RawTransaction sourceTx = RPCclient.getRawTransaction(soruceTXId);
                        Date receivedDate = sourceTx.blocktime();

                        List<BitcoindRpcClient.RawTransaction.Out> sourceOut = (List<BitcoindRpcClient.RawTransaction.Out>) sourceTx.vOut();

                        String bitcoinAddress = sourceOut.get(sourceVoutId).scriptPubKey().addresses().get(0);
                        double value = sourceOut.get(sourceVoutId).value();
                        Date spentDate = block.time();

                        String spentTxId = TxHash;
                        int spentVinIndex=TxList.indexOf(TxHash);

//                        Duration duration;
//                        duration = (Duration) Duration.between(spentDate, receivedDate);
//                        long diff = Math.abs(duration.toMinutes());
                        double diff = spentDate.getTime() - receivedDate.getTime();
                        diff = diff / (60 * 60*1000); // minutes


                        System.out.println("Input No: " + i);
                        System.out.println(
                                "\n\n\n*****************************************************\n"+
                                "Transaction Details\n"+
                                "*****************************************************\n"+
                                "Received TxId: "+soruceTXId+
                                " \n"+
                                "ID: "+String.valueOf(sourceVoutId)+
                                "\n"+
                                "Received Date: "+ receivedDate.toString()+
                                " \n\n"+

                                "Spent TxId: "+spentTxId+
                                " \n"+
                                "ID: "+String.valueOf(spentVinIndex)+
                                " \n"+
                                "Spent Date: "+ spentDate.toString()+
                                "\n\n"+
                                "Address: "+bitcoinAddress+
                                " \n"+
                                "Amount: "+String.valueOf(value) +
                                "\n"+
                                "Holded Duration: " +diff+

                        "\n\n"+
                                "*****************************************************"
                        );
                        i++;
                    }
                    //input parsing ends
                    in = null;
                }
            }
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
