package com.zsy.bmw.dao;

import com.zsy.bmw.model.House;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface HouseMapper {

    List<House> getHouseByTime();

    List<House> getHouseByArea(@Param("min") Integer min, @Param("max") Integer max);

    List<House> getHouseByPrice(@Param("min") Integer min, @Param("max") Integer max);

    List<House> getHouseByRoom(@Param("count") Integer count);

    String getHeadImg(House house);

    void insertHouse(House house);

    void insertHouseExtend(House house);

    void insertHouseImg(@Param("houseId") Integer houseId,
                        @Param("imgUrl") String imgUrl,
                        @Param("houseImgTable") String houseImgTable);

    House getHouseById(House house);

    House getHouseExtendById(House house);

    List<String> getHouseImgs(House house);

    List<House> getHouseByCreator(@Param("agentId") Integer agentId);

    void insertAndGetIncId(House house);
}
