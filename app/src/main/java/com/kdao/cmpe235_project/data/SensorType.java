package com.kdao.cmpe235_project.data;

/**
 * Entity for sensorType
 */
public class SensorType {
    private String type;
    private int id;

    public SensorType() {}

    public SensorType(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public SensorType(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
