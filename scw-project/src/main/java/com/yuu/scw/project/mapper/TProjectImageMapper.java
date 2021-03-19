package com.yuu.scw.project.mapper;

import com.yuu.scw.project.bean.TProjectImage;
import com.yuu.scw.project.bean.TProjectImageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TProjectImageMapper {
    long countByExample(TProjectImageExample example);

    int deleteByExample(TProjectImageExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TProjectImage record);

    int insertSelective(TProjectImage record);

    List<TProjectImage> selectByExample(TProjectImageExample example);

    TProjectImage selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TProjectImage record, @Param("example") TProjectImageExample example);

    int updateByExample(@Param("record") TProjectImage record, @Param("example") TProjectImageExample example);

    int updateByPrimaryKeySelective(TProjectImage record);

    int updateByPrimaryKey(TProjectImage record);
}