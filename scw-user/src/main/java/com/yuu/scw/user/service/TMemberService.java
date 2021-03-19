package com.yuu.scw.user.service;

import com.yuu.scw.user.vo.req.UserRegistVO;
import com.yuu.scw.user.vo.resp.UserRespVO;

public interface TMemberService {

    int saveTMember(UserRegistVO userRegistVO);

    UserRespVO getUserByLogin(String loginacct, String password);
    
}
