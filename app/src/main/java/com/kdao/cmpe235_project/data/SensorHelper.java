package com.kdao.cmpe235_project.data;

import org.json.simple.JSONObject;

public class SensorHelper {
    private SensorType type;
    private JSONObject data;

    public SensorHelper() {}

    public SensorHelper(SensorType type, JSONObject data) {
        this.data = data;
        this.type = type;
    }

    //1. water
    //2. light
    //3. speed
    //4. voice
    public boolean getSensorState() {
        int sensorType = type.getId();
        int state = 0;
        System.out.println(data);
        switch(sensorType) {
            case 1:
                state = Integer.parseInt(data.get("wateron").toString());
                break;
            case 2:
                state = Integer.parseInt(data.get("lighton").toString());
                break;
            case 3:
                state = Integer.parseInt(data.get("speedon").toString());
                break;
            case 4:
                state = Integer.parseInt(data.get("voiceon").toString());
                break;
            default:
                break;
        }
        //System.out.println(">>>>>> State=" + state);
        //System.out.println(">>>>>> State=" + (state != 0));
        return (state != 0);
    }
}
