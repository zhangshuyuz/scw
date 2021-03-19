package com.yuu.scw.project.vo.req;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 项目基本信息VO
 */
@Data
@ToString
public class ProjectBaseInfoVO extends BaseVO{

    // 用户Token继承自BaseVO


    // 项目Token
    private String projectToken;


    // 项目基本信息
    private List<Integer> typeId; // 项目分类id
    private List<Integer> tagIds; // 项目的标签id

    private String name; // 项目名称
    private String remark; // 项目简介
    private Integer money; // 项目资金
    private Integer day; // 筹集天数

    private String headImage; // 项目头部照片
    private List<String> detailsImages; // 项目详情图片


    // 发起人信息

}
