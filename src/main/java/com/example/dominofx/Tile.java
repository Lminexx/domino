package com.example.dominofx;

import java.io.Serial;
import java.io.Serializable;

public class Tile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int first, second;
    private int degree;
    private boolean dooble;
    private boolean vertical = false;
    private double x, y;
    private int lastclick;

    public Tile(int fir, int sec, int degree, boolean dooble) {
        first = fir;
        second = sec;
        this.degree = degree;
        this.dooble = dooble;
        lastclick=0;
    }
    public void setDegree(int val) {
        degree = val;
    }

    public int getFirst() {
        return first;
    }


    public int getLastclick() {
        return lastclick;
    }

    public void setLastclick(int lastclick) {
        this.lastclick = lastclick;
    }

    public int getSecond() {
        return second;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public boolean isGorizont() {
        return degree == 90 || degree == 270 || degree == -90 || degree == -270;
    }
    public boolean isVertical(){
        return degree == 0 || degree == 180 || degree == -180;
    }
    public void rotate(){
        if(degree == 0){
            degree = 180;
        }else if(degree == 180){
            degree = 0;
        }
    }


    public int getDegree() {
        return degree;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
