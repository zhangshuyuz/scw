package com.yuu.scw.project.vo.req;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ReturnVO extends BaseVO{

    // 从BaseVOA中继承AccessToken

    // 项目Token
    private String projectToken;

    // 回报信息
    private String type;

    private Integer supportmoney;

    private String content;

    private Integer count;

    private Integer signalpurchase;

    private Integer purchase;

    private Integer freight;

    private String invoice;

    private Integer rtndate;

}
