package com.kdao.cmpe235_project.data;

import com.kdao.cmpe235_project.util.Config;

import org.json.simple.JSONObject;

public class SensorHelper {
    private SensorType type;
    private JSONObject data;

    public SensorHelper() {}

    public SensorHelper(SensorType type, JSONObject data) {
        this.data = data;
        this.type = type;
    }

    public SensorType getType() {
        return type;
    }

    public void setType(SensorType type) {
        this.type = type;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    //1. water
    //2. light
    //3. speed
    //4. voice
    public boolean getSensorState() {
        int state = 0;
        state = Integer.parseInt(data.get("onoff").toString());
        System.out.println(">>>>>> State=" + state);
        System.out.println(">>>>>> State checked=" + (state != 0));
        return (state != 0);
    }
}
