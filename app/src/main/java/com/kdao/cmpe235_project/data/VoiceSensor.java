package com.kdao.cmpe235_project.data;

public class VoiceSensor extends Sensor {

    private boolean onoff;
    private int volume;

    public boolean isOnoff() {
        return onoff;
    }

    public void setOnoff(boolean onoff) {
        this.onoff = onoff;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
