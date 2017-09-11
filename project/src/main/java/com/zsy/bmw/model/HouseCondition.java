package com.zsy.bmw.model;


public class HouseCondition extends BaseEntity {

    private Character priceType;
    private Character areaType;
    private Character roomType;
    private Character sortType;

    public Character getPriceType() {
        return priceType;
    }

    public void setPriceType(Character priceType) {
        this.priceType = priceType;
    }

    public Character getAreaType() {
        return areaType;
    }

    public void setAreaType(Character areaType) {
        this.areaType = areaType;
    }

    public Character getRoomType() {
        return roomType;
    }

    public void setRoomType(Character roomType) {
        this.roomType = roomType;
    }


    private Integer pageNum = 1;
    private Integer rows = 2;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }


    public Character getSortType() {
        return sortType;
    }

    public void setSortType(Character sortType) {
        this.sortType = sortType;
    }
}
