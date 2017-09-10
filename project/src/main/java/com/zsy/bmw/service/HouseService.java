package com.zsy.bmw.service;

import com.zsy.bmw.dao.HouseMapper;
import com.zsy.bmw.model.House;
import com.zsy.bmw.model.HouseCondition;
import com.zsy.bmw.utils.Constant;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class HouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SolrClient solrClient;

    private static Logger logger = LoggerFactory.getLogger(HouseService.class);

    public List<House> getHouseByCreator(Integer agentId) {
        return houseMapper.getHouseByCreator(agentId);
    }


    @Cacheable(value = "houseList", keyGenerator = "keyGenerator")
    public List<House> getHouse(HouseCondition condition) {
        logger.info("cache not hit");
        List<Integer> houseIds = getHouseIdBySolr(condition);
        List<House> result = new ArrayList<>();
        for (Integer id : houseIds) {
            House house = new House();
            house.setId(id);
            handleTableName(house);
            house = houseMapper.getHouseById(house);
            if (house != null) {
                String imgName = houseMapper.getHeadImg(house);
                house.setHeadImg(handleHouseImgUrl(imgName));
                result.add(house);
            }
        }

        return result;
    }

    private List<Integer> getHouseIdBySolr(HouseCondition condition) {
        SolrQuery solrQuery = getQuery(condition);
        try {
            List<Integer> result = new ArrayList<>();
            QueryResponse resp = solrClient.query(solrQuery);
            SolrDocumentList list = resp.getResults();
            for (SolrDocument solrDocument : list) {
                result.add(Integer.parseInt((String) solrDocument.get("id")));
            }
            return result;
        } catch (SolrServerException e) {
            e.printStackTrace();
            return Collections.emptyList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private SolrQuery getQuery(HouseCondition condition) {
        String searchKey = getSearchKey(condition);
        if (searchKey == null) {
            searchKey = "*:*";
        }
        SolrQuery solrQuery = new SolrQuery(searchKey);
        solrQuery.set("sort", "id desc");
        solrQuery.set("start", (condition.getPageNum() - 1) * condition.getRows());
        solrQuery.set("rows", condition.getRows());
        solrQuery.set("fl", "id");
        return solrQuery;
    }

    private String getSearchKey(HouseCondition condition) {
        Character areaType = condition.getAreaType();
        Character priceType = condition.getPriceType();
        Character roomType = condition.getRoomType();

        if (areaType == null && priceType == null
                && roomType == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Boolean needAnd = false;
        if (priceType != null) {
            Integer min = 0;
            Integer max = 100;
            if (priceType == 'b') {
                min = 100;
                max = 150;
            } else if (priceType == 'c') {
                min = 150;
                max = 10000;
            }
            String fieldQuery = "price:" + "[" + min + " TO " + max + "]";
            sb.append(fieldQuery);
            needAnd = true;
        }
        if (areaType != null) {
            Integer min = 0;
            Integer max = 50;
            if (areaType == 'b') {
                min = 50;
                max = 100;
            } else if (areaType == 'c') {
                min = 100;
                max = 10000;
            }
            String fieldQuery = "area:" + "[" + min + " TO " + max + "]";
            if (needAnd) {
                sb.append(" AND ");
            }
            sb.append(fieldQuery);
            needAnd = true;
        }
        if (roomType != null) {
            Integer count = 1;
            if (roomType == 'b') {
                count = 2;
            } else if (roomType == 'c') {
                count = 3;
            } else if (roomType == 'd') {
                count = 4;
            } else if (roomType == 'f') {
                count = 5;
            }
            String fieldQuery = "room:" + count;
            if (needAnd) {
                sb.append(" AND ");
            }
            sb.append(fieldQuery);
        }
        return sb.toString();
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


    public void saveHouse(House house) {
        House forId = new House();
        houseMapper.insertAndGetIncId(forId);
        house.setId(forId.getId());
        handleTableName(house);
        houseMapper.insertHouse(house);
        houseMapper.insertHouseExtend(house);
        if (house.getImgUrls() != null) {
            for (String imgUrl : house.getImgUrls()) {
                houseMapper.insertHouseImg(house.getId(), imgUrl, house.getHouseImgTable());
            }
        }
        saveSolrIndex(house);
    }

    private void saveSolrIndex(House house) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", house.getId());
        document.addField("area", house.getArea());
        document.addField("price", house.getPrice().intValue());
        document.addField("room", house.getRoom());
        try {
            solrClient.add(document);
            solrClient.commit();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }

    @Cacheable(value = "houseDetail", keyGenerator = "keyGenerator")
    public House getHouseDetail(Integer id) {
        logger.info("cache not hit");
        House house = new House();
        house.setId(id);
        handleTableName(house);
        house = houseMapper.getHouseById(house);
        if (house != null) {
            house = getExtendInfo(house);
            house.setImgUrls(getHouseImgUrlList(house));
        }
        return house;
    }

    private List<String> getHouseImgUrlList(House house) {
        handleTableName(house);
        List<String> result = new ArrayList<>();
        List<String> imgNames = houseMapper.getHouseImgs(house);
        for (String imgName : imgNames) {
            result.add(handleHouseImgUrl(imgName));
        }
        return result;
    }

    private House getExtendInfo(House house) {
        handleTableName(house);
        House houseExt = houseMapper.getHouseExtendById(house);
        house.setAddress(houseExt.getAddress());
        house.setYear(houseExt.getYear());
        house.setComName(houseExt.getComName());
        house.setDes(houseExt.getDes());
        return house;
    }

    private House handleTableName(House house) {
        if (house == null || house.getId() == null) {
            return house;
        }
        Integer num = house.getId() % 2;
        house.setHouseTable("house" + num);
        house.setHouseExtTable("house_ext" + num);
        house.setHouseImgTable("house_img" + num);
        return house;
    }

}
