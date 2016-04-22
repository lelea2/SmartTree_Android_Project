package com.kdao.cmpe235_project.data;

public class Tree {

    private String id;
    private String description;
    private String icon;
    private String youtubeId;
    private int sensorCount;
    private int likecount;
    private Location location;
    private Sensor sensor;

    public Tree() {}

    public Tree(String id, String description, String icon, String youtubeId, Location location, Sensor sensor)  {
        this.id = id;
        this.description = description;
        this.icon = icon;
        this.youtubeId = youtubeId;
        this.location = location;
        this.sensor = sensor;
    }

    public Tree(String id, String description, String icon, String youtubeId, Location location,
                Sensor sensor, int sensorCount, int likecount)  {
        this.id = id;
        this.description = description;
        this.icon = icon;
        this.youtubeId = youtubeId;
        this.location = location;
        this.sensor = sensor;
        this.sensorCount = sensorCount;
        this.likecount = likecount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public int getSensorCount() {
        return sensorCount;
    }

    public void setSensorCount(int sensorCount) {
        this.sensorCount = sensorCount;
    }

    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }
}
