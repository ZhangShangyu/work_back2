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
import java.util.Random;


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
                handleTableName(house);
                String imgName = houseMapper.getHeadImg(house);
                house.setHeadImg(handleHouseImgUrl(imgName));
                result.add(house);
            }
        }

        return result;
    }

    public void genTestData(Integer count) {
        Runnable runner = () -> {
            for (int i = 0; i < count; ++i) {
                House house = getTestHouse();
                saveHouse(house);
                if (i % 1000 == 0) {
                    logger.info("finish " + i);
                }
            }

        };
        new Thread(runner).start();
    }

    private House getTestHouse() {
        House house = new House();
        house.setName(getName());
        house.setArea(getRandom(0, 150));
        house.setRoom(getRandom(1, 5));
        house.setHall(getRandom(0, 4));
        house.setPrice((float) getRandom(10, 2000));
        house.setPosition(getRandom(0, 20));
        house.setAllPos(getRandom(20, 30));
        house.setYear(getRandom(1990, 2020));
        house.setComName(getName());
        house.setDes(getName());

        String[] address = {"杨浦", "黄浦", "静安", "闵行", "嘉定", "浦东", "宝山", "徐汇"};
        house.setAddress(address[getRandom(0, 8)]);
        String[] imgs = {"https://pic1.ajkimg.com/display/xinfang/bf4a9ccb39e707ce3de7e0f53a514508/180x135m.jpg",
                "https://pic1.ajkimg.com/display/xinfang/af05eac6e92d659be827ba2c9d5796f4/180x135m.jpg",
                "https://pic1.ajkimg.com/display/xinfang/1fce8b54f922521061914b2ea35ecbfa/180x135m.jpg",
                "https://pic1.ajkimg.com/display/xinfang/52e178897960bc3f9225f8ed925fffd7/180x135m.jpg",
                "https://pic1.ajkimg.com/display/xinfang/9dc660e0be8f83f1495a8227b8f63a13/180x135m.jpg",
                "https://pic1.ajkimg.com/display/xinfang/e648c0f51085c9d44b2a4e05f1e68cbe/180x135m.jpg",
                "https://pic1.ajkimg.com/display/xinfang/0583cd060291deafcb2367fa0d3978cf/180x135m.jpg",
                "https://pic1.ajkimg.com/display/aifang/b7a0141af6b4e8b2c7b40901dd24f08b/180x135c.jpg",
                "https://pic1.ajkimg.com/display/aifang/ed526939e5a70956f925affd1ab20ad6/180x135c.jpg",
                "https://pic1.ajkimg.com/display/aifang/52e178897960bc3f9225f8ed925fffd7/133x100c.jpg",
                "https://pic1.ajkimg.com/display/aifang/e648c0f51085c9d44b2a4e05f1e68cbe/133x100c.jpg",
                "https://pic1.ajkimg.com/display/aifang/a36321b0f3ff8cf2c878ba7dc928d856/133x100c.jpg"};

        List<String> _imgs = new ArrayList<>();
        _imgs.add(imgs[getRandom(0, 5)]);
        _imgs.add(imgs[getRandom(6, 11)]);

        house.setImgUrls(_imgs);

        return house;
    }

    private int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    private String getName() {
        String name = "保利建工西郊锦庐首创旭辉城绿地海湾宫园巧筑天健萃园上海长滩城";
        return String.valueOf(name.charAt(getRandom(0, 30))) +
                name.charAt(getRandom(0, 30)) +
                name.charAt(getRandom(0, 30)) +
                name.charAt(getRandom(0, 30));
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
        String sortKey = getSortKey(condition);
        if (searchKey == null) {
            searchKey = "*:*";
        }
        SolrQuery solrQuery = new SolrQuery(searchKey);
        solrQuery.set("sort", sortKey);
        solrQuery.set("start", (condition.getPageNum() - 1) * condition.getRows());
        solrQuery.set("rows", condition.getRows());
        solrQuery.set("fl", "id");
        return solrQuery;
    }

    private String getSortKey(HouseCondition condition) {
        Character sortType = condition.getSortType();
        String sortKey = "id desc";
        if (sortType == null) {
            return sortKey;
        } else if (sortType == 'b') {
            return "area desc";
        } else if (sortType == 'c') {
            return "price desc";
        } else {
            return sortKey;
        }
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
