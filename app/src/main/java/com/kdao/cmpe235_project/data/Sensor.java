package com.kdao.cmpe235_project.data;

public class Sensor {
    private String id;
    private String name;
    private SensorType type;
    private boolean isDeploy;

    public Sensor() {}

    public Sensor(String id, String name, SensorType type, boolean isDeploy) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isDeploy = isDeploy;
    }

    public boolean isDeploy() {
        return isDeploy;
    }

    public void setIsDeploy(boolean isDeploy) {
        this.isDeploy = isDeploy;
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
