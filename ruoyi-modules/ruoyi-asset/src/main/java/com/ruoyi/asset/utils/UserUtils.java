package com.ruoyi.asset.utils;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    private static final Logger log = LoggerFactory.getLogger(UserUtils.class);

    @Autowired
    private RemoteUserService userService;

    /**
     * 获取userId对应的SysUser并返回SysUser对象
     */
    public SysUser getSysUserById(Long userId) {
        R<SysUser> res = userService.getUserById(userId, SecurityConstants.INNER);
        if (res.getCode() == R.FAIL) {
            throw new ServiceException("调用远程RemoteUserService接口失败");
        }
        return res.getData();
    }

    /**
     * 获取用户名称（简化版本，仅用于展示）
     *
     * @param userId 用户ID
     * @return 用户名称字符串
     */
    public String getUserName(Long userId) {
        log.debug("尝试调用RemoteUserService接口获取数据，传递参数：{}", userId);
        R<SysUser> res = userService.getUserById(userId, SecurityConstants.INNER);
        log.debug("调用RemoteUserService接口获取数据->成功是200/失败是500，返回结果为：{}", res.getCode());
        if (res.getCode() == 200 && res.getData() != null) {
            log.debug("调用RemoteUserService接口获取数据->成功200，返回结果为：{}", res.getData());
            SysUser u = res.getData();
            log.debug("调用用户信息成功，SysUser对象里面的userName{}", res.getData().getUserName());
            return u.getUserName();
        }
        log.error("调用RemoteUserService接口获取数据->失败500，返回结果为：{}", res.getData());
        log.error("调用RemoteUserService接口获取数据->失败500，返回错误为：{}", res.getMsg());
        return "用户" + userId;
    }

    public String getDeptName(Long deptId) {
        log.debug("尝试调用RemoteUserService接口获取数据，传递参数：{}", deptId);
        R<SysUser> res = userService.getUserById(deptId, SecurityConstants.INNER);
        log.debug("调用RemoteUserService接口获取数据->成功200/失败500，返回结果为：{}", res.getCode());
        if (res.getCode() == 200 && res.getData() != null) {
            log.debug("调用RemoteUserService接口获取数据->成功200，返回结果为：{}", res.getData());
            String deptName = res.getData().getDept().getDeptName();
            return deptName;
        }
        return "用户" + deptId;
    }
}
