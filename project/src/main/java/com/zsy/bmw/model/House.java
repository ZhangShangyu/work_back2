package com.zsy.bmw.model;

import java.util.List;

public class House extends BaseEntity {
    private Integer id;
    private String name = "";
    private Integer area = 0;
    private Integer room = 0;
    private Integer hall = 0;
    private Float price = 0f;
    private Integer position = 0;
    private Integer allPos = 0;
    private String upTime;
    private Integer agentId = 0;
    private Integer year = 0;
    private String comName = "";
    private String address = "";
    private String des = "";

    private String houseTable = "house0";
    private String houseExtTable = "house_ext0";
    private String houseImgTable = "house_img0";

    public String getHouseTable() {
        return houseTable;
    }

    public void setHouseTable(String houseTable) {
        this.houseTable = houseTable;
    }

    public String getHouseExtTable() {
        return houseExtTable;
    }

    public void setHouseExtTable(String houseExtTable) {
        this.houseExtTable = houseExtTable;
    }

    public String getHouseImgTable() {
        return houseImgTable;
    }

    public void setHouseImgTable(String houseImgTable) {
        this.houseImgTable = houseImgTable;
    }

    private String headImg;
    private List<String> imgUrls;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public Integer getHall() {
        return hall;
    }

    public void setHall(Integer hall) {
        this.hall = hall;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getAllPos() {
        return allPos;
    }

    public void setAllPos(Integer allPos) {
        this.allPos = allPos;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }


}
