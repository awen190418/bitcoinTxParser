package databse.Signature;

import java.sql.Date;
import java.sql.SQLException;

public interface Database {

    boolean WriteInput(String hash_AB, String TxHash, String TxHashParent_A, String BTCAddress_B, long date) throws SQLException;
    boolean WriteOutput(String hash_AB, String TxHash_A, String BTCAddress_B, Double value, long date) throws SQLException;


   boolean Write(String blockHash,String blockHeight, String receivedTxId, int rID, Date receivedDate, String spentTxId, int sID, Date spentDate, String address , double value, double holdDuration) throws SQLException;

}
