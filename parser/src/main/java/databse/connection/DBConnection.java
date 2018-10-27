package databse.connection;

import main.Main;

import java.sql.Connection;


import java.sql.*;
import java.util.Properties;

public class DBConnection {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;




    public DBConnection() throws SQLException, ClassNotFoundException {
       Properties prop= Main.properties;

        try {
//            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Setup the connection with the DB
//            connect = DriverManager.getConnection(prop.getProperty("URL")+"/"+prop.getProperty("db")+"?user="+prop.getProperty("user")+"&password="+prop.getProperty("password"));
            connect = DriverManager.getConnection(prop.getProperty("URL")+":3306/"+prop.getProperty("db")+"?useUnicode=true&characterEncoding=UTF-8" + "&rewriteBatchedStatements=true" ,prop.getProperty("user"),prop.getProperty("password"));

        } catch (ClassNotFoundException | SQLException e) {
            throw e;

        }
    }


    public  void close(){

        try {
            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }



    public Connection getConnect() {
        return connect;
    }

    public Statement getStatement() {
        return statement;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }
}
