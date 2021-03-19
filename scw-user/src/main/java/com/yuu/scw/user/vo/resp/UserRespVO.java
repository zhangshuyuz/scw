package com.yuu.scw.user.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class UserRespVO {

    @ApiModelProperty("访问令牌，以后每次请求都必须带上该令牌")
    private String accessToken;

    private String loginacct;

    private String username;

    private String email;

    private String authstatus;

    private String usertype;

    private String realname;

    private String cardnum;

    private String accttype;

}
