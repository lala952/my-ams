package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Change;
import com.ruoyi.asset.domain.vo.ChangeVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IChangeService extends IService<Change> {
    /**
     * 查询资产变动列表
     */
    List<ChangeVO> selectChangeList(Change change);

    /**
     * 各业务状态统计
     */
    Map<String, Integer> countByStatus();

    /**
     * 根据id查询资产变动数据
     */
    ChangeVO selectChangeById(Long id);

    /**
     * 插入资产变动数据
     */
    int insertChange(ChangeVO change);

    /**
     * 更新资产变动数据
     */
    int updateChange(ChangeVO change);

    /**
     * 删除资产变动
     * @param ids
     * @return
     */

    int deleteChangeByIds(Long[] ids);

    /**
     * 保存草稿
     * @param change
     * @return
     */

    Long saveDraft(ChangeVO change);

    /**
     * 提交资产变动申请
     * @param change
     * @return
     */
    Long submitChange(ChangeVO change);

    int approveChange(Long id, boolean result, String comment, Long approverId);

    int withdrawChange(Long id);

    void exportPdf(HttpServletResponse response, Long id) throws Exception;
}