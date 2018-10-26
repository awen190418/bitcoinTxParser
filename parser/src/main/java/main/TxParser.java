package main;

import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TxParser extends Thread {
    List<BitcoindRpcClient.RawTransaction.In> in;
    BitcoindRpcClient.Block block;
    BitcoindRpcClient RPCclient;
    String TxHash;
    ArrayList<String> TxList;
    ArrayList<DbRow> dataToInsert = new ArrayList<>();

    public TxParser(BitcoindRpcClient.Block b, List<BitcoindRpcClient.RawTransaction.In> i, BitcoindRpcClient r, String t, ArrayList<String> tL){
        this.block=b;
        this.RPCclient=r;
        this.in = i;
        this.TxHash=t;
        this.TxList=tL;
    }
    @Override
    public void run(){


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


//                    System.out.println("Input No: " + i);

            if(Main.properties.get("debug").equals("1")) {
                System.out.println(
                        "\n\n\n*****************************************************\n" +
                                "Transaction Details\n" +
                                "*****************************************************\n" +
                                "Received TxId: " + soruceTXId +
                                " \n" +
                                "ID: " + String.valueOf(sourceVoutId) +
                                "\n" +
                                "Received Date: " + receivedDate.toString() +
                                " \n\n" +

                                "Spent TxId: " + spentTxId +
                                " \n" +
                                "ID: " + String.valueOf(spentVinIndex) +
                                " \n" +
                                "Spent Date: " + spentDate.toString() +
                                "\n\n" +
                                "Address: " + bitcoinAddress +
                                " \n" +
                                "Amount: " + String.valueOf(value) +
                                "\n" +
                                "Holded Duration: " + diff +

                                "\n\n" +
                                "*****************************************************"
                );
            }
//                    public boolean Write(String blockHash, String receivedTxId, int rID, String receivedDate, String spendTxId, String spentTxId, int sID, String spentDate,String address , double value, double holdDuration) throws SQLException {
            dataToInsert.add(new DbRow(block.hash(),String.valueOf(block.height()),soruceTXId,Integer.valueOf(sourceVoutId),new java.sql.Date(receivedDate.getTime()),spentTxId,Integer.valueOf(spentVinIndex),new java.sql.Date(spentDate.getTime()),bitcoinAddress,value,diff));

            i++;
        }
          try {
                Main.dbPool.getConnection().Write(dataToInsert);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        //input parsing ends

        dataToInsert.clear();
        in = null;


    }
}
