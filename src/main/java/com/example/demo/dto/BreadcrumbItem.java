package com.example.demo.dto;

public class BreadcrumbItem {
    private int position;
    private String name;
    private String url;

    public BreadcrumbItem(int position, String name, String url) {
        this.position = position;
        this.name = name;
        this.url = url;
    }

    // getter và setter
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
