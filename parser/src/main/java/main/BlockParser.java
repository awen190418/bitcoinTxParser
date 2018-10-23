package main;

import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlockParser extends Thread  {

    BitcoindRpcClient RPCclient;
    BitcoindRpcClient.Block block;
    String blockHash;

    public BlockParser(BitcoinJSONRPCClient rc, BitcoindRpcClient.Block b,String hash){
        RPCclient =rc;
        block =b;
        blockHash=hash;
    }

    @Override
    public void run() {
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

                if(Main.properties.getProperty("debug").equals("1")) {
                    System.out.println("Transaction No : " + TxList.indexOf(TxHash));
                }
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

                    try {
                        Main.dbPool.getConnection().Write(block.hash(),String.valueOf(block.height()),soruceTXId,Integer.valueOf(sourceVoutId),new java.sql.Date(receivedDate.getTime()),spentTxId,Integer.valueOf(spentVinIndex),new java.sql.Date(spentDate.getTime()),bitcoinAddress,value,diff);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                //input parsing ends
                in = null;
            }
        }
    }

}
