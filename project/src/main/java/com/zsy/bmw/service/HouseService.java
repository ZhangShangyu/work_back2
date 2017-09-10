package com.zsy.bmw.service;

import com.github.pagehelper.PageHelper;
import com.zsy.bmw.dao.HouseMapper;
import com.zsy.bmw.model.House;
import com.zsy.bmw.model.HouseCondition;
import com.zsy.bmw.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class HouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private RedisService redisService;

    public List<House> getHouseByCreator(Integer agentId) {
        return houseMapper.getHouseByCreator(agentId);
    }


    @Cacheable(value = "houseList", keyGenerator = "keyGenerator")
    public List<House> getHouse(HouseCondition condition) {
        executePagination(condition.getPageNum());
        List<House> houses = getHouseByCondition(condition);
        for (House house : houses) {
            String imgName = houseMapper.getHeadImg(house.getId());
            house.setHeadImg(handleHouseImgUrl(imgName));
        }
        return houses;
    }

    private String handleHouseImgUrl(String imgName) {
        String imgUrl = "";
        if (imgName != null) {
            if (imgName.contains("http")) {
                return imgName;
            } else {
                return Constant.IMG_PREFIX + imgName;
            }
        }
        return imgUrl;
    }

    private List<House> getHouseByCondition(HouseCondition condition) {
        List<House> houses;
        Character areaType = condition.getAreaType();
        Character priceType = condition.getPriceType();
        Character roomType = condition.getRoomType();

        if (areaType != null) {
            houses = getHouseByArea(areaType);
        } else if (priceType != null) {
            houses = getHouseByPrice(priceType);
        } else if (roomType != null) {
            houses = getHouseByRoom(roomType);
        } else {
            houses = houseMapper.getHouseByTime();
        }
        return houses;
    }

    private List<House> getHouseByArea(Character areaType) {
        Integer min = 0;
        Integer max = 50;
        switch (areaType) {
            case 'b':
                min = 50;
                max = 100;
                break;
            case 'c':
                min = 100;
                max = 10000;
                break;
            default:
                break;
        }
        return houseMapper.getHouseByArea(min, max);
    }

    private List<House> getHouseByPrice(Character priceType) {
        Integer min = 0;
        Integer max = 100;
        switch (priceType) {
            case 'b':
                min = 100;
                max = 150;
                break;
            case 'c':
                min = 150;
                max = 10000;
                break;
            default:
                break;
        }
        return houseMapper.getHouseByPrice(min, max);
    }

    private List<House> getHouseByRoom(Character roomType) {
        Integer count = 1;
        switch (roomType) {
            case 'b':
                count = 2;
                break;
            case 'c':
                count = 3;
                break;
            case 'd':
                count = 4;
                break;
            case 'f':
                count = 5;
                break;
            default:
                break;
        }
        return houseMapper.getHouseByRoom(count);
    }


    public void saveHouse(House house) {
        houseMapper.insertHouse(house);
        houseMapper.insertHouseExtend(house);
        for (String imgUrl : house.getImgUrls()) {
            houseMapper.insertHouseImg(house.getId(), imgUrl);
        }
    }

    @Cacheable(value = "houseDetail", keyGenerator = "keyGenerator")
    public House getHouseDetail(Integer id) {

        House house = houseMapper.getHouseById(id);
        if (house != null) {
            house = getExtendInfo(house);
            House houseExt = houseMapper.getHouseExtendById(id);
            house.setAddress(houseExt.getAddress());
            house.setYear(houseExt.getYear());
            house.setComName(houseExt.getComName());
            house.setDes(houseExt.getDes());
            house.setImgUrls(getHouseImgUrlList(house.getId()));
        }
        return house;
    }

    private List<String> getHouseImgUrlList(Integer houseId) {
        List<String> result = new ArrayList<>();
        List<String> imgNames = houseMapper.getHouseImgs(houseId);
        for (String imgName : imgNames) {
            result.add(handleHouseImgUrl(imgName));
        }
        return result;
    }

    private House getExtendInfo(House house) {
        House houseExt = houseMapper.getHouseExtendById(house.getId());
        house.setAddress(houseExt.getAddress());
        house.setYear(houseExt.getYear());
        house.setComName(houseExt.getComName());
        house.setDes(houseExt.getDes());
        return house;
    }


    private void executePagination(Integer pageNum) {
        PageHelper.startPage(pageNum, Constant.HOUSE_ROWS);
    }

}
