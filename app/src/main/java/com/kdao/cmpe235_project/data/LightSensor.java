package com.kdao.cmpe235_project.data;

public class LightSensor extends Sensor {
    private boolean onoff;
    private String color;

    public boolean isOnoff() {
        return onoff;
    }

    public void setOnoff(boolean onoff) {
        this.onoff = onoff;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
