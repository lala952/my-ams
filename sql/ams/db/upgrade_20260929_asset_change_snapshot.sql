-- ----------------------------------------------------------------------------
-- 2026-09-29 资产变动：拟变更数据快照持久化
-- 给 asset_change 表新增 change_data 字段，落库存储拟变更资产数据快照，
-- 使 Redis 仅作为读缓存，Redis 异常或过期时降级到数据库快照兜底。
-- 说明：已部署的数据库直接执行本脚本；全新安装使用 ry-cloud-v1.sql（已含该字段）。
-- ----------------------------------------------------------------------------
ALTER TABLE `asset_change`
    ADD COLUMN `change_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NULL COMMENT '拟变更资产数据快照(JSON)' AFTER `business_status`;
