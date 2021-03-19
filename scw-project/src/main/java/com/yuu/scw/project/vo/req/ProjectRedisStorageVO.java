package com.yuu.scw.project.vo.req;

import com.yuu.scw.project.bean.TReturn;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装整个项目信息的VO
 */
@Data
@ToString
public class ProjectRedisStorageVO extends BaseVO{

    public ProjectRedisStorageVO() {
        this.init();
    }

    public ProjectRedisStorageVO(String projectToken, List<Integer> typeId, List<Integer> tagIds, String name, String remark, Integer money, Integer day, String headImage, List<String> detailsImages, List<ReturnVO> projectReturns) {
        ProjectToken = projectToken;
        this.typeId = typeId;
        this.tagIds = tagIds;
        this.name = name;
        this.remark = remark;
        this.money = money;
        this.day = day;
        this.headImage = headImage;
        this.detailsImages = detailsImages;
        this.projectReturns = projectReturns;
        init();
    }

    private void init() {
        this.projectReturns = new ArrayList<>();
        this.typeId = new ArrayList<>();
        this.tagIds = new ArrayList<>();
        this.detailsImages = new ArrayList<>();
    }

    // 项目相关属性
    private String ProjectToken; // 项目Token

    // 项目基本信息
    private List<Integer> typeId; // 项目分类id
    private List<Integer> tagIds; // 项目的标签id

    private String name; // 项目名称
    private String remark; // 项目简介
    private Integer money; // 项目资金
    private Integer day; // 筹集天数

    private String headImage; // 项目头部照片
    private List<String> detailsImages; // 项目详情图片


    // 发起人相关属性


    // 回报信息
    private List<ReturnVO> projectReturns; // 项目回报


    // 法人信息

}
