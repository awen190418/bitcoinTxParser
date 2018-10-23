import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoinRpcException;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.util.Date;
import java.util.List;

public class Block  implements BitcoinJSONRPCClient.Block {
    @Override
    public String hash() {
        return null;
    }

    @Override
    public int confirmations() {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int height() {
        return 0;
    }

    @Override
    public int version() {
        return 0;
    }

    @Override
    public String merkleRoot() {
        return null;
    }

    @Override
    public List<String> tx() {
        return null;
    }

    @Override
    public Date time() {
        return null;
    }

    @Override
    public long nonce() {
        return 0;
    }

    @Override
    public String bits() {
        return null;
    }

    @Override
    public double difficulty() {
        return 0;
    }

    @Override
    public String previousHash() {
        return null;
    }

    @Override
    public String nextHash() {
        return null;
    }

    @Override
    public String chainwork() {
        return null;
    }

    @Override
    public BitcoindRpcClient.Block previous() throws BitcoinRpcException {
        return null;
    }

    @Override
    public BitcoindRpcClient.Block next() throws BitcoinRpcException {
        return null;
    }
}
