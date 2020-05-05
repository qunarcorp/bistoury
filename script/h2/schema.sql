DROP TABLE IF EXISTS bistoury_app;
CREATE TABLE bistoury_app
(
    id          INT UNSIGNED auto_increment PRIMARY KEY,
    code        VARCHAR(50) DEFAULT ''                NOT NULL COMMENT '应用代号',
    name        VARCHAR(50) DEFAULT ''                NOT NULL COMMENT '应用名称',
    group_code  VARCHAR(50) DEFAULT ''                NOT NULL COMMENT '所属组编码',
    status      TINYINT     DEFAULT 0                 NOT NULL COMMENT '应用状态, 0=未审核，1=审核通过, 2=审核被拒绝, 3=已废弃',
    creator     VARCHAR(50) DEFAULT ''                NOT NULL COMMENT '创建者',
    create_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    CONSTRAINT uniq_code UNIQUE (CODE)
) charset = utf8mb4;

DROP TABLE IF EXISTS bistoury_user_app;
CREATE TABLE bistoury_user_app
(
    id          INT UNSIGNED auto_increment PRIMARY KEY,
    app_code    VARCHAR(50)                         NOT NULL COMMENT '应用代号',
    user_code   VARCHAR(50)                         NOT NULL COMMENT '用户标识',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    CONSTRAINT uniq_app_user UNIQUE (app_code, user_code)
) charset = utf8mb4;

CREATE INDEX idx_app_code ON bistoury_user_app (app_code);
CREATE INDEX idx_user_code ON bistoury_user_app (user_code);

DROP TABLE IF EXISTS bistoury_server;
CREATE TABLE bistoury_server
(
    id                     BIGINT(11) UNSIGNED auto_increment COMMENT '主键' PRIMARY KEY,
    server_id              varchar(32)  default '' not null comment 'server id',
    ip                     VARCHAR(15)  DEFAULT '' NOT NULL COMMENT 'server ip',
    port                   INT UNSIGNED DEFAULT 0  NOT NULL COMMENT 'server port',
    host                   VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'server host',
    log_dir                VARCHAR(255) DEFAULT '' NOT NULL COMMENT 'server 日志目录',
    room                   VARCHAR(20)  DEFAULT '' NOT NULL COMMENT 'server机房',
    app_code               varchar(50)  default '' not null comment '对应的appcode',
    auto_jstack_enable     tinyint      default 0  not null comment '自动jstack打开状态：0为关闭，1为开启',
    auto_jmap_histo_enable tinyint      default 0  not null comment '打开自动jmap histo状态：0为关闭，1为开启',
    index idx_server_app_code (app_code),
    constraint uniq_server_id unique (server_id),
    CONSTRAINT uniq_ip UNIQUE (ip)
) charset = utf8mb4;

DROP TABLE IF EXISTS `bistoury_gitlab_token`;
CREATE TABLE `bistoury_gitlab_token`
(
    `id`            int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `user_code`     varchar(50)      NOT NULL DEFAULT '' COMMENT '用户code',
    `private_token` varchar(100)     NOT NULL DEFAULT '' COMMENT 'gitlab private token',
    `create_time`   timestamp        NOT NULL DEFAULT '1970-01-01 08:00:01' COMMENT '创建时间',
    `update_time`   timestamp        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_git_user_code` (`user_code`)
) CHARSET = utf8mb4;

DROP TABLE if exists `bistoury_user`;
create table `bistoury_user`
(
    id        int unsigned auto_increment comment '主键' primary key,
    user_code varchar(50)  not null default '' comment '用户code',
    password  varchar(100) not null default '' comment '用户密码',
    constraint uniq_user_code unique (user_code)
) CHARSET = utf8mb4;

DROP TABLE IF EXISTS `bistoury_profiler_lock`;
CREATE TABLE `bistoury_profiler_lock`
(
    `id`       INT(10) UNSIGNED       NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `app_code` VARCHAR(50) DEFAULT '' NOT NULL COMMENT '对应的appCode',
    `agent_id` VARCHAR(32) DEFAULT '' NOT NULL COMMENT 'agent机器对应的id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_app_code_agent_id` (`app_code`, `agent_id`)
) CHARSET = utf8mb4;

DROP TABLE IF EXISTS `bistoury_profiler`;
CREATE TABLE `bistoury_profiler`
(
    `id`          INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `profiler_id` VARCHAR(32)      NOT NULL COMMENT '性能分析对应的id',
    `operator`    VARCHAR(50)      NOT NULL DEFAULT '' COMMENT '操作用户code',
    `app_code`    VARCHAR(50)      NOT NULL DEFAULT '' COMMENT '对应的appCode',
    `agent_id`    VARCHAR(32)      NOT NULL DEFAULT '' COMMENT 'agent机器对应的id',
    `duration`    INT(10)          NOT NULL COMMENT '性能分析时长,单位s',
    `interval_ms`   INT(10)          NOT NULL COMMENT '抽样间隔时长,单位ms',
    `mode`        INT(3) UNSIGNED  NOT NULL COMMENT '分析模式,异步抽样-0,同步抽样-1',
    `pid`         INT(10) UNSIGNED NOT NULL COMMENT '目标vm对应的pid',
    `start_time`  TIMESTAMP        NOT NULL DEFAULT '1970-01-01 08:00:01' COMMENT '性能分析开始时间',
    `update_time` TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `state`       INT(3) UNSIGNED  NOT NULL COMMENT '状态, 0: 开始 1: 已结束 2: 准备 4: 错误',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_profiler_id` (`profiler_id`),
    INDEX idx_start_time (start_time),
    INDEX idx_app_code_agent_id (`app_code`, `agent_id`)
) CHARSET = utf8mb4;
