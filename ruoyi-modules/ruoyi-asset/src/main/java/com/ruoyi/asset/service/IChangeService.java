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

    List<ChangeVO> selectChangeList(Change change);

    Map<String, Integer> countByStatus();

    ChangeVO selectChangeById(Long id);

    int insertChange(ChangeVO change);

    int updateChange(ChangeVO change);

    int deleteChangeByIds(Long[] ids);

    Long saveDraft(ChangeVO change);

    Long submitChange(ChangeVO change);

    int approveChange(Long id, boolean result, String comment, Long approverId);

    int withdrawChange(Long id);

    void exportPdf(HttpServletResponse response, Long id) throws Exception;
}