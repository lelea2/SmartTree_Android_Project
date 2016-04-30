package com.kdao.cmpe235_project.data;

public class WaterSensor extends Sensor {
    private boolean onoff;
    private int temp;

    public boolean isOnoff() {
        return onoff;
    }

    public void setOnoff(boolean onoff) {
        this.onoff = onoff;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}
