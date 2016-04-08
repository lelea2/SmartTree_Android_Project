package com.kdao.cmpe235_project.data;

/**
 * Created by YuexingYin on 4/8/16.
 */
public class Sensor {

    private int id;
    private String name;
    private boolean lighton;
    private String lightcolor;

    public Sensor(boolean lighton, String name, String lightcolor, int id) {
        this.id = id;
        this.name = name;
        this.lighton = lighton;
        this.lightcolor = lightcolor;
    }
}
