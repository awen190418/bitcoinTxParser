package databse.connection;


import databse.implementation.DatabaseAPI;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DbPool {

    public ArrayList<DatabaseAPI> pool;
    int noOfUse=0;
    int noOfConnection=0;
    public DbPool(int noOfConnection) {
        this.noOfConnection=noOfConnection;
        this.pool = new ArrayList<>();
        int i =0;
        while(i < noOfConnection){
            try {
                this.pool.add(new DatabaseAPI());
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    public void restart(){
        this.pool = new ArrayList<>();
        int i =0;
        while(i < this.noOfConnection){
            try {
                this.pool.add(new DatabaseAPI());
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    public void closeConnectionPool(){
        for(DatabaseAPI db : pool){
            db.closeConnection();
        }
    }
    public DatabaseAPI getConnection(){
//        int min=0;
//        int max=pool.size()-1;
//        Random random = new Random();
//        if(noOfUse>5000){
//            this.closeConnectionPool();
//            this.restart();
//            this.noOfUse=0;
//            System.out.println("DB Connection CLEARED");
//        }

        int r3 = ThreadLocalRandom.current().nextInt(pool.size());

        //int randomNo=random.nextInt((max - min) + 1) + min;


        this.noOfUse++;

        return  pool.get(r3);
    }



}
