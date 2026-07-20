package com.ruoyi.asset.utils;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.utils.DictUtils;
import com.ruoyi.system.api.domain.SysDictData;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典标签转换工具类
 * 用于将字典值(dictValue)转换为字典标签(dictLabel)
 *
 * @author ruoyi
 *
 * 所有方法都为public static，方便直接类名加静态方法调用，静态方法不可继承或者修改
 */
public class DictLabelUtils {
    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型（如：change_type、asset_status等）
     * @param dictValue 字典值（如：INFO、1、draft等）
     * @return 字典标签，如果未找到则返回原值
     */
    public static String getDictLabel(String dictType, String dictValue) {
        // 1.参数校验
        if (StringUtils.isEmpty(dictType) || StringUtils.isEmpty(dictValue)) {
            return dictValue;
        }
        // 2.从缓冲中获取字典数据列表
        List<SysDictData> dictList = DictUtils.getDictCache(dictType);
        // 2.1 字典数据列表校验（空值情况下）
        if (dictList == null || dictList.isEmpty()) {
            return dictValue;
        }
        // 2.2 非空情况下，返回字典标签
        // 以stream的方式，筛选出传递过来的字典值所对应的字典标签，并返回第一个，然后map转为字典标签，如果未找到则返回原值
        return dictList.stream()
                .filter(d -> dictValue.equals(d.getDictValue()))
                .findFirst()
                .map(SysDictData::getDictLabel)
                .orElse(dictValue);
    }

    /**
     * 根据字典类型和字典值获取字典标签（带默认值）
     *
     * @param dictType     字典类型
     * @param dictValue    字典值
     * @param defaultValue 默认值（当字典值为空或未找到时使用）
     * @return 字典标签或默认值
     */
    public static String getDictLabel(String dictType, String dictValue, String defaultValue) {
        // 1.参数校验
        if (StringUtils.isEmpty(dictValue)) {
            return defaultValue;
        }
        // 2.返回字典标签
        String label = getDictLabel(dictType, dictValue);
        // 3.字典标签为空时，返回默认值
        return StringUtils.isEmpty(label) ? defaultValue : label;
    }

    /**
     * 批量转换字典值为标签
     *
     * @param dictType   字典类型
     * @param dictValues 字典值列表
     * @return 字典标签列表
     */
    public static List<String> getDictLabels(String dictType, List<String> dictValues) {
        // 1.参数校验
        if (dictValues == null || dictValues.isEmpty()) {
            return Collections.emptyList();
        }
        // 2.返回字典标签列表
        return dictValues.stream()
                .map(value -> getDictLabel(dictType, value))
                .collect(Collectors.toList());
    }
}
