package com.kdao.cmpe235_project.data;

public class Tree {

    private String id;
    private String title;
    private String description;
    private String icon;
    private String youtubeId;
    private Location location;
    private Sensor sensor;

    public Tree(String id, String title, String description, String icon, String youtubeId, Location location, Sensor sensor)  {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.youtubeId = youtubeId;
        this.location = location;
        this.sensor = sensor;
    }
}
