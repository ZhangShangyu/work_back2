package com.zsy.bmw.controller;

import com.zsy.bmw.model.House;
import com.zsy.bmw.model.HouseCondition;
import com.zsy.bmw.service.HouseService;
import com.zsy.bmw.utils.Constant;
import com.zsy.bmw.utils.Result;
import com.zsy.bmw.utils.UploadFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping(value = "/house")
public class HouseController {

    private static Logger logger = LoggerFactory.getLogger(HouseController.class);

    @Autowired
    private HouseService houseService;

    @Autowired
    private UploadFileUtil uploadFileUtil;

    @RequestMapping("/list")
    public Result getHouseList(HouseCondition condition) {
        logger.info("house list request", condition);
        Result result = new Result(Constant.OK_CODE, Constant.OK);
        result.setData(houseService.getHouse(condition));
        logger.info("house list response", result);
        return result;
    }

    @RequestMapping("/save")
    public Result uploadHouse(@RequestBody House house) {
        logger.info("house save request", house);
        houseService.saveHouse(house);
        Result result = new Result(Constant.OK_CODE, Constant.OK);
        logger.info("house save response", result);
        return result;
    }

    @RequestMapping("/detail")
    public Result getHouseDetail(@RequestParam("id") Integer houseId) {
        System.out.print('b');
        logger.info("house detail request", houseId);
        House house = houseService.getHouseDetail(houseId);
        Result result = new Result(Constant.OK_CODE, Constant.OK);
        result.setData(house);
        logger.info("house detail response", result);
        return result;
    }


    @RequestMapping("house-by-me")
    public Result getHouseByCreator(@RequestParam("id") Integer agentId) {
        List<House> houses = houseService.getHouseByCreator(agentId);
        Result result = new Result(Constant.OK_CODE, Constant.OK);
        result.setData(houses);
        return result;
    }

    @RequestMapping(value = "/upload")
    public Result uploadPic(@RequestParam("file") MultipartFile uploadFile) {
        logger.info("img upload request");
        if (uploadFile == null || uploadFile.isEmpty()) {
            logger.info("img upload response: not img file");
            return new Result(Constant.ERROR_CODE1, Constant.PARAM_ERROR);
        }
        String fileUrl;
        try {
            fileUrl = uploadFileUtil.saveUploadedFiles(uploadFile);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("img upload response: upload exception");
            return new Result(Constant.ERROR_CODE2, Constant.SAVE_FILE_ERROR);
        }
        Result result = new Result(Constant.OK_CODE, Constant.OK);
        result.setData(fileUrl);
        logger.info("img upload response", result);
        return result;
    }

    @RequestMapping("test")
    public Result genTestData(@RequestParam("count") Integer count) {
        houseService.genTestData(count);
        return new Result(Constant.OK_CODE, "run test data started!");
    }

}
