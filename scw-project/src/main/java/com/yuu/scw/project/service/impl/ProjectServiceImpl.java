package com.yuu.scw.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.netflix.discovery.converters.Auto;
import com.yuu.scw.common.util.AppDateUtils;
import com.yuu.scw.project.bean.*;
import com.yuu.scw.project.constant.ProjectConstant;
import com.yuu.scw.project.mapper.*;
import com.yuu.scw.project.service.ProjectService;
import com.yuu.scw.project.vo.req.ProjectRedisStorageVO;
import com.yuu.scw.project.vo.req.ReturnVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    TProjectMapper projectMapper;

    @Autowired
    TProjectImageMapper projectImageMapper;

    @Autowired
    TReturnMapper returnMapper;

    @Autowired
    TProjectTypeMapper projectTypeMapper;

    @Autowired
    TProjectTagMapper projectTagMapper;

    @Transactional
    @Override
    public void saveProject(String accessToken, String projectToken, byte code) {

        // 从Redis中获取用户id
        String memberId = stringRedisTemplate.opsForValue().get(accessToken);


        // 从Redis中获取整个项目的信息
        String projectJson = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);
        ProjectRedisStorageVO projectRedisStorageVO = JSON.parseObject(projectJson, ProjectRedisStorageVO.class);


        // 保存项目基本信息
        TProject project = new TProject();
        BeanUtils.copyProperties(projectRedisStorageVO, project);
        project.setStatus(code + "");
        project.setMemberid(Integer.parseInt(memberId));
        project.setCreatedate(AppDateUtils.getFormatTime());

        projectMapper.insertSelective(project);


        // 根据主键回填，获取项目的id
        Integer projectId = project.getId();
        log.debug("主键回填ID为-{}", projectId);


        // 保存图片
        String headImage = projectRedisStorageVO.getHeadImage();
        TProjectImage projectImage = new TProjectImage();
        projectImage.setProjectid(projectId);
        projectImage.setImgurl(headImage);
        projectImage.setImgtype((byte)0);
        projectImageMapper.insertSelective(projectImage);
        
        List<String> detailImages = projectRedisStorageVO.getDetailsImages();
        for (String detailImage :
                detailImages) {
            TProjectImage image = new TProjectImage();
            image.setProjectid(projectId);
            image.setImgurl(detailImage);
            image.setImgtype((byte)1);
            projectImageMapper.insertSelective(image);
        }


        // 保存回报
        List<ReturnVO> projectReturns = projectRedisStorageVO.getProjectReturns();
        for (ReturnVO returnVO :
                projectReturns) {
            TReturn tReturn = new TReturn();
            tReturn.setProjectid(projectId);
            BeanUtils.copyProperties(returnVO, tReturn);
            returnMapper.insertSelective(tReturn);
        }


        // 保存项目和分类关系
        List<Integer> typeIds = projectRedisStorageVO.getTypeId();
        for (Integer typeId :
                typeIds) {
            TProjectType projectType = new TProjectType();
            projectType.setProjectid(projectId);
            projectType.setTypeid(typeId);
            projectTypeMapper.insertSelective(projectType);
        }


        // 保存项目和标签关系
        List<Integer> tagIds = projectRedisStorageVO.getTagIds();
        for (Integer tagId:
                tagIds) {
            TProjectTag projectTag = new TProjectTag();
            projectTag.setProjectid(projectId);
            projectTag.setTagid(tagId);
            projectTagMapper.insertSelective(projectTag);
        }

        // 保存发起人

        // 保存法人

        // 从Redis中删除项目
        stringRedisTemplate.delete(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);
    }
}
