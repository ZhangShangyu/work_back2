package com.zsy.bmw.dao;

import com.zsy.bmw.model.House;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface HouseMapper {

    List<House> getHouseByTime();

    List<House> getHouseByArea(@Param("min") Integer min, @Param("max") Integer max);

    List<House> getHouseByPrice(@Param("min") Integer min, @Param("max") Integer max);

    List<House> getHouseByRoom(@Param("count") Integer count);

    String getHeadImg(@Param("houseId") Integer houseId);

    void insertHouse(House house);

    void insertHouseExtend(House house);

    void insertHouseImg(@Param("houseId") Integer houseId, @Param("imgUrl") String imgUrl);

    House getHouseById(@Param("houseId") Integer houseId);

    House getHouseExtendById(@Param("houseId") Integer houseId);

    List<String> getHouseImgs(@Param("houseId") Integer houseId);

    List<House> getHouseByCreator(@Param("agentId") Integer agentId);
}
