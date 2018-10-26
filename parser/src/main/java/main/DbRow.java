package main;

import java.sql.Date;


public class DbRow {

    public String blockHash;
    public String blockHeight;
    public String receivedTxId;
    public int rID;
    public Date receivedDate;
    public String spentTxId;
    public int sID;
    public Date spentDate;
    public String address ;
    public double value;
    public double holdDuration;


    public DbRow(String blockHash,String blockHeight, String receivedTxId, int rID, Date receivedDate, String spentTxId, int sID, Date spentDate, String address , double value, double holdDuration){
        this.blockHash=blockHash;
        this.blockHeight=blockHeight;
        this.receivedDate=receivedDate;
        this.receivedTxId=receivedTxId;
        this.rID=rID;
        this.spentDate=spentDate;
        this.spentTxId=spentTxId;
        this.sID=sID;
        this.address=address;
        this.value=value;
        this.holdDuration=holdDuration;

    }




    }
