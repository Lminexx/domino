package com.example.dominofx;

public class Marker {
    private double x;
    private double y;
    private int value;
    private boolean start;
    private double degree;
    private int who;

    public Marker(double x, double y, int value, boolean start, double degree, int who) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.start = start;
        this.degree = degree;
        this.who = who;
    }

    public int getWho() {
        return who;
    }

    public boolean isClicked(double mouseX, double mouseY) {
        double dx = mouseX - x;
        double dy = mouseY - y;
        return Math.sqrt(dx * dx + dy * dy) <= 20; // Радиус маркера 20 пикселей
    }



    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isStart() {
        return start;
    }

    public double getDegree() {
        return degree;
    }

    public boolean isVertical() {
        return degree == 0 || degree == 180 || degree == -180;
    }
}
