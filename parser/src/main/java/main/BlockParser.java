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
                "\n"+
                        "Block Hash: " + blockHash+"\n"+
                        "Block number: " + block.height()+"\n"+
                        "Block TxSize: "+ block.tx().size()

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
        ArrayList<TxParser> tParser = new ArrayList<>();

        if(TxList.size()>1) {
            for (String TxHash : TxList.subList(1, TxList.size())) {
                Tx = RPCclient.getRawTransaction(TxHash);

                if(Main.properties.getProperty("debug").equals("1")) {
                    System.out.println("Transaction No : " + TxList.indexOf(TxHash));
                }
                out = (List<BitcoindRpcClient.RawTransaction.Out>) Tx.vOut();
                in = (List<BitcoindRpcClient.RawTransaction.In>) Tx.vIn();

                int index = tParser.size();
                if(index!=0){
                    index=index-1;
                }
                tParser.add(index,new TxParser(block,in,RPCclient,TxHash,TxList));
                tParser.get(index).start();

            }
            for(TxParser p : tParser){
                try {
                    p.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

//            try {
//                Main.dbPool.getConnection().Write(this.rowList);
//                this.rowList.clear();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }


        }
    }

}
