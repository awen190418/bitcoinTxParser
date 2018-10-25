package databse.implementation;


import databse.Signature.Database;
import databse.connection.DBConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class DatabaseAPI implements Database {
    DBConnection connection;


    public DatabaseAPI() throws SQLException, ClassNotFoundException {
        connection = new DBConnection();
    }

    @Override
    public boolean WriteInput(String hash_AB, String TxHash, String TxHashParent_A,String BTCAddress_B, long date) throws SQLException {
        try {
            PreparedStatement preparedStatement;
            preparedStatement=connection.getConnect().prepareStatement("INSERT INTO input(hash_a_b,Tx_Hash,Tx_Hash_Parent_a,BTCAddress_b,date) values( ?, ?, ?  ,? ,?)");

            preparedStatement.setString(1, hash_AB);
            preparedStatement.setString(2, TxHash);
            preparedStatement.setString(3, TxHashParent_A);
            preparedStatement.setString(4, BTCAddress_B);
            preparedStatement.setTimestamp(5, new java.sql.Timestamp(date));

            preparedStatement.executeUpdate();
            preparedStatement.close();
//            System.out.println("Inserted");

        } catch (SQLException e) {
//            System.out.println(e.getMessage());
            throw e;

        }
        return true;
    }

    @Override
    public boolean WriteOutput(String hash_AB, String TxHash_A, String BTCAddress_B, Double value, long date) throws SQLException {

        try {
            PreparedStatement preparedStatement;
            preparedStatement=connection.getConnect().prepareStatement("INSERT INTO output(hash_a_b,Tx_Hash_a,BTCAddress_b,value,date) values( ?, ?, ?  ,? ,?)");


            preparedStatement.setString(1, hash_AB);
            preparedStatement.setString(2, TxHash_A);
            preparedStatement.setString(3, BTCAddress_B);
            preparedStatement.setDouble(4, value);
            preparedStatement.setTimestamp(5, new java.sql.Timestamp(date));

            preparedStatement.executeUpdate();
            preparedStatement.close();

//            System.out.println("Inserted OP");

        } catch (SQLException e) {
            //e.printStackTrace();
            //System.out.println(e.getMessage());
            throw e;
        }
        return true;
    }

    @Override
    public boolean Write(String blockHash,String blockHeight, String receivedTxId, int rID, Date receivedDate, String spentTxId, int sID, Date spentDate, String address , double value, double holdDuration) throws SQLException {
        try {
            PreparedStatement preparedStatement;
            preparedStatement=connection.getConnect().prepareStatement("INSERT INTO main.transaction1(blockID, receivedTxHash, rID,receivedDate, spentTxHash,sID,spentDate, address,bitcoin,holdedTime,blockNo ) values(?,?,?,?,?,?,?,?,?,?,?)");


            preparedStatement.setString(1, blockHash);

            preparedStatement.setString(2, receivedTxId);
            preparedStatement.setInt(3, rID);
            preparedStatement.setDate(4, receivedDate);

            preparedStatement.setString(5, spentTxId);
            preparedStatement.setInt(6, sID);
            preparedStatement.setDate(7, spentDate);

            preparedStatement.setString(8, address);
            preparedStatement.setDouble(9, value);
            preparedStatement.setDouble(10, holdDuration);
            preparedStatement.setInt(11, Integer.valueOf(blockHeight));

            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {

            throw e;
        }
        return true;
    }

    public void closeConnection(){
        connection.close();
    }
}
