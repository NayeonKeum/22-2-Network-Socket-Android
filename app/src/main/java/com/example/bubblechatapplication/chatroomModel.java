package com.example.bubblechatapplication;

public class chatroomModel {
    private int port;
    private String area;

    public chatroomModel(int port){//}, String area){
        this.port=port;
//        this.area=area;
    }

    public int getPort(){
        return port;
    }
}
