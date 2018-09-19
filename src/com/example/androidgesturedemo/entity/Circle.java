package com.example.androidgesturedemo.entity;

public class Circle {
    /**
     * position of the circle
     */
    private int position;
    private int x;
    private int y;
    /**
     * the state of circle
     */
    private int state;
    private RectMain rectMain;
    String readText;

    public Circle() {
    }

    public Circle(int position, int x, int y, int state, RectMain rectMain, String readText) {
        this.position = position;
        this.x = x;
        this.y = y;
        this.state = state;
        this.rectMain = rectMain;
        this.readText = readText;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public RectMain getRectMain() {
        return rectMain;
    }

    public void setRectMain(RectMain rectMain) {
        this.rectMain = rectMain;
    }

    public String getReadText() {
        return readText;
    }

    public void setReadText(String readText) {
        this.readText = readText;
    }
}