package com.example.als;

import java.io.Serializable;

public class Point implements Serializable {
    private double x;
    private double y;

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double f) {
        this.x = f;
    }

    public void setY(double f) {
        this.y = f;
    }
}