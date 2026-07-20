/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.186.128
 Source Server Type    : MySQL
 Source Server Version : 80044
 Source Host           : 192.168.186.128:3306
 Source Schema         : ry-config

 Target Server Type    : MySQL
 Target Server Version : 80044
 File Encoding         : 65001

 Date: 17/07/2026 14:59:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
DROP TABLE IF EXISTS `config_info`;
CREATE TABLE `config_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'group_id',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'app_name',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'configuration description',
  `c_use` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'configuration usage',
  `effect` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '配置生效的描述',
  `type` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '配置的类型',
  `c_schema` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL COMMENT '配置的模式',
  `encrypted_data_key` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT '密钥',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfo_datagrouptenant`(`data_id` ASC, `group_id` ASC, `tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 65 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = 'config_info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info
-- ----------------------------
INSERT INTO `config_info` VALUES (1, 'application-dev.yml', 'DEFAULT_GROUP', 'spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n  compression:\n    request:\n      enabled: true\n      min-request-size: 8192\n    response:\n      enabled: true\n\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\n', '9928f41dfb10386ad38b3254af5692e0', '2020-05-20 12:00:00', '2026-07-17 13:47:48', NULL, '192.168.186.1', '', '', '通用配置', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` VALUES (2, 'ruoyi-gateway-dev.yml', 'DEFAULT_GROUP', 'spring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: ruoyi-auth\n          uri: lb://ruoyi-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestBody\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: ruoyi-gen\n          uri: lb://ruoyi-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 定时任务\n        - id: ruoyi-job\n          uri: lb://ruoyi-job\n          predicates:\n            - Path=/schedule/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: ruoyi-system\n          uri: lb://ruoyi-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: ruoyi-file\n          uri: lb://ruoyi-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n        # 资产管理\n        - id: ruoyi-asset\n          uri: lb://ruoyi-asset\n          predicates:\n            - Path=/asset/**\n          filters:\n            - StripPrefix=1\n        # 资产工作流\n        - id: ruoyi-workflow\n          uri: lb://ruoyi-workflow\n          predicates:\n            - Path=/workflow/**\n          filters:\n            - StripPrefix=0\n        \n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /*/v3/api-docs\n      - /csrf\n\n# springdoc配置\nspringdoc:\n  webjars:\n    # 访问前缀\n    prefix:\n', '5a50257d9c8cc17e49cd43de278af1e9', '2020-05-14 14:17:55', '2026-07-17 13:48:30', NULL, '192.168.186.1', '', '', '网关模块', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` VALUES (3, 'ruoyi-auth-dev.yml', 'DEFAULT_GROUP', 'spring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n', '5d5c86d795ea9e70219b518206b4d083', '2020-11-20 00:00:00', '2026-07-17 13:48:39', NULL, '192.168.186.1', '', '', '认证中心', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` VALUES (4, 'ruoyi-monitor-dev.yml', 'DEFAULT_GROUP', '# spring\nspring:\n  security:\n    user:\n      name: ruoyi\n      password: 123456\n  boot:\n    admin:\n      ui:\n        title: 若依服务状态监控\n', '6f122fd2bfb8d45f858e7d6529a9cd44', '2020-11-20 00:00:00', '2026-07-17 13:48:48', NULL, '192.168.186.1', '', '', '监控中心', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` VALUES (5, 'ruoyi-system-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径（多个包用逗号分隔）\n  type-aliases-package: com.ruoyi.system.domain,com.ruoyi.system.api.domain\n  # mapper xml 文件路径\n  mapper-locations: classpath*:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      id-type: auto\n      # 逻辑删除字段\n      logic-delete-field: delFlag\n      # 逻辑删除值（不存在）\n      logic-delete-value: 2 \n      # 逻辑删除值 存在\n      logic-not-delete-value: 0\n       # 插入时，字段为 null 则不生成该列\n      insert-strategy: NOT_NULL\n      # 更新时，字段为 null 则不生成该列\n      update-strategy: NOT_NULL\n  configuration:\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    enabled: true\n  info:\n    title: \'系统模块接口文档\'\n    description: \'系统模块接口描述\'\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip', 'e02aac88e1f531b242221bf462fbbe58', '2020-11-20 00:00:00', '2026-07-17 13:59:50', NULL, '192.168.186.1', '', '', '系统模块', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` VALUES (6, 'ruoyi-gen-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# ruoyi-gen-dev.yml\nmybatis-plus:\n  type-aliases-package: com.ruoyi.gen.domain\n  mapper-locations: classpath*:mapper/**/*.xml\n  global-config:\n    db-config:\n      id-type: auto\n  configuration:\n    map-underscore-to-camel-case: true\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    # 是否开启接口文档\n    enabled: true\n  info:\n    # 标题\n    title: \'代码生成接口文档\'\n    # 描述\n    description: \'代码生成接口描述\'\n    # 作者信息\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n\n# 代码生成\ngen:\n  # 作者\n  author: xiaowang\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.ruoyi.asset\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_,tb_,asset_\n  # 是否允许生成文件覆盖到本地（自定义路径），默认不允许\n  allowOverwrite: false', '99a46159d1b2bd98f08c7ddaa10a283d', '2020-11-20 00:00:00', '2026-07-17 14:00:04', NULL, '192.168.186.1', '', '', '代码生成', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` VALUES (7, 'ruoyi-job-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径（多个包用逗号分隔）\n  type-aliases-package: com.ruoyi.job.domain,com.ruoyi.system.api.domain\n  # mapper xml 文件路径\n  mapper-locations: classpath*:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      id-type: auto\n      logic-delete-field: delFlag\n      logic-delete-value: 2\n      logic-not-delete-value: 0\n  configuration:\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    # 是否开启接口文档\n    enabled: true\n  info:\n    # 标题\n    title: \'定时任务接口文档\'\n    # 描述\n    description: \'定时任务接口描述\'\n    # 作者信息\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n', 'ac4cbc49cdf31f8f2f0b8fa3241d68d4', '2020-11-20 00:00:00', '2026-07-09 11:28:12', NULL, '192.168.186.1', '', '', '定时任务', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` VALUES (8, 'ruoyi-file-dev.yml', 'DEFAULT_GROUP', '# 本地文件上传    \nfile:\n    domain: http://127.0.0.1:9300\n    path: E:\\study\\Backend\\Devolopment\\Asset-Management-System\\ruoyi\\uploadPath\n    prefix: /statics\n\n# FastDFS配置\nfdfs:\n  domain: http://127.0.0.1\n  soTimeout: 3000\n  connectTimeout: 2000\n  trackerList: 127.0.0.1:22122\n\n# Minio配置\nminio:\n  url: http://127.0.0.1:9000\n  accessKey: minioadmin\n  secretKey: minioadmin\n  bucketName: test\n\n  # 防盗链配置\nreferer:\n  # 防盗链开关\n  enabled: false\n  # 允许的域名列表\n  allowed-domains: localhost,127.0.0.1,ruoyi.vip,www.ruoyi.vip\n\n# 文件上传\ndromara:\n  x-file-storage: # 文件存储配置\n    default-platform: aliyun-oss-1 # 默认使用的存储平台\n  thumbnail-suffix: \".min.jpg\" # 缩略图后缀\n  # 对应平台的配置\n  aliyun-oss:\n  - platform: aliyun-oss-1 # 存储平台标识\n    enable-storage: false  # 启用存储\n    access-key: LTAI5t5nnJToKvfFMSDrrD5w\n    secret-key: CmlygBpbMO20keS04MiwZM5w5PDDKV\n    end-point: oss-cn-chengdu.aliyuncs.com\n    bucket-name: asset-management-system-ruoyi\n    domain: https://asset-management-system-ruoyi.oss-cn-chengdu.aliyuncs.com/ # 访问域名，注意“/”结尾，例如：https://abc.oss-cn-shanghai.aliyuncs.com/\n    base-path: ams/ # 基础路径\n  minio:\n  - platform: minio-1 # 存储平台标识\n    enable-storage: false  # 启用存储\n    access-key: ??\n    secret-key: ??\n    end-point: ??\n    bucket-name: ??\n    domain: ?? # 访问域名，注意“/”结尾，例如：http://minio.abc.com/abc/\n    base-path: test/ # 基础路径\n', '9e6adf49e0e0d8b21dc0a25734796f20', '2020-11-20 00:00:00', '2026-06-08 06:06:09', 'nacos', '192.168.186.1', '', '', '文件服务', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` VALUES (9, 'sentinel-ruoyi-gateway', 'DEFAULT_GROUP', '[\n    {\n        \"resource\": \"ruoyi-auth\",\n        \"count\": 500,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"ruoyi-system\",\n        \"count\": 1000,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"ruoyi-gen\",\n        \"count\": 200,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"ruoyi-job\",\n        \"count\": 300,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    }\n]', '411936d945573749e5956f2df0b04989', '2020-11-20 00:00:00', '2026-07-09 11:27:17', NULL, '192.168.186.1', '', '', '限流策略', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` VALUES (20, 'ruoyi-asset-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  application:\n    name: ruoyi-asset\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n        master:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true\n          username: root\n          password: 123456\n  cloud:\n    sentinel:\n      transport:\n        dashboard: localhost:8090\n    http-method-specify: true\n  feign:\n    sentinel:\n      enabled: true\n  rabbitmq:\n    host: 192.168.186.128\n    port: 5672\n    virtual-host: /\n    username: admin\n    password: 123456\n    connection-timeout: 30000\n    publisher-confirm-type: simple\n    publisher-returns: true\n    template:\n      mandatory: true\n    listener:\n      simple:\n        acknowledge-mode: auto\n        prefetch: 1\n\nserver:\n  tomcat:\n    threads:\n      max: 50\n    accept-count: 50\n    max-connections: 100\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径\n  typeAliasesPackage: com.ruoyi.asset.domain\n  # mapper xml文件路径\n  mapperLocations: classpath:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      # 主键类型 AUTO自增 ID_WORKER雪花算法\n      id-type: auto\n      # 逻辑删除配置\n      logic-delete-field: delFlag\n      # 逻辑删除值\n      logic-delete-value: 2\n      # 逻辑未删除值\n      logic-not-delete-value: 0\n      # 表名前缀（如需全局统一前缀可配置）\n      # table-prefix: t_\n    # 刷新mapper\n    refresh-mapper: true\n  # 配置控制台打印SQL\n  configuration:\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    map-underscore-to-camel-case: true\n    cache-enabled: false\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    enabled: true\n  info:\n    title: \'资产管理模块接口文档\'\n    description: \'资产管理模块接口描述\'\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n\n# 日志配置\nlogging:\n  level:\n    com.ruoyi.asset: debug\n    # MyBatis-Plus SQL日志\n    com.baomidou.mybatisplus: debug', 'ec2ceabfc7055dd6d1c14b53c08c3f4e', '2026-03-29 02:41:38', '2026-07-17 14:18:07', NULL, '192.168.186.1', '', '', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (43, 'ruoyi-workflow-dev.yml', 'DEFAULT_GROUP', 'spring:\n  application:\n    name: ruoyi-workflow\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n        master:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true\n          username: root\n          password: 123456\n  cloud:\n    nacos:\n      discovery:\n        server-addr: 192.168.186.128:8848\n      config:\n        server-addr: 192.168.186.128:8848\n        file-extension: yml\n    sentinel:\n      transport:\n        dashboard: 192.168.186.128:8718\n  activiti:\n    database-schema-update: true\n    db-history-used: true\n    history-level: full\n    check-process-definitions: true\n    async-executor-activate: true\n\n# Sentinel Feign 配置\nfeign:\n  sentinel:\n    enabled: true\n\n# HTTP 方法配置\nspringfox:\n  documentation:\n    auto-startup: false\n\nserver:\n  port: 9304\n  tomcat:\n    threads:\n      max: 50\n    accept-count: 50\n    max-connections: 100\n\n# mybatis配置\nmybatis:\n  typeAliasesPackage: com.ruoyi.workflow\n  mapperLocations: classpath:mapper/**/*.xml\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    enabled: true\n  info:\n    title: \'工作流模块接口文档\'\n    description: \'工作流模块接口描述\'\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n\n# 日志配置\nlogging:\n  level:\n    com.ruoyi.workflow: debug\n    org.activiti: info\n', '46ca8ed7d8b4026f6340a521d178ca1f', '2026-04-14 05:39:02', '2026-04-15 13:18:05', 'nacos', '192.168.186.1', '', '', '', '', '', 'yaml', '', '');

-- ----------------------------
-- Table structure for config_info_aggr
-- ----------------------------
DROP TABLE IF EXISTS `config_info_aggr`;
CREATE TABLE `config_info_aggr`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'datum_id',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfoaggr_datagrouptenantdatum`(`data_id` ASC, `group_id` ASC, `tenant_id` ASC, `datum_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '增加租户字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_aggr
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_beta
-- ----------------------------
DROP TABLE IF EXISTS `config_info_beta`;
CREATE TABLE `config_info_beta`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT '密钥',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfobeta_datagrouptenant`(`data_id` ASC, `group_id` ASC, `tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = 'config_info_beta' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_beta
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_gray
-- ----------------------------
DROP TABLE IF EXISTS `config_info_gray`;
CREATE TABLE `config_info_gray`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'group_id',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'md5',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT 'src_user',
  `src_ip` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'src_ip',
  `gmt_create` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'gmt_create',
  `gmt_modified` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'gmt_modified',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT 'app_name',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT 'tenant_id',
  `gray_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'gray_name',
  `gray_rule` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'gray_rule',
  `encrypted_data_key` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT 'encrypted_data_key',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfogray_datagrouptenantgray`(`data_id` ASC, `group_id` ASC, `tenant_id` ASC, `gray_name` ASC) USING BTREE,
  INDEX `idx_dataid_gmt_modified`(`data_id` ASC, `gmt_modified` ASC) USING BTREE,
  INDEX `idx_gmt_modified`(`gmt_modified` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'config_info_gray' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_gray
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_tag
-- ----------------------------
DROP TABLE IF EXISTS `config_info_tag`;
CREATE TABLE `config_info_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfotag_datagrouptenanttag`(`data_id` ASC, `group_id` ASC, `tenant_id` ASC, `tag_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = 'config_info_tag' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_tag
-- ----------------------------

-- ----------------------------
-- Table structure for config_tags_relation
-- ----------------------------
DROP TABLE IF EXISTS `config_tags_relation`;
CREATE TABLE `config_tags_relation`  (
  `id` bigint NOT NULL COMMENT 'id',
  `tag_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增长标识',
  PRIMARY KEY (`nid`) USING BTREE,
  UNIQUE INDEX `uk_configtagrelation_configidtag`(`id` ASC, `tag_name` ASC, `tag_type` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = 'config_tag_relation' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_tags_relation
-- ----------------------------

-- ----------------------------
-- Table structure for group_capacity
-- ----------------------------
DROP TABLE IF EXISTS `group_capacity`;
CREATE TABLE `group_capacity`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_id`(`group_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '集群、各Group容量信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for his_config_info
-- ----------------------------
DROP TABLE IF EXISTS `his_config_info`;
CREATE TABLE `his_config_info`  (
  `id` bigint UNSIGNED NOT NULL COMMENT 'id',
  `nid` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增标识',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'source ip',
  `op_type` char(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'operation type',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT '密钥',
  `publish_type` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT 'formal' COMMENT 'publish type gray or formal',
  `gray_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'gray name',
  `ext_info` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL COMMENT 'ext info',
  PRIMARY KEY (`nid`) USING BTREE,
  INDEX `idx_gmt_create`(`gmt_create` ASC) USING BTREE,
  INDEX `idx_gmt_modified`(`gmt_modified` ASC) USING BTREE,
  INDEX `idx_did`(`data_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 73 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '多租户改造' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of his_config_info
-- ----------------------------
INSERT INTO `his_config_info` VALUES (5, 56, 'ruoyi-system-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径（多个包用逗号分隔）\n  type-aliases-package: com.ruoyi.system.domain,com.ruoyi.system.api.domain\n  # mapper xml 文件路径\n  mapper-locations: classpath*:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      id-type: auto\n      logic-delete-field: delFlag\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n  configuration:\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    enabled: true\n  info:\n    title: \'系统模块接口文档\'\n    description: \'系统模块接口描述\'\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip', 'cb1262828be3f30ca9e1afdfc1ed35a5', '2026-06-30 17:07:05', '2026-06-30 09:07:05', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"src_user\":\"nacos\",\"c_desc\":\"系统模块\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (5, 57, 'ruoyi-system-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径（多个包用逗号分隔）\n  type-aliases-package: com.ruoyi.system.domain,com.ruoyi.system.api.domain\n  # mapper xml 文件路径\n  mapper-locations: classpath*:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      id-type: auto\n      logic-delete-field: delFlag\n      # 逻辑删除值（不存在）\n      logic-delete-value: 2 \n      # 逻辑删除值 存在\n      logic-not-delete-value: 0\n  configuration:\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    enabled: true\n  info:\n    title: \'系统模块接口文档\'\n    description: \'系统模块接口描述\'\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip', 'e53b5a0bd2823aed6a52d408412c0e09', '2026-06-30 17:24:51', '2026-06-30 09:24:51', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"c_desc\":\"系统模块\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (5, 58, 'ruoyi-system-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径（多个包用逗号分隔）\n  type-aliases-package: com.ruoyi.system.domain,com.ruoyi.system.api.domain\n  # mapper xml 文件路径\n  mapper-locations: classpath*:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      id-type: auto\n      logic-delete-field: delFlag\n      # 逻辑删除值（不存在）\n      logic-delete-value: 2 \n      # 逻辑删除值 存在\n      logic-not-delete-value: 0\n  configuration:\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    enabled: true\n  info:\n    title: \'系统模块接口文档\'\n    description: \'系统模块接口描述\'\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip', 'e53b5a0bd2823aed6a52d408412c0e09', '2026-06-30 20:17:58', '2026-06-30 12:17:59', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"c_desc\":\"系统模块\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (20, 59, 'ruoyi-asset-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  application:\n    name: ruoyi-asset\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n        master:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true\n          username: root\n          password: 123456\n  cloud:\n    sentinel:\n      transport:\n        dashboard: localhost:8090\n    http-method-specify: true\n  feign:\n    sentinel:\n      enabled: true\n\nserver:\n  tomcat:\n    threads:\n      max: 50\n    accept-count: 50\n    max-connections: 100\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径\n  typeAliasesPackage: com.ruoyi.asset.domain\n  # mapper xml文件路径\n  mapperLocations: classpath:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      # 主键类型 AUTO自增 ID_WORKER雪花算法\n      id-type: auto\n      # 逻辑删除配置\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n      # 表名前缀（如需全局统一前缀可配置）\n      # table-prefix: t_\n    # 刷新mapper\n    refresh-mapper: true\n  # 配置控制台打印SQL\n  configuration:\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    map-underscore-to-camel-case: true\n    cache-enabled: false\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    enabled: true\n  info:\n    title: \'资产管理模块接口文档\'\n    description: \'资产管理模块接口描述\'\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n\n# 日志配置\nlogging:\n  level:\n    com.ruoyi.asset: debug\n    # MyBatis-Plus SQL日志\n    com.baomidou.mybatisplus: debug', 'af76d5267a4aea59aa7f3ea1f6484818', '2026-07-09 11:27:03', '2026-07-09 03:27:03', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"src_user\":\"nacos\"}');
INSERT INTO `his_config_info` VALUES (9, 60, 'sentinel-ruoyi-gateway', 'DEFAULT_GROUP', '', '[\n    {\n        \"resource\": \"ruoyi-auth\",\n        \"count\": 500,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"ruoyi-system\",\n        \"count\": 1000,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"ruoyi-gen\",\n        \"count\": 200,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"ruoyi-job\",\n        \"count\": 300,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    }\n]', '411936d945573749e5956f2df0b04989', '2026-07-09 11:27:17', '2026-07-09 03:27:17', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"src_user\":\"nacos\",\"c_desc\":\"限流策略\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (7, 61, 'ruoyi-job-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径（多个包用逗号分隔）\n  type-aliases-package: com.ruoyi.job.domain,com.ruoyi.system.api.domain\n  # mapper xml 文件路径\n  mapper-locations: classpath*:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      id-type: auto\n      logic-delete-field: delFlag\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n  configuration:\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    # 是否开启接口文档\n    enabled: true\n  info:\n    # 标题\n    title: \'定时任务接口文档\'\n    # 描述\n    description: \'定时任务接口描述\'\n    # 作者信息\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n', 'bb4840e4418923438da2e74e39d170b6', '2026-07-09 11:28:11', '2026-07-09 03:28:12', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"src_user\":\"nacos\",\"c_desc\":\"定时任务\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (20, 62, 'ruoyi-asset-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  application:\n    name: ruoyi-asset\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n        master:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true\n          username: root\n          password: 123456\n  cloud:\n    sentinel:\n      transport:\n        dashboard: localhost:8090\n    http-method-specify: true\n  feign:\n    sentinel:\n      enabled: true\n\nserver:\n  tomcat:\n    threads:\n      max: 50\n    accept-count: 50\n    max-connections: 100\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径\n  typeAliasesPackage: com.ruoyi.asset.domain\n  # mapper xml文件路径\n  mapperLocations: classpath:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      # 主键类型 AUTO自增 ID_WORKER雪花算法\n      id-type: auto\n      # 逻辑删除配置\n      # 逻辑删除值\n      logic-delete-value: 2\n      # 逻辑未删除值\n      logic-not-delete-value: 0\n      # 表名前缀（如需全局统一前缀可配置）\n      # table-prefix: t_\n    # 刷新mapper\n    refresh-mapper: true\n  # 配置控制台打印SQL\n  configuration:\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    map-underscore-to-camel-case: true\n    cache-enabled: false\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    enabled: true\n  info:\n    title: \'资产管理模块接口文档\'\n    description: \'资产管理模块接口描述\'\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n\n# 日志配置\nlogging:\n  level:\n    com.ruoyi.asset: debug\n    # MyBatis-Plus SQL日志\n    com.baomidou.mybatisplus: debug', '143dbd6b711450f22d9ee620da5ceb9f', '2026-07-09 11:28:52', '2026-07-09 03:28:53', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\"}');
INSERT INTO `his_config_info` VALUES (2, 63, 'ruoyi-gateway-dev.yml', 'DEFAULT_GROUP', '', 'spring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: ruoyi-auth\n          uri: lb://ruoyi-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestBody\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: ruoyi-gen\n          uri: lb://ruoyi-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 定时任务\n        - id: ruoyi-job\n          uri: lb://ruoyi-job\n          predicates:\n            - Path=/schedule/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: ruoyi-system\n          uri: lb://ruoyi-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: ruoyi-file\n          uri: lb://ruoyi-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n        # 资产管理\n        - id: ruoyi-asset\n          uri: lb://ruoyi-asset\n          predicates:\n            - Path=/asset/**\n          filters:\n            - StripPrefix=1\n        # 资产管理\n        - id: ruoyi-workflow\n          uri: lb://ruoyi-workflow\n          predicates:\n            - Path=/workflow/**\n          filters:\n            - StripPrefix=0\n        \n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /*/v3/api-docs\n      - /csrf\n\n# springdoc配置\nspringdoc:\n  webjars:\n    # 访问前缀\n    prefix:\n', '13d9c62eb12436315b2b4ec2f826f32b', '2026-07-09 11:30:30', '2026-07-09 03:30:31', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"src_user\":\"nacos\",\"c_desc\":\"网关模块\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (5, 64, 'ruoyi-system-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径（多个包用逗号分隔）\n  type-aliases-package: com.ruoyi.system.domain,com.ruoyi.system.api.domain\n  # mapper xml 文件路径\n  mapper-locations: classpath*:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      id-type: auto\n      logic-delete-field: delFlag\n      # 逻辑删除值（不存在）\n      logic-delete-value: 2 \n      # 逻辑删除值 存在\n      logic-not-delete-value: 0\n       # 插入时，字段为 null 则不生成该列\n      insert-strategy: NOT_NULL\n      # 更新时，字段为 null 则不生成该列\n      update-strategy: NOT_NULL\n  configuration:\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    enabled: true\n  info:\n    title: \'系统模块接口文档\'\n    description: \'系统模块接口描述\'\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip', '29d5d8c710a315de20205407a01f84ad', '2026-07-09 11:31:29', '2026-07-09 03:31:30', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"c_desc\":\"系统模块\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (20, 65, 'ruoyi-asset-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  application:\n    name: ruoyi-asset\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n        master:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true\n          username: root\n          password: 123456\n  cloud:\n    sentinel:\n      transport:\n        dashboard: localhost:8090\n    http-method-specify: true\n  feign:\n    sentinel:\n      enabled: true\n\nserver:\n  tomcat:\n    threads:\n      max: 50\n    accept-count: 50\n    max-connections: 100\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径\n  typeAliasesPackage: com.ruoyi.asset.domain\n  # mapper xml文件路径\n  mapperLocations: classpath:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      # 主键类型 AUTO自增 ID_WORKER雪花算法\n      id-type: auto\n      # 逻辑删除配置\n      logic-delete-field: delFlag\n      # 逻辑删除值\n      logic-delete-value: 2\n      # 逻辑未删除值\n      logic-not-delete-value: 0\n      # 表名前缀（如需全局统一前缀可配置）\n      # table-prefix: t_\n    # 刷新mapper\n    refresh-mapper: true\n  # 配置控制台打印SQL\n  configuration:\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    map-underscore-to-camel-case: true\n    cache-enabled: false\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    enabled: true\n  info:\n    title: \'资产管理模块接口文档\'\n    description: \'资产管理模块接口描述\'\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n\n# 日志配置\nlogging:\n  level:\n    com.ruoyi.asset: debug\n    # MyBatis-Plus SQL日志\n    com.baomidou.mybatisplus: debug', '51925a93810964667d321762668e35e5', '2026-07-09 12:34:06', '2026-07-09 04:34:07', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\"}');
INSERT INTO `his_config_info` VALUES (1, 66, 'application-dev.yml', 'DEFAULT_GROUP', '', 'spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n  compression:\n    request:\n      enabled: true\n      min-request-size: 8192\n    response:\n      enabled: true\n\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\n', '9928f41dfb10386ad38b3254af5692e0', '2026-07-17 13:47:48', '2026-07-17 05:47:48', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"src_user\":\"nacos\",\"c_desc\":\"通用配置\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (2, 67, 'ruoyi-gateway-dev.yml', 'DEFAULT_GROUP', '', 'spring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: ruoyi-auth\n          uri: lb://ruoyi-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestBody\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: ruoyi-gen\n          uri: lb://ruoyi-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 定时任务\n        - id: ruoyi-job\n          uri: lb://ruoyi-job\n          predicates:\n            - Path=/schedule/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: ruoyi-system\n          uri: lb://ruoyi-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: ruoyi-file\n          uri: lb://ruoyi-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n        # 资产管理\n        - id: ruoyi-asset\n          uri: lb://ruoyi-asset\n          predicates:\n            - Path=/asset/**\n          filters:\n            - StripPrefix=1\n        # 资产工作流\n        - id: ruoyi-workflow\n          uri: lb://ruoyi-workflow\n          predicates:\n            - Path=/workflow/**\n          filters:\n            - StripPrefix=0\n        \n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /*/v3/api-docs\n      - /csrf\n\n# springdoc配置\nspringdoc:\n  webjars:\n    # 访问前缀\n    prefix:\n', '5a50257d9c8cc17e49cd43de278af1e9', '2026-07-17 13:48:30', '2026-07-17 05:48:30', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"c_desc\":\"网关模块\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (3, 68, 'ruoyi-auth-dev.yml', 'DEFAULT_GROUP', '', 'spring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n', '5d5c86d795ea9e70219b518206b4d083', '2026-07-17 13:48:38', '2026-07-17 05:48:39', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"src_user\":\"nacos\",\"c_desc\":\"认证中心\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (4, 69, 'ruoyi-monitor-dev.yml', 'DEFAULT_GROUP', '', '# spring\nspring:\n  security:\n    user:\n      name: ruoyi\n      password: 123456\n  boot:\n    admin:\n      ui:\n        title: 若依服务状态监控\n', '6f122fd2bfb8d45f858e7d6529a9cd44', '2026-07-17 13:48:48', '2026-07-17 05:48:48', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"src_user\":\"nacos\",\"c_desc\":\"监控中心\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (5, 70, 'ruoyi-system-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径（多个包用逗号分隔）\n  type-aliases-package: com.ruoyi.system.domain,com.ruoyi.system.api.domain\n  # mapper xml 文件路径\n  mapper-locations: classpath*:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      id-type: auto\n      logic-delete-field: delFlag\n      # 逻辑删除值（不存在）\n      logic-delete-value: 2 \n      # 逻辑删除值 存在\n      logic-not-delete-value: 0\n       # 插入时，字段为 null 则不生成该列\n      insert-strategy: NOT_NULL\n      # 更新时，字段为 null 则不生成该列\n      update-strategy: NOT_NULL\n  configuration:\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    enabled: true\n  info:\n    title: \'系统模块接口文档\'\n    description: \'系统模块接口描述\'\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip', '29d5d8c710a315de20205407a01f84ad', '2026-07-17 13:59:49', '2026-07-17 05:59:50', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"c_desc\":\"系统模块\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (6, 71, 'ruoyi-gen-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# ruoyi-gen-dev.yml\nmybatis-plus:\n  type-aliases-package: com.ruoyi.gen.domain\n  mapper-locations: classpath*:mapper/**/*.xml\n  global-config:\n    db-config:\n      id-type: auto\n  configuration:\n    map-underscore-to-camel-case: true\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    # 是否开启接口文档\n    enabled: true\n  info:\n    # 标题\n    title: \'代码生成接口文档\'\n    # 描述\n    description: \'代码生成接口描述\'\n    # 作者信息\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n\n# 代码生成\ngen:\n  # 作者\n  author: xiaowang\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.ruoyi.asset\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_,tb_,asset_\n  # 是否允许生成文件覆盖到本地（自定义路径），默认不允许\n  allowOverwrite: false', '99a46159d1b2bd98f08c7ddaa10a283d', '2026-07-17 14:00:04', '2026-07-17 06:00:04', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\",\"effect\":\"null\",\"src_user\":\"nacos\",\"c_desc\":\"代码生成\",\"c_use\":\"null\"}');
INSERT INTO `his_config_info` VALUES (20, 72, 'ruoyi-asset-dev.yml', 'DEFAULT_GROUP', '', '# spring配置\nspring:\n  application:\n    name: ruoyi-asset\n  redis:\n    host: 192.168.186.128\n    port: 6379\n    password: 123456\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n        master:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          url: jdbc:mysql://192.168.186.128:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true\n          username: root\n          password: 123456\n  cloud:\n    sentinel:\n      transport:\n        dashboard: localhost:8090\n    http-method-specify: true\n  feign:\n    sentinel:\n      enabled: true\n  rabbitmq:\n    host: 192.168.186.128\n    port: 5672\n    virtual-host: /\n    username: admin\n    password: 123456\n    connection-timeout: 30000\n    publisher-confirm-type: simple\n    publisher-returns: true\n    template:\n      mandatory: true\n    listener:\n      simple:\n        acknowledge-mode: auto\n        prefetch: 1\n\nserver:\n  tomcat:\n    threads:\n      max: 50\n    accept-count: 50\n    max-connections: 100\n\n# MyBatis-Plus配置\nmybatis-plus:\n  # 实体类包路径\n  typeAliasesPackage: com.ruoyi.asset.domain\n  # mapper xml文件路径\n  mapperLocations: classpath:mapper/**/*.xml\n  # 全局配置\n  global-config:\n    db-config:\n      # 主键类型 AUTO自增 ID_WORKER雪花算法\n      id-type: auto\n      # 逻辑删除配置\n      logic-delete-field: delFlag\n      # 逻辑删除值\n      logic-delete-value: 2\n      # 逻辑未删除值\n      logic-not-delete-value: 0\n      # 表名前缀（如需全局统一前缀可配置）\n      # table-prefix: t_\n    # 刷新mapper\n    refresh-mapper: true\n  # 配置控制台打印SQL\n  configuration:\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    map-underscore-to-camel-case: true\n    cache-enabled: false\n\n# springdoc配置\nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    enabled: true\n  info:\n    title: \'资产管理模块接口文档\'\n    description: \'资产管理模块接口描述\'\n    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n\n# 日志配置\nlogging:\n  level:\n    com.ruoyi.asset: debug\n    # MyBatis-Plus SQL日志\n    com.baomidou.mybatisplus: debug', 'ec2ceabfc7055dd6d1c14b53c08c3f4e', '2026-07-17 14:18:07', '2026-07-17 06:18:07', NULL, '192.168.186.1', 'U', '', '', 'formal', '', '{\"type\":\"yaml\"}');

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'role',
  `resource` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'resource',
  `action` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'action',
  UNIQUE INDEX `uk_role_permission`(`role` ASC, `resource` ASC, `action` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'username',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'role',
  UNIQUE INDEX `idx_user_role`(`username` ASC, `role` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES ('nacos', 'ROLE_ADMIN');

-- ----------------------------
-- Table structure for tenant_capacity
-- ----------------------------
DROP TABLE IF EXISTS `tenant_capacity`;
CREATE TABLE `tenant_capacity`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数',
  `max_aggr_size` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '租户容量信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for tenant_info
-- ----------------------------
DROP TABLE IF EXISTS `tenant_info`;
CREATE TABLE `tenant_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_info_kptenantid`(`kp` ASC, `tenant_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = 'tenant_info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant_info
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'username',
  `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'password',
  `enabled` tinyint(1) NOT NULL COMMENT 'enabled',
  PRIMARY KEY (`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', 1);

SET FOREIGN_KEY_CHECKS = 1;
