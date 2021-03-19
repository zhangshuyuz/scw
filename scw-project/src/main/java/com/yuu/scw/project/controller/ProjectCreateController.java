package com.yuu.scw.project.controller;


import com.alibaba.fastjson.JSON;
import com.yuu.scw.project.component.OssTemplate;
import com.yuu.scw.project.constant.ProjectConstant;
import com.yuu.scw.project.enums.ProjectStatusEnum;
import com.yuu.scw.project.service.ProjectService;
import com.yuu.scw.project.vo.req.BaseVO;
import com.yuu.scw.project.vo.req.ProjectBaseInfoVO;
import com.yuu.scw.project.vo.req.ProjectRedisStorageVO;
import com.yuu.scw.project.vo.req.ReturnVO;
import com.yuu.scw.common.vo.resp.AppResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Api(tags = "项目发起模块")
@RequestMapping("/project")
@RestController
public class ProjectCreateController {

    @Autowired
    OssTemplate ossTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ProjectService projectService;

    @ApiOperation(value = "1 - 项目初始创建")
    @PostMapping("/init")
    public AppResponse<Object> init(BaseVO baseVO) {

        try {
            // 获取accessToken
            String accessToken = baseVO.getAccessToken();

            if (StringUtils.isEmpty(accessToken)) {
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMsg("请求必须提供accessToken");
                return fail;
            }

            // 根据accessToken在Redis中查询用户
            String s = stringRedisTemplate.opsForValue().get(accessToken);

            if (StringUtils.isEmpty(s)) {
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMsg("请先登录，再发布项目");
                return fail;
            }

            // 将用户信息拷贝到ProjectRedisStorageVO
            ProjectRedisStorageVO projectRedisStorageVO = new ProjectRedisStorageVO();
            BeanUtils.copyProperties(baseVO, projectRedisStorageVO);

            // 生成项目Token，并封装到ProjectRedisStorageVO中
            String projectToken = UUID.randomUUID().toString().replaceAll("-", "");
            projectRedisStorageVO.setProjectToken(projectToken);

            // 序列化ProjectRedisStorageVO，并存储到Redis中
            String projectJson = JSON.toJSONString(projectRedisStorageVO);
            stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken, projectJson);

            log.debug("项目初始化成功-{}", projectRedisStorageVO);

            return AppResponse.ok(projectRedisStorageVO);
        } catch (Exception e) {
            e.printStackTrace();

            log.error("项目初始化失败-{}", e.getMessage());

            AppResponse<Object> fail = AppResponse.fail(null);
            fail.setMsg("项目初始化失败");
            return fail;
        }

    }

    @ApiOperation(value = "2 - 添加项目回报档位")
    @PostMapping("/baseinfo")
    public AppResponse<Object> baseinfo(ProjectBaseInfoVO projectBaseInfoVO) {

        try {
            // 获取accessToken
            String accessToken = projectBaseInfoVO.getAccessToken();

            if (StringUtils.isEmpty(accessToken)) {
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMsg("请求必须提供accessToken");
                return fail;
            }

            // 根据accessToken在Redis中查询用户
            String s = stringRedisTemplate.opsForValue().get(accessToken);

            if (StringUtils.isEmpty(s)) {
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMsg("请先登录，再发布项目");
                return fail;
            }

            // 从projectBaseInfoVO中获取项目Token
            String projectToken = projectBaseInfoVO.getProjectToken();

            if (StringUtils.isEmpty(projectToken)) {
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMsg("未传入项目Token，再发布项目");
                return fail;
            }

            // 根据项目Token获取Redis中的ProjectRedisStorageVO
            String projectJson = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);
            ProjectRedisStorageVO projectRedisStorageVO = JSON.parseObject(projectJson, ProjectRedisStorageVO.class);

            // 将ProjectBaseInfoVO中数据拷贝到ProjectRedisStorageVO中
            BeanUtils.copyProperties(projectBaseInfoVO, projectRedisStorageVO);

            // 将ProjectRedisStorageVO重新存入Redis
            projectJson = JSON.toJSONString(projectRedisStorageVO);
            stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken, projectJson);

            log.debug("数据写入成功-{}", projectRedisStorageVO);

            // 返回数据
            return AppResponse.ok(projectRedisStorageVO);
        } catch (BeansException e) {
            e.printStackTrace();

            log.error("数据写入失败-{}", e.getMessage());

            return AppResponse.fail(null);
        }

    }

    @ApiOperation(value = "3 - 添加项目回报档位")
    @PostMapping("/return")
    public AppResponse<Object> returnDetail(@RequestBody List<ReturnVO> returnVOList) {

        try {
            // 判断回报个数
            if (returnVOList.size() < 1) {
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMsg("回报个数至少为1");
                return fail;
            }

            // 获取accessToken
            String accessToken = returnVOList.get(0).getAccessToken();

            if (StringUtils.isEmpty(accessToken)) {
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMsg("请求必须提供accessToken");
                return fail;
            }

            // 根据accessToken在Redis中查询用户
            String s = stringRedisTemplate.opsForValue().get(accessToken);

            if (StringUtils.isEmpty(s)) {
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMsg("请先登录，再发布项目");
                return fail;
            }

            // 从returnVOList中获取项目Token
            String projectToken = returnVOList.get(0).getProjectToken();

            if (StringUtils.isEmpty(projectToken)) {
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMsg("未传入项目Token");
                return fail;
            }

            // 根据项目Token获取Redis中的ProjectRedisStorageVO
            String projectJson = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);
            ProjectRedisStorageVO projectRedisStorageVO = JSON.parseObject(projectJson, ProjectRedisStorageVO.class);

            // 将returnVOList中数据拷贝到ProjectRedisStorageVO中
            List<ReturnVO> projectReturnVOList = projectRedisStorageVO.getProjectReturns();
            for (ReturnVO returnVO :
                    returnVOList) {
                ReturnVO temp = new ReturnVO();
                BeanUtils.copyProperties(returnVO, temp);
                projectReturnVOList.add(temp);
            }

            // 将ProjectRedisStorageVO重新存入Redis
            projectJson = JSON.toJSONString(projectRedisStorageVO);
            stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken, projectJson);

            return AppResponse.ok(projectRedisStorageVO);
        } catch (Exception e) {
            e.printStackTrace();

            return AppResponse.fail(null);
        }

    }


    @ApiOperation(value = "上传图片")
    @PostMapping("/upload")
    public AppResponse<Object> upload(@RequestParam("uploadFiles") MultipartFile[] files) {

        try {
            // 存储上传成功文件的url
            List<String> filesUrl = new ArrayList<>();

            // 文件上传
            for (MultipartFile file :
                    files) {
                String fileUploadName = UUID.randomUUID().toString().replaceAll("-", "")
                        + "_" + file.getOriginalFilename();

                String upload = ossTemplate.upload(file.getInputStream(), fileUploadName);

                filesUrl.add(upload);
            }

            log.debug("文件上传成功-{}", filesUrl.toString());

            // 返回上传成功文件的url集合
            return AppResponse.ok(filesUrl);
        } catch (IOException e) {
            e.printStackTrace();

            log.error("文件上传失败-{}", e.getMessage());

            return AppResponse.fail(null);
        }

    }

    @ApiOperation(value = "4 - 项目提交审核申请")
    @PostMapping("/submit")
    public AppResponse<Object> submit(String accessToken, String projectToken, String ops) {

        try {
            // 根据accessToken在Redis中查询用户
            String s = stringRedisTemplate.opsForValue().get(accessToken);

            if (StringUtils.isEmpty(s)) {
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMsg("请先登录，再发布项目");
                return fail;
            }

            // 判断项目Token
            if (StringUtils.isEmpty(projectToken)) {
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMsg("未传入项目Token");
                return fail;
            }

            // 判断ops
            if ("0".equals(ops)) {
                // 保存草稿
                projectService.saveProject(accessToken, projectToken, ProjectStatusEnum.DRAFT.getCode());

                return AppResponse.ok("ok");
            } else if ("1".equals(ops)) {
                // 提交项目
                projectService.saveProject(accessToken, projectToken, ProjectStatusEnum.SUBMIT_AUTH.getCode());

                return AppResponse.ok("ok");
            } else {
                // ops参数错误
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMsg("ops参数错误");
                return fail;
            }
        } catch (Exception e) {
            e.printStackTrace();

            log.error("项目操作失败-{}", e.getMessage());

            AppResponse<Object> fail = AppResponse.fail(null);
            fail.setMsg("项目操作失败");
            return fail;
        }


    }

//    @ApiOperation(value = "删除项目回报档位")
//    @DeleteMapping("/return")
//    public AppResponse<Object> deleteReturnDetail() {
//        return AppResponse.ok("ok");
//    }


//    @ApiOperation(value = "确认项目法人信息")
//    @PostMapping("/confirm/legal")
//    public AppResponse<Object> legal() {
//        return AppResponse.ok("ok");
//    }


//    @ApiOperation(value = "5 - 项目草稿保存")
//    @PostMapping("/tempsave")
//    public AppResponse<Object> tempsave() {
//        return AppResponse.ok("ok");
//    }


}





