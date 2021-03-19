package com.yuu.scw.user.service.impl;

import com.yuu.scw.user.bean.TMember;
import com.yuu.scw.user.bean.TMemberExample;
import com.yuu.scw.user.enums.UserExceptionEnum;
import com.yuu.scw.user.exception.UserException;
import com.yuu.scw.user.mapper.TMemberMapper;
import com.yuu.scw.user.service.TMemberService;
import com.yuu.scw.user.vo.req.UserRegistVO;
import com.yuu.scw.user.vo.resp.UserRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
public class TMemberServiceImpl implements TMemberService {

    @Autowired
    TMemberMapper memberMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    @Override
    public int saveTMember(UserRegistVO userRegistVO) {

        try {
            // 将vo对拷到do
            TMember member = new TMember();
            BeanUtils.copyProperties(userRegistVO, member);
            member.setUsername(userRegistVO.getLoginacct());

            // 密码加密
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String password = userRegistVO.getUserpswd();
            String encodePassword = bCryptPasswordEncoder.encode(password);
            member.setUserpswd(encodePassword);

            // 保存数据
            int i = memberMapper.insertSelective(member);

            log.debug("注册会员成功:{}", member.toString());

            return i;
        } catch (Exception e) {
            e.printStackTrace();

            log.error("注册会员失败：{}", e.getMessage());

            throw new UserException(UserExceptionEnum.USER_SAVE_ERROR);
        }

    }

    @Override
    public UserRespVO getUserByLogin(String loginacct, String password) {

        UserRespVO userRespVO = new UserRespVO();

        // 获取用户
        TMemberExample memberExample = new TMemberExample();
        memberExample.createCriteria().andLoginacctEqualTo(loginacct);
        List<TMember> members = memberMapper.selectByExample(memberExample);

        if (members == null || members.size() != 1) {
            throw new UserException(UserExceptionEnum.USER_NOT_EXIT);
        }

        TMember member = members.get(0);

        // 判断密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(password, member.getUserpswd())) {
            throw new UserException(UserExceptionEnum.USER_PASSWORD_ERROR);
        }

        // 保存用户信息
        BeanUtils.copyProperties(member, userRespVO);

        // 设置accessToken
        String accessToken = UUID.randomUUID().toString().replaceAll("-", "");
        userRespVO.setAccessToken(accessToken);

        // 保存accessToken和用户id的关系到缓存
        stringRedisTemplate.opsForValue().set(accessToken, member.getId().toString());

        // 返回用户信息
        return userRespVO;

    }

}
