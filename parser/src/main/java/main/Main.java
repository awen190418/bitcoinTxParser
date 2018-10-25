package main;

import config.Config;
import databse.connection.DbPool;
import org.json.simple.JSONObject;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Main {
    public static Properties properties;
    public static DbPool dbPool;
    public static void main(String args[]) {
        Config c = new Config();

        Main.properties = c.getConfig();

        Main.dbPool= new DbPool(10);


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
        String blockHash=Main.properties.getProperty("startBlockHash");
//        String blockHash="0000000000000000011388415eedad7b48c85d96c24522c72195da90d7e1ff59";


        int threadCount = Integer.valueOf(Main.properties.getProperty("threads"));
        ArrayList<BlockParser> parsers = new ArrayList<BlockParser>();





        //This will sacan the block for always.  just for the while loop.
        while(!String.valueOf(Main.properties.getProperty("stopBlockHash")).equals( blockHash)) {
            //1. Block payo
            BitcoindRpcClient.Block block = RPCclient.getBlock(blockHash);

            Properties properties = new Properties();
            properties.setProperty("lastBlock", blockHash);
            writeToProp("last.properties",properties);

            ArrayList<Integer> toRemove = new ArrayList<Integer>();

            for (BlockParser i : parsers){
                if(!i.isAlive()){
                    toRemove.add(parsers.indexOf(i));
                }
            }
            for(Integer x : toRemove){
                parsers.remove(x);
            }

            if(parsers.size() >= threadCount){
                while(!parsers.isEmpty()){
                    try {
                        BlockParser p =parsers.remove(parsers.size()-1);
                        p.join();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Main.gc();
            }

            parsers.add(0, new BlockParser(RPCclient, block, blockHash));
            parsers.get(0).start();

            blockHash = RPCclient.getBlock(blockHash).nextHash();
        }

    }
    public static void writeToProp(String fileName, Properties properties){
        try {
            File file = new File(fileName);
            FileOutputStream fileOut = new FileOutputStream(file);
            properties.store(fileOut, "Last Block Read");
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

    /**
     * This method guarantees that garbage collection is
     * done unlike <code>{@link System#gc()}</code>
     */
    public static void gc() {
        Object obj = new Object();
        WeakReference ref = new WeakReference<Object>(obj);
        obj = null;
        while(ref.get() != null) {
            System.gc();
        }
    }

}
