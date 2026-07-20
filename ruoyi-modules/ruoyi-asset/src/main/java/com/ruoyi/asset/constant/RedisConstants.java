package com.ruoyi.asset.constant;

public class RedisConstants {
    /**
     * 资产变动草稿前缀
     */
    public static final String ASSET_CHANGE_DRAFT_PREFIX = "asset:change:draft:";

    /**
     * 资产变动草稿过期时间（天）
     */
    public static final long ASSET_CHANGE_DRAFT_TTL = 7;

    public static final String ASSET_SPLIT_DRAFT_PREFIX = "asset:split:draft:";
    public static final int ASSET_SPLIT_DRAFT_TTL = 7;

    /** 空值缓存TTL（防缓存穿透） */
    public static final Long CACHE_NULL_TTL = 2L;

    /** 默认缓存TTL */
    public static final Long CACHE_DEFAULT_TTL = 30L;

    /** AMS业务分布式锁Key前缀 */
    public static final String LOCK_AMS_KEY = "lock:ams:" ;

    public static final String ASSET_KEY = "ams:asset:";
    public static final String CHANGE_KEY = "ams:change:";

    public static final String ACCEPTANCE_KEY = "ams:acceptance:";
    public static final String ATTACHMENT_KEY = "ams:attachment:";
    public static final String CAPITALIZE_KEY = "ams:capitalize:";
}
