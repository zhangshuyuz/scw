package com.yuu.scw.user.controller;

import com.yuu.scw.user.component.SmsTemplate;
import com.yuu.scw.user.enums.UserExceptionEnum;
import com.yuu.scw.user.exception.UserException;
import com.yuu.scw.user.service.TMemberService;
import com.yuu.scw.user.vo.req.UserRegistVO;
import com.yuu.scw.user.vo.resp.UserRespVO;
import com.yuu.scw.common.vo.resp.AppResponse;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "用户登陆注册模块")
@RequestMapping("/user")
@RestController
public class UserLoginRegistController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    SmsTemplate smsTemplate;

    @Autowired
    TMemberService memberService;

    @ApiOperation(value="用户登陆")
    @ApiImplicitParams(value={
            @ApiImplicitParam(value="登陆账号",name="loginacct"),
            @ApiImplicitParam(value="用户密码",name="password")
    })
    @PostMapping("/login")
    public AppResponse<UserRespVO> login(String loginacct, String password){

        try {
            // 校验用户名
            if (StringUtils.isEmpty(loginacct)) {
                throw new UserException(UserExceptionEnum.USER_EMPTY);
            }

            // 校验密码
            if (StringUtils.isEmpty(password)) {
                throw new UserException(UserExceptionEnum.PASSWORD_EMPTY);
            }

            log.debug("登录表单数据：{}", loginacct);

            // 校验登录
            UserRespVO respVo = memberService.getUserByLogin(loginacct, password);

            log.debug("登录成功：{}", loginacct);

            return AppResponse.ok(respVo);
        } catch (Exception e) {
            e.printStackTrace();

            log.error("登录失败：{} -- {}",loginacct, e.getMessage());

            AppResponse fail = AppResponse.fail(null);
            fail.setMsg(e.getMessage());

            return fail;
        }

    }

    @ApiOperation(value="用户注册")
    @PostMapping("/register")
    public AppResponse<Object> register(UserRegistVO userRegistVO){

        // 获取用户名
        String loginacct = userRegistVO.getLoginacct();

        // 判断用户名是否为空
        if (StringUtils.isEmpty(loginacct)) {
            AppResponse<Object> fail = AppResponse.fail(null);
            fail.setMsg("用户名称不能为空");
            return fail;
        }

        // 获取验证码
        String code = stringRedisTemplate.opsForValue().get(loginacct);

        // 判断验证码是否为空
        if (StringUtils.isEmpty(code)) {
            AppResponse<Object> fail = AppResponse.fail(null);
            fail.setMsg("验证码已经过期");
            return fail;
        }

        // 判断验证码是否一致
        if (!code.equals(userRegistVO.getCode())) {
            AppResponse<Object> fail = AppResponse.fail(null);
            fail.setMsg("验证码不一致，请重新输入");
            return fail;
        }

        // 校验账号是否唯一

        // 校验email是否被占用

        // 保存数据
        int i = memberService.saveTMember(userRegistVO);

        if (i != 1) {
            return AppResponse.fail(null);
        }

        // 保存成功，销毁缓存中的验证码
        stringRedisTemplate.delete(loginacct);

        // 返回成功消息
        return AppResponse.ok("成功");

    }

    @ApiOperation(value="发送短信验证码")
    @PostMapping("/sendsms")
    public AppResponse<Object> sendsms(String loginacct){

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append(new Random().nextInt(10));
        }

        Map<String, String> querys = new HashMap<>();
        querys.put("mobile", loginacct);
        querys.put("param", "code:" + code.toString());
        querys.put("tpl_id", "TP1711063");

        smsTemplate.sendMessage(querys);

        stringRedisTemplate.opsForValue().set(loginacct, code.toString(), 5L, TimeUnit.MINUTES);

        log.debug("发送短信成功，验证码为{}", code.toString());

        return AppResponse.ok("ok");

    }

    @ApiOperation(value="验证短信验证码")
    @PostMapping("/valide")
    public AppResponse<Object> valide(){
        return AppResponse.ok("ok");
    }

    @ApiOperation(value="重置密码")
    @PostMapping("/reset")
    public AppResponse<Object> reset(){
        return AppResponse.ok("ok");
    }

}
