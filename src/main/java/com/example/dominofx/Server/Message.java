package com.example.dominofx.Server;

import com.example.dominofx.Tile;
import javafx.scene.image.PixelBuffer;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private final MessageType type;
    private final String data;
    private final ArrayList<Tile> list;
    private int lastlick;
    private Tile tile;
    public Message(MessageType type){
        this.type = type;
        data = null;
        list =  null;
        tile = null;
    }
    public Message(MessageType type, String data){
        this.type = type;
        this.data = data;
        list = null;
        tile = null;
    }
    public Message(MessageType type, ArrayList<Tile> list){
        this.type = type;
        data = null;
        this.list = list;
        tile = null;
    }
    public Message (MessageType type, int lastClick){
        this.type = type;
        data = null;
        list =  null;
        this.lastlick = lastClick;
        tile = null;
    }
    public Message(MessageType type, Tile tile) {
        this.type = type;
        data = null;
        list = null;
        this.tile = tile;
    }


    public int getLastlick() {
        return lastlick;
    }

    public MessageType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public ArrayList<Tile> getList() {
        return list;
    }
}