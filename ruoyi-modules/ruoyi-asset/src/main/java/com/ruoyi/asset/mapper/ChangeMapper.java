package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Assets;
import com.ruoyi.asset.domain.Change;
import com.ruoyi.asset.domain.ChangeDetail;
import com.ruoyi.asset.domain.vo.ChangeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资产变动Mapper接口
 * 提供资产变动记录及其明细数据的增删改查操作
 *
 * @author wangqin
 * @date 2026-08-21
 */
public interface ChangeMapper extends BaseMapper<Change> {

    /**
     * 批量查询资产变动列表
     * 支持多条件组合查询，返回包含关联信息的视图对象
     *
     * @param change 资产变动查询条件对象，包含业务状态、变动类型、时间范围等筛选条件
     * @return 资产变动视图对象列表，包含变动主信息及关联的资产明细
     */
    List<ChangeVO> selectChangeList(Change change);

    /**
     * 根据业务状态统计变动记录数量
     *
     * @param status 业务状态编码（如：待审核、已通过、已驳回等）
     * @return 指定状态下的变动记录总数
     */
    Integer countByBusinessStatus(@Param("status") String status);

    /**
     * 根据主键ID查询资产变动详细信息
     * 返回包含变动主表及关联明细数据的完整视图对象
     *
     * @param id 变动记录主键ID
     * @return 资产变动视图对象，包含关联的资产明细列表；若不存在则返回null
     */
    ChangeVO selectChangeById(@Param("id") Long id);

    /**
     * 批量删除资产变动记录
     * 同时会级联删除关联的变动明细数据（需配合业务层事务处理）
     *
     * @param ids 待删除的变动记录主键ID数组
     * @return 实际删除的记录条数
     */
    int deleteChangeByIds(Long[] ids);

    /**
     * 批量插入资产变动明细数据
     *
     * @param detailList 变动明细对象列表，每个对象需包含资产ID、变动类型、变动数量等信息
     * @return 实际插入的明细记录条数
     */
    int batchInsertDetail(List<ChangeDetail> detailList);

    /**
     * 根据主表ID查询关联的变动明细列表
     *
     * @param masterId 变动主表ID
     * @return 该变动单下的所有明细记录列表，按创建时间降序排列
     */
    List<ChangeDetail> selectDetailListByMasterId(@Param("masterId") Long masterId);

    /**
     * 根据主表ID删除所有关联的变动明细数据
     * 通常在更新变动单时先删除旧明细再插入新明细使用
     *
     * @param masterId 变动主表ID
     * @return 实际删除的明细记录条数
     */
    int deleteDetailByMasterId(@Param("masterId") Long masterId);

    /**
     * 根据变动ID查询关联的资产列表
     * 返回该变动单所涉及的所有资产信息
     *
     * @param changeId 变动记录ID
     * @return 关联的资产对象列表，包含资产编号、名称、规格、原值等完整信息
     */
    List<Assets> selectAssetsByChangeId(@Param("changeId") Long changeId);
}