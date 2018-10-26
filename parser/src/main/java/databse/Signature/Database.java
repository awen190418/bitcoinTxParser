package databse.Signature;

import main.DbRow;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public interface Database {

    boolean WriteInput(String hash_AB, String TxHash, String TxHashParent_A, String BTCAddress_B, long date) throws SQLException;
    boolean WriteOutput(String hash_AB, String TxHash_A, String BTCAddress_B, Double value, long date) throws SQLException;


    boolean Write(ArrayList<DbRow> rowBatch)throws SQLException;

}
