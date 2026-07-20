package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Assets;
import com.ruoyi.asset.domain.Change;
import com.ruoyi.asset.domain.ChangeDetail;
import com.ruoyi.asset.domain.vo.ChangeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ChangeMapper extends BaseMapper<Change> {

    List<ChangeVO> selectChangeList(Change change);

    /**
     * 统计各审批状态数量（关联工作流表）
     */
    Map<String, Integer> countByBusinessStatus();

    ChangeVO selectChangeById(@Param("id") Long id);

    int deleteChangeByIds(Long[] ids);

    int batchInsertDetail(List<ChangeDetail> detailList);

    List<ChangeDetail> selectDetailListByMasterId(@Param("masterId") Long masterId);

    int deleteDetailByMasterId(@Param("masterId") Long masterId);

    List<Assets> selectAssetsByChangeId(@Param("changeId") Long changeId);
}