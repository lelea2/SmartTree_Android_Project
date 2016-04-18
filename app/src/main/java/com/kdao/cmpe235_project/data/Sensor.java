package com.kdao.cmpe235_project.data;

public class Sensor {
    private String id;
    private String name;
    private SensorType type;

    public Sensor() {}

    public Sensor(String id, String name, SensorType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SensorType getType() {
        return type;
    }

    public void setType(SensorType type) {
        this.type = type;
    }
}
