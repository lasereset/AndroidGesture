package com.example.androidgesturedemo.entity;

public class RectMain {
    float left;
    float right;
    float top;
    float bottom;

    public RectMain(float left, float rifht, float top, float bottom) {
        this.left = left;
        this.right = rifht;
        this.top = top;
        this.bottom = bottom;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRifht() {
        return right;
    }

    public void setRifht(float rifht) {
        this.right = rifht;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }
}