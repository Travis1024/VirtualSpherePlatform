create table VSP.VSP_AUTH_GROUP
(
    ID          BIGINT(19) not null,
    NAME        VARCHAR(64) not null,
    DESCRIPTION VARCHAR(256),
    IS_DELETED  INT(10) default 0 not null,
    UPDATER     BIGINT(19),
    CREATOR     BIGINT(19),
    UPDATE_TIME TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6)
);

comment
on table VSP.VSP_AUTH_GROUP is '权限组信息表';

comment
on column VSP.VSP_AUTH_GROUP.ID is 'ID';

comment
on column VSP.VSP_AUTH_GROUP.NAME is '权限组名称';

comment
on column VSP.VSP_AUTH_GROUP.DESCRIPTION is '权限组描述信息';

comment
on column VSP.VSP_AUTH_GROUP.IS_DELETED is '逻辑信息';

comment
on column VSP.VSP_AUTH_GROUP.UPDATER is '更新者';

comment
on column VSP.VSP_AUTH_GROUP.CREATOR is '创建者';

comment
on column VSP.VSP_AUTH_GROUP.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_AUTH_GROUP.CREATE_TIME is '创建时间';

create unique index VSP.INDEX33555467
    on VSP.VSP_AUTH_GROUP (ID);

alter table VSP.VSP_AUTH_GROUP
    add constraint VSP_AUTH_GROUP_PK
        primary key (ID);

create table VSP.VSP_AUTH_USER_RELATION
(
    ID            BIGINT(19) not null,
    USER_ID       BIGINT(19) not null,
    AUTH_GROUP_ID BIGINT(19) not null,
    IS_DELETED    INT(10) default 0 not null,
    UPDATER       BIGINT(19),
    CREATOR       BIGINT(19),
    UPDATE_TIME   TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6)
);

comment
on table VSP.VSP_AUTH_USER_RELATION is '权限组-用户关联关系表';

comment
on column VSP.VSP_AUTH_USER_RELATION.ID is 'ID';

comment
on column VSP.VSP_AUTH_USER_RELATION.USER_ID is '用户ID';

comment
on column VSP.VSP_AUTH_USER_RELATION.AUTH_GROUP_ID is '权限组ID';

comment
on column VSP.VSP_AUTH_USER_RELATION.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_AUTH_USER_RELATION.UPDATER is '更新者';

comment
on column VSP.VSP_AUTH_USER_RELATION.CREATOR is '创建者';

comment
on column VSP.VSP_AUTH_USER_RELATION.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_AUTH_USER_RELATION.CREATE_TIME is '创建时间';

create unique index VSP.INDEX33555469
    on VSP.VSP_AUTH_USER_RELATION (ID);

alter table VSP.VSP_AUTH_USER_RELATION
    add constraint VSP_AUTH_RELATION_PK
        primary key (ID);

create table VSP.VSP_AUTH_VMWARE_RELATION
(
    ID            BIGINT(19) not null,
    VMWARE_ID     BIGINT(19) not null,
    AUTH_GROUP_ID BIGINT(19) not null,
    IS_DELETED    INT(10) default 0 not null,
    UPDATER       BIGINT(19),
    CREATOR       BIGINT(19),
    UPDATE_TIME   TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6)
);

comment
on table VSP.VSP_AUTH_VMWARE_RELATION is '权限组-虚拟机关联关系表';

comment
on column VSP.VSP_AUTH_VMWARE_RELATION.ID is 'ID';

comment
on column VSP.VSP_AUTH_VMWARE_RELATION.VMWARE_ID is '虚拟机ID';

comment
on column VSP.VSP_AUTH_VMWARE_RELATION.AUTH_GROUP_ID is '权限组ID';

comment
on column VSP.VSP_AUTH_VMWARE_RELATION.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_AUTH_VMWARE_RELATION.UPDATER is '更新者';

comment
on column VSP.VSP_AUTH_VMWARE_RELATION.CREATOR is '创建者';

comment
on column VSP.VSP_AUTH_VMWARE_RELATION.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_AUTH_VMWARE_RELATION.CREATE_TIME is '创建时间';

create unique index VSP.INDEX33555471
    on VSP.VSP_AUTH_VMWARE_RELATION (ID);

alter table VSP.VSP_AUTH_VMWARE_RELATION
    add constraint VSP_AUTH_VMWARE_RELATION_PK
        primary key (ID);

create table VSP.VSP_DISK_INFO
(
    ID          BIGINT(19) not null,
    NAME        VARCHAR(128) not null,
    DESCRIPTION VARCHAR(256),
    SPACE_SIZE  BIGINT(19) not null,
    SUB_PATH    VARCHAR(512),
    VMWARE_ID   BIGINT(19) not null,
    DISK_TYPE   INT(10) not null,
    IS_DELETED  INT(10) default 0 not null,
    UPDATER     BIGINT(19),
    CREATOR     BIGINT(19),
    UPDATE_TIME TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6),
	IS_MOUNT INT(10) default 0 not null,
	TARGET_DEV VARCHAR(8)
);

comment
on table VSP.VSP_DISK_INFO is '虚拟机磁盘信息表';

comment
on column VSP.VSP_DISK_INFO.ID is 'ID';

comment
on column VSP.VSP_DISK_INFO.NAME is '磁盘名称';

comment
on column VSP.VSP_DISK_INFO.DESCRIPTION is '磁盘描述信息';

comment
on column VSP.VSP_DISK_INFO.SPACE_SIZE is '磁盘大小（字节）';

comment
on column VSP.VSP_DISK_INFO.SUB_PATH is '磁盘存放路径（共享存储子路径）';

comment
on column VSP.VSP_DISK_INFO.VMWARE_ID is '磁盘所属虚拟机ID';

comment
on column VSP.VSP_DISK_INFO.DISK_TYPE is '磁盘类型（1-Data、2-Root）';

comment
on column VSP.VSP_DISK_INFO.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_DISK_INFO.UPDATER is '更新者';

comment
on column VSP.VSP_DISK_INFO.CREATOR is '创建者';

comment
on column VSP.VSP_DISK_INFO.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_DISK_INFO.CREATE_TIME is '创建时间';

comment
on column VSP.VSP_DISK_INFO.IS_MOUNT is '磁盘是否挂载（0-未挂载、1-已挂载）';

comment
on column VSP.VSP_DISK_INFO.TARGET_DEV is '磁盘符-vda';

create unique index VSP.INDEX33555475
    on VSP.VSP_DISK_INFO (ID);

alter table VSP.VSP_DISK_INFO
    add constraint VSP_DISK_INFO_PK
        primary key (ID);

create table VSP.VSP_DYNAMIC_CONFIG_INFO
(
    ID                 BIGINT(19) not null,
    CONFIG_NAME        VARCHAR(64)  not null,
    CONFIG_VALUE       VARCHAR(256) not null,
    CONFIG_DESCRIPTION VARCHAR(512),
    CONFIG_TYPE        INT(10) not null,
    IS_DELETED         INT(10) default 0 not null,
    UPDATER            BIGINT(19),
    CREATOR            BIGINT(19),
    UPDATE_TIME        TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6),
	CONFIG_EXAMPLE VARCHAR(256) not null,
	IS_FIXED INT(10) default 0 not null,
	AFFILIATION_MACHINE_ID BIGINT(19) default 0 not null,
	AFFILIATION_TYPE INT(10) default 0 not null,
	AFFILIATION_MACHINE_UUID VARCHAR(64)
);

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.ID is 'ID';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.CONFIG_NAME is '动态配置名称';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.CONFIG_VALUE is '动态配置 VALUE';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.CONFIG_DESCRIPTION is '动态配置描述信息';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.CONFIG_TYPE is '配置类型';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.UPDATER is '更新者';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.CREATOR is '创建者';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.CREATE_TIME is '创建时间';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.CONFIG_EXAMPLE is '动态配置示例值';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.IS_FIXED is '是否不可修改（0-可修改、1-禁止修改）';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.AFFILIATION_MACHINE_ID is '配置归属主机 ID（系统默认为 0）';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.AFFILIATION_TYPE is '配置归属主机类型（0-系统、1-宿主机、2-虚拟机）';

comment
on column VSP.VSP_DYNAMIC_CONFIG_INFO.AFFILIATION_MACHINE_UUID is '配置归属主机 UUID';

create unique index VSP.INDEX33555477
    on VSP.VSP_DYNAMIC_CONFIG_INFO (ID);

alter table VSP.VSP_DYNAMIC_CONFIG_INFO
    add constraint VSP_DYNAMIC_CONFIG_INFO_PK
        primary key (ID);

create table VSP.VSP_GLOBAL_MESSAGE
(
    ID              BIGINT(19) not null,
    MESSAGE_STATE   INT(10),
    MESSAGE_MODULE  INT(10),
    MESSAGE_CONTENT LONGVARCHAR( max),
    IS_CONFIRM      INT(10) default 0 not null,
    IS_DELETED      INT(10) default 0 not null,
    UPDATER         BIGINT(19),
    CREATOR         BIGINT(19),
    UPDATE_TIME     TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6),
	CONFIRM_USER_ID BIGINT(19),
	CONFIRM_TIME TIMESTAMP(39,6),
	MESSAGE_TITLE VARCHAR(64) not null
);

comment
on column VSP.VSP_GLOBAL_MESSAGE.ID is 'ID';

comment
on column VSP.VSP_GLOBAL_MESSAGE.MESSAGE_STATE is '消息状态';

comment
on column VSP.VSP_GLOBAL_MESSAGE.MESSAGE_MODULE is '消息所属模块';

comment
on column VSP.VSP_GLOBAL_MESSAGE.MESSAGE_CONTENT is '消息内容';

comment
on column VSP.VSP_GLOBAL_MESSAGE.IS_CONFIRM is '消息是否确认';

comment
on column VSP.VSP_GLOBAL_MESSAGE.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_GLOBAL_MESSAGE.UPDATER is '更新者';

comment
on column VSP.VSP_GLOBAL_MESSAGE.CREATOR is '创建者';

comment
on column VSP.VSP_GLOBAL_MESSAGE.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_GLOBAL_MESSAGE.CREATE_TIME is '创建时间';

comment
on column VSP.VSP_GLOBAL_MESSAGE.CONFIRM_USER_ID is '消息确认用户';

comment
on column VSP.VSP_GLOBAL_MESSAGE.CONFIRM_TIME is '消息确认时间';

comment
on column VSP.VSP_GLOBAL_MESSAGE.MESSAGE_TITLE is '消息标题';

create unique index VSP.INDEX33555495
    on VSP.VSP_GLOBAL_MESSAGE (ID);

alter table VSP.VSP_GLOBAL_MESSAGE
    add constraint VSP_GLOBAL_MESSAGE_PK
        primary key (ID);

create table VSP.VSP_HOST_INFO
(
    ID             BIGINT(19) not null,
    NAME           VARCHAR(64)  not null,
    DESCRIPTION    VARCHAR(256),
    IP             VARCHAR(16)  not null,
    MEMORY_SIZE    BIGINT(19),
    CPU_NUMBER     INT(10),
    ARCHITECTURE   VARCHAR(64),
    LOGIN_USER     VARCHAR(32)  not null,
    LOGIN_PASSWORD VARCHAR(128) not null,
    SSH_PORT       INT(10) not null,
    IS_DELETED     INT(10) default 0 not null,
    UPDATER        BIGINT(19),
    CREATOR        BIGINT(19),
    UPDATE_TIME    TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6),
	NETWORK_LAYER_ID BIGINT(19) not null,
	SHARED_STORAGE_PATH VARCHAR(256) not null,
	VIRTUAL_CPU_NUMBER INT(10),
	STATE INT(10) default 0,
	STATE_MESSAGE VARCHAR(256),
	UUID VARCHAR(64) not null,
	constraint INDEX33555480
		unique (ID, IP)
);

comment
on table VSP.VSP_HOST_INFO is '宿主机信息表';

comment
on column VSP.VSP_HOST_INFO.ID is 'ID';

comment
on column VSP.VSP_HOST_INFO.NAME is '宿主机名称';

comment
on column VSP.VSP_HOST_INFO.DESCRIPTION is '宿主机描述信息';

comment
on column VSP.VSP_HOST_INFO.IP is '宿主机 IP 地址';

comment
on column VSP.VSP_HOST_INFO.MEMORY_SIZE is '宿主机内存大小（字节）';

comment
on column VSP.VSP_HOST_INFO.CPU_NUMBER is '宿主机 CPU 核数';

comment
on column VSP.VSP_HOST_INFO.ARCHITECTURE is '宿主机架构信息';

comment
on column VSP.VSP_HOST_INFO.LOGIN_USER is '宿主机管理员登录用户';

comment
on column VSP.VSP_HOST_INFO.LOGIN_PASSWORD is '宿主机管理员登录密码';

comment
on column VSP.VSP_HOST_INFO.SSH_PORT is '宿主机 SSH 连接端口号';

comment
on column VSP.VSP_HOST_INFO.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_HOST_INFO.UPDATER is '更新者';

comment
on column VSP.VSP_HOST_INFO.CREATOR is '创建者';

comment
on column VSP.VSP_HOST_INFO.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_HOST_INFO.CREATE_TIME is '创建时间';

comment
on column VSP.VSP_HOST_INFO.NETWORK_LAYER_ID is '宿主机所属二层网络 ID';

comment
on column VSP.VSP_HOST_INFO.SHARED_STORAGE_PATH is '宿主机共享存储路径';

comment
on column VSP.VSP_HOST_INFO.VIRTUAL_CPU_NUMBER is '宿主机虚拟 CPU 总数量';

comment
on column VSP.VSP_HOST_INFO.STATE is '宿主机状态 (0-准备中、1-就绪、2-异常、3-停用)';

comment
on column VSP.VSP_HOST_INFO.STATE_MESSAGE is '宿主机状态消息';

comment
on column VSP.VSP_HOST_INFO.UUID is '宿主机 UUID';

create unique index VSP.INDEX33555481
    on VSP.VSP_HOST_INFO (ID);

alter table VSP.VSP_HOST_INFO
    add constraint VSP_HOST_INFO_PK
        primary key (ID);

create table VSP.VSP_IMAGE_INFO
(
    ID             BIGINT(19) not null,
    NAME           VARCHAR(128) not null,
    DESCRIPTION    VARCHAR(256),
    ARCHITECTURE   INT(10) not null,
    SUB_PATH       VARCHAR(256) not null,
    IMAGE_PLATFORM INT(10) not null,
    IS_DELETED     INT(10) default 0 not null,
    UPDATER        BIGINT(19),
    CREATOR        BIGINT(19),
    UPDATE_TIME    TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6),
	STATE INT(10),
	STATE_MESSAGE VARCHAR(512),
	IMAGE_TYPE INT(10) not null
);

comment
on table VSP.VSP_IMAGE_INFO is '镜像信息表';

comment
on column VSP.VSP_IMAGE_INFO.ID is 'ID';

comment
on column VSP.VSP_IMAGE_INFO.NAME is '镜像名字';

comment
on column VSP.VSP_IMAGE_INFO.DESCRIPTION is '镜像描述信息';

comment
on column VSP.VSP_IMAGE_INFO.ARCHITECTURE is '镜像 CPU 架构（1-x86_64、2-aarch64）';

comment
on column VSP.VSP_IMAGE_INFO.SUB_PATH is '镜像存储路径（共享存储子路径）';

comment
on column VSP.VSP_IMAGE_INFO.IMAGE_PLATFORM is '镜像平台（0-Other、1-Linux、2-Windows）';

comment
on column VSP.VSP_IMAGE_INFO.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_IMAGE_INFO.UPDATER is '更新者';

comment
on column VSP.VSP_IMAGE_INFO.CREATOR is '创建者';

comment
on column VSP.VSP_IMAGE_INFO.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_IMAGE_INFO.CREATE_TIME is '创建时间';

comment
on column VSP.VSP_IMAGE_INFO.STATE is '镜像状态（0-上传中、1-异常、2-就绪）';

comment
on column VSP.VSP_IMAGE_INFO.STATE_MESSAGE is '镜像状态消息';

comment
on column VSP.VSP_IMAGE_INFO.IMAGE_TYPE is '镜像类型（1-ISO镜像、2-系统镜像）';

create unique index VSP.INDEX33555483
    on VSP.VSP_IMAGE_INFO (ID);

alter table VSP.VSP_IMAGE_INFO
    add constraint VSP_IMAGE_INFO_PK
        primary key (ID);

create table VSP.VSP_NETWORK_LAYER_INFO
(
    ID                BIGINT(19) not null,
    NIC_NAME          VARCHAR(32) not null,
    NIC_START_ADDRESS VARCHAR(16) not null,
    NIC_MASK          INT(10) not null,
    IS_DELETED        INT(10) default 0 not null,
    UPDATER           BIGINT(19),
    CREATOR           BIGINT(19),
    UPDATE_TIME       TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6)
);

comment
on table VSP.VSP_NETWORK_LAYER_INFO is '二层网络信息表';

comment
on column VSP.VSP_NETWORK_LAYER_INFO.ID is 'ID';

comment
on column VSP.VSP_NETWORK_LAYER_INFO.NIC_NAME is '网卡名称（eg：p4p1）';

comment
on column VSP.VSP_NETWORK_LAYER_INFO.NIC_START_ADDRESS is '网卡起始 IP 地址（192.168.0.0）';

comment
on column VSP.VSP_NETWORK_LAYER_INFO.NIC_MASK is '网卡掩码（eg：24）';

comment
on column VSP.VSP_NETWORK_LAYER_INFO.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_NETWORK_LAYER_INFO.UPDATER is '更新者';

comment
on column VSP.VSP_NETWORK_LAYER_INFO.CREATOR is '创建者';

comment
on column VSP.VSP_NETWORK_LAYER_INFO.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_NETWORK_LAYER_INFO.CREATE_TIME is '创建时间';

create unique index VSP.INDEX33555485
    on VSP.VSP_NETWORK_LAYER_INFO (ID);

alter table VSP.VSP_NETWORK_LAYER_INFO
    add constraint VSP_NETWORK_LAYER2_INFO_PK
        primary key (ID);

create table VSP.VSP_OPERATION_LOG_202406
(
    ID              BIGINT(19) not null,
    OPERATION_STATE INT(10) not null,
    IP_ADDRESS      VARCHAR(32) not null,
    REQUEST_URL     VARCHAR(256),
    USER_ID         BIGINT(19),
    METHOD          VARCHAR(128),
    REQUEST_METHOD  VARCHAR(128),
    TITLE           VARCHAR(64),
    BUSINESS_TYPE   INT(10),
    REQUEST_PARAMS  LONGVARCHAR( max),
    RESPONSE_INFO   LONGVARCHAR( max),
    IS_DELETED      INT(10) default 0 not null,
    UPDATER         BIGINT(19),
    CREATOR         BIGINT(19),
    UPDATE_TIME     TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6),
	ERROR_MESSAGE LONGVARCHAR(max),
	COST_TIME BIGINT(19)
);

create unique index VSP.INDEX33555487
    on VSP.VSP_OPERATION_LOG_202406 (ID);

alter table VSP.VSP_OPERATION_LOG_202406
    add constraint VSP_OPERATION_LOG_202406_PK
        primary key (ID);

create table VSP.VSP_OPERATION_LOG_202407
(
    ID              BIGINT(19) not null,
    OPERATION_STATE INT(10) not null,
    IP_ADDRESS      VARCHAR(32) not null,
    REQUEST_URL     VARCHAR(256),
    USER_ID         BIGINT(19),
    METHOD          VARCHAR(128),
    REQUEST_METHOD  VARCHAR(128),
    TITLE           VARCHAR(64),
    BUSINESS_TYPE   INT(10),
    REQUEST_PARAMS  LONGVARCHAR( max),
    RESPONSE_INFO   LONGVARCHAR( max),
    IS_DELETED      INT(10) default 0 not null,
    UPDATER         BIGINT(19),
    CREATOR         BIGINT(19),
    UPDATE_TIME     TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6),
	ERROR_MESSAGE LONGVARCHAR(max),
	COST_TIME BIGINT(19)
);

create unique index VSP.INDEX33555550
    on VSP.VSP_OPERATION_LOG_202407 (ID);

alter table VSP.VSP_OPERATION_LOG_202407
    add constraint VSP_OPERATION_LOG_202407_PK
        primary key (ID);

create table VSP.VSP_SCHEDULE_JOB
(
    ID               BIGINT(19) not null,
    SCHEDULE_NAME    VARCHAR(128) not null,
    CRON_EXPRESSION  VARCHAR(32)  not null,
    CRON_DESCRIPTION VARCHAR(256) not null,
    IS_FIXED         INT(10) default 0 not null,
    SCHEDULE_STATUS  INT(10) default 0 not null,
    JOB_CLASS        VARCHAR(512) not null,
    JOB_GROUP        INT(10) default 0 not null,
    IS_DELETED       INT(10) not null,
    UPDATER          BIGINT(19),
    CREATOR          BIGINT(19),
    UPDATE_TIME      TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6)
);

comment
on column VSP.VSP_SCHEDULE_JOB.ID is 'ID';

comment
on column VSP.VSP_SCHEDULE_JOB.SCHEDULE_NAME is '定时任务名称';

comment
on column VSP.VSP_SCHEDULE_JOB.CRON_EXPRESSION is 'Crontab 表达式';

comment
on column VSP.VSP_SCHEDULE_JOB.CRON_DESCRIPTION is 'Crontab 表达式描述信息';

comment
on column VSP.VSP_SCHEDULE_JOB.IS_FIXED is '是否不可修改（0-可修改、1-禁止修改）';

comment
on column VSP.VSP_SCHEDULE_JOB.SCHEDULE_STATUS is '定时任务状态（0-启动、1-停止）';

comment
on column VSP.VSP_SCHEDULE_JOB.JOB_CLASS is '定时任务实现类';

comment
on column VSP.VSP_SCHEDULE_JOB.JOB_GROUP is '定时任务分组';

comment
on column VSP.VSP_SCHEDULE_JOB.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_SCHEDULE_JOB.UPDATER is '更新者';

comment
on column VSP.VSP_SCHEDULE_JOB.CREATOR is '创建者';

comment
on column VSP.VSP_SCHEDULE_JOB.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_SCHEDULE_JOB.CREATE_TIME is '创建时间';

create unique index VSP.INDEX33555548
    on VSP.VSP_SCHEDULE_JOB (ID);

alter table VSP.VSP_SCHEDULE_JOB
    add constraint VSP_SCHEDULE_JOB_PK
        primary key (ID);

create table VSP.VSP_SNAPSHOT_INFO
(
    ID                 BIGINT(19) not null,
    SNAPSHOT_NAME      VARCHAR(256) not null,
    VMWARE_ID          BIGINT(19) not null,
    VMWARE_UUID        VARCHAR(64)  not null,
    DESCRIPTION        VARCHAR(256) not null,
    VERSION_TYPE       INT(10) default 0 not null,
    SUB_PATH           VARCHAR(512) not null,
    AUTO_SNAPSHOT_NAME VARCHAR(64)  not null,
    TARGET_DEV         VARCHAR(16)  not null,
    IS_DELETED         INT(10) default 0 not null,
    UPDATER            BIGINT(19),
    CREATOR            BIGINT(19),
    UPDATE_TIME        TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6)
);

comment
on column VSP.VSP_SNAPSHOT_INFO.ID is 'ID';

comment
on column VSP.VSP_SNAPSHOT_INFO.SNAPSHOT_NAME is '快照名称（用户定义）';

comment
on column VSP.VSP_SNAPSHOT_INFO.VMWARE_ID is '所属虚拟机 ID';

comment
on column VSP.VSP_SNAPSHOT_INFO.VMWARE_UUID is '所属虚拟机 UUID';

comment
on column VSP.VSP_SNAPSHOT_INFO.DESCRIPTION is '快照描述信息';

comment
on column VSP.VSP_SNAPSHOT_INFO.VERSION_TYPE is '快照版本类型（0-原始快照）';

comment
on column VSP.VSP_SNAPSHOT_INFO.SUB_PATH is '快照共享存储子路径';

comment
on column VSP.VSP_SNAPSHOT_INFO.AUTO_SNAPSHOT_NAME is '快照名称（自动定义）';

comment
on column VSP.VSP_SNAPSHOT_INFO.TARGET_DEV is '快照目标设备（eg:vda）';

comment
on column VSP.VSP_SNAPSHOT_INFO.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_SNAPSHOT_INFO.UPDATER is '更新者';

comment
on column VSP.VSP_SNAPSHOT_INFO.CREATOR is '创建者';

comment
on column VSP.VSP_SNAPSHOT_INFO.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_SNAPSHOT_INFO.CREATE_TIME is '创建时间';

create unique index VSP.INDEX33555552
    on VSP.VSP_SNAPSHOT_INFO (ID);

alter table VSP.VSP_SNAPSHOT_INFO
    add constraint VSP_SNAPSHOT_INFO_PK
        primary key (ID);

create table VSP.VSP_USER
(
    ID          BIGINT(19) not null,
    USERNAME    VARCHAR(64)  not null,
    PASSWORD    VARCHAR(256) not null,
    PHONE       VARCHAR(16)  not null,
    EMAIL       VARCHAR(64)  not null,
    REAL_NAME   VARCHAR(16)  not null,
    IS_DELETED  INT(10) default 0 not null,
    UPDATER     BIGINT(19),
    CREATOR     BIGINT(19),
    UPDATE_TIME TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6),
	ROLE_TYPE INT(10) not null,
	DESCRIPTION VARCHAR(256)
);

comment
on table VSP.VSP_USER is '用户信息表';

comment
on column VSP.VSP_USER.ID is 'ID';

comment
on column VSP.VSP_USER.USERNAME is '登录用户名';

comment
on column VSP.VSP_USER.PASSWORD is '登录密码';

comment
on column VSP.VSP_USER.PHONE is '用户手机号';

comment
on column VSP.VSP_USER.EMAIL is '用户邮箱地址';

comment
on column VSP.VSP_USER.REAL_NAME is '用户真实名字';

comment
on column VSP.VSP_USER.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_USER.UPDATER is '更新者';

comment
on column VSP.VSP_USER.CREATOR is '创建者';

comment
on column VSP.VSP_USER.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_USER.CREATE_TIME is '创建时间';

comment
on column VSP.VSP_USER.ROLE_TYPE is '用户角色类型（1-管理员、2-普通用户）';

comment
on column VSP.VSP_USER.DESCRIPTION is '用户个人介绍';

create unique index VSP.INDEX33555489
    on VSP.VSP_USER (ID);

alter table VSP.VSP_USER
    add constraint VSP_USER_PK
        primary key (ID);

create table VSP.VSP_VMWARE_INFO
(
    ID             BIGINT(19) not null,
    NAME           VARCHAR(64) not null,
    UUID           VARCHAR(64) not null,
    DESCRIPTION    VARCHAR(256),
    HOST_ID        BIGINT(19) not null,
    STATE          INT(10) not null,
    CREATE_FORM    INT(10) not null,
    VCPU_MAX       INT(10) not null,
    VCPU_CURRENT   INT(10) not null,
    MEMORY_MAX     BIGINT(19) not null,
    MEMORY_CURRENT BIGINT(19) not null,
    IS_DELETED     INT(10) default 0 not null,
    UPDATER        BIGINT(19),
    CREATOR        BIGINT(19),
    UPDATE_TIME    TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6)
);

comment
on column VSP.VSP_VMWARE_INFO.ID is 'ID';

comment
on column VSP.VSP_VMWARE_INFO.NAME is '虚拟机名称';

comment
on column VSP.VSP_VMWARE_INFO.UUID is '虚拟机 UUID-自定义';

comment
on column VSP.VSP_VMWARE_INFO.DESCRIPTION is '虚拟机描述信息';

comment
on column VSP.VSP_VMWARE_INFO.HOST_ID is '虚拟机当前所属宿主机 ID';

comment
on column VSP.VSP_VMWARE_INFO.STATE is '虚拟机当前状态';

comment
on column VSP.VSP_VMWARE_INFO.CREATE_FORM is '虚拟机创建形式（1-ISO安装介质、2-现有磁盘镜像）';

comment
on column VSP.VSP_VMWARE_INFO.VCPU_MAX is '虚拟机 vCPU 最大数量';

comment
on column VSP.VSP_VMWARE_INFO.VCPU_CURRENT is '虚拟机 vCPU 当前数量';

comment
on column VSP.VSP_VMWARE_INFO.MEMORY_MAX is '虚拟机内存最大值（字节）';

comment
on column VSP.VSP_VMWARE_INFO.MEMORY_CURRENT is '虚拟机内存当前值（字节）';

comment
on column VSP.VSP_VMWARE_INFO.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_VMWARE_INFO.UPDATER is '更新者';

comment
on column VSP.VSP_VMWARE_INFO.CREATOR is '创建者';

comment
on column VSP.VSP_VMWARE_INFO.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_VMWARE_INFO.CREATE_TIME is '创建时间';

create unique index VSP.INDEX33555491
    on VSP.VSP_VMWARE_INFO (ID);

alter table VSP.VSP_VMWARE_INFO
    add constraint VSP_VMWARE_INFO_PK
        primary key (ID);

create table VSP.VSP_VMWARE_XML_DETAILS
(
    ID          BIGINT(19) not null,
    INIT_XML    LONGVARCHAR( max),
    LATEST_XML  LONGVARCHAR( max),
    IS_DELETED  INT(10) default 0 not null,
    UPDATER     BIGINT(19),
    CREATOR     BIGINT(19),
    UPDATE_TIME TIMESTAMP(39, 6
) ,
	CREATE_TIME TIMESTAMP(39,6)
);

comment
on column VSP.VSP_VMWARE_XML_DETAILS.ID is 'ID';

comment
on column VSP.VSP_VMWARE_XML_DETAILS.INIT_XML is '虚拟机初始化 XML 信息';

comment
on column VSP.VSP_VMWARE_XML_DETAILS.LATEST_XML is '虚拟机最新 XML 信息';

comment
on column VSP.VSP_VMWARE_XML_DETAILS.IS_DELETED is '逻辑删除';

comment
on column VSP.VSP_VMWARE_XML_DETAILS.UPDATER is '更新者';

comment
on column VSP.VSP_VMWARE_XML_DETAILS.CREATOR is '创建者';

comment
on column VSP.VSP_VMWARE_XML_DETAILS.UPDATE_TIME is '更新时间';

comment
on column VSP.VSP_VMWARE_XML_DETAILS.CREATE_TIME is '创建时间';

create unique index VSP.INDEX33555493
    on VSP.VSP_VMWARE_XML_DETAILS (ID);

alter table VSP.VSP_VMWARE_XML_DETAILS
    add constraint VSP_VMWARE_XML_DETAILS_PK
        primary key (ID);

