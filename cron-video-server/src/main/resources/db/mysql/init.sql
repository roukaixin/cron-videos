-- 媒体列表信息
create table media
(
    id            bigint        not null comment '主键 id'
        primary key,
    name          varchar(100)  not null comment '电影/电视剧名称',
    type          varchar(10)   not null comment '媒体类型: movie / show',
    type_alias    varchar(255)  not null comment '媒体列别（如 电影=影片，电视剧=剧集）',
    season_number int           null comment '电视剧季号/部数（仅电视剧用、电影为 NULL）',
    total_episode int           not null comment '总集数（电影默认为1）',
    start_episode int           null comment '开始集数（beginEpisode）',
    release_date  datetime      not null comment '首播/上映日期',
    tmdb_id       int           null comment 'tmdb id',
    created_at    datetime      not null comment '创建时间',
    updated_at    int           null comment '更新时间',
    is_deleted    int default 0 not null comment '逻辑删除字段（0未删除,1已删除）'
)
    comment '媒体列表';

create table media_episode
(
    id             bigint        not null comment '主键 id'
        primary key,
    media_id       bigint        not null comment '与 media 关联',
    season_number  int           null comment '电视剧季号/部数（仅电视剧用、电影为 NULL）',
    episode_number int           not null comment '剧集',
    air_date       datetime      null comment '播出时间',
    is_update      int default 0 not null comment '是否更新（0否，1是）',
    created_at     datetime      not null comment '创建时间',
    updated_at     int           null comment '更新时间'
)
    comment '媒体库剧集信息';


create table cloud_share
(
    id            bigint               not null comment '主键 id'
        primary key,
    media_id      bigint               not null comment '与 media 关联',
    provider      int                  not null comment '网盘提供商（1: 夸克, 2: 阿里云盘, 3: 百度网盘 等',
    share_id      varchar(255)         not null comment '分享 ID (路径 ID 或链接)',
    share_code    varchar(255)         not null comment '提取码（部分网盘需要）',
    expired_at    datetime             null comment '过期时间 (部分网盘有限制)',
    file_regex    varchar(50)          null comment '用于提取文件的正则表达式',
    episode_regex varchar(50)          not null comment '匹配集数规则（用于匹配出重命名后文件名）',
    onlyIn_dir    varchar(255)         null comment '只在指定目录下有效的目录路径',
    excluded_dir  varchar(255)         null comment '排除的目录',
    is_lapse      int        default 0 not null comment '是否失效（0:否、1:是）',
    lapse_cause   varchar(255)         null comment '失效原因',
    created_at    datetime             not null comment '创建时间',
    updated_at    int                  null comment '更新时间',
    is_deleted    tinyint(1) default 0 not null comment '逻辑删除 (0=正常, 1=删除)'
)
    comment '网盘分享链接';

create table cloud_storage_auth
(
    id            bigint               not null comment '主键 id'
        primary key,
    provider      int                  not null comment '网盘提供商（1: 夸克, 2: 阿里云盘, 3: 百度网盘 等）',
    access_token  varchar(255)         null comment '访问令牌（某些网盘用）',
    refresh_token varchar(255)         null comment '刷新令牌（某些网盘用）',
    cookie        varchar(1000)        null comment '网盘 Cookie（如夸克需要的）',
    created_at    datetime             not null comment '创建时间',
    updated_at    int                  null comment '更新时间',
    is_deleted    tinyint(1) default 0 not null comment '逻辑删除 (0=正常, 1=删除)'
)
    comment '网盘认证信息存储';

create table downloader
(
    id         bigint               not null comment '主键 id'
        primary key,
    type       int                  not null comment '下载器类型。（0->aria2、1->qbittorrent）',
    protocol   varchar(10)          not null comment '协议。ws/http',
    host       varchar(100)         not null comment '下载器主机地址（IP 或域名）',
    port       int                  not null comment '监听端口',
    secret     varchar(255)         null comment 'RPC 密钥（Token）',
    weight     int        default 1 not null comment '服务器权重（用于负载均衡）',
    is_online  tinyint(1) default 0 not null comment '在线状态（1: 在线, 0: 离线）',
    created_at datetime             not null comment '创建时间',
    updated_at datetime             null comment '更新时间',
    is_deleted tinyint(1) default 0 not null comment '逻辑删除 (0=正常, 1=删除)'
)
    comment '下载器连接信息';

create table download_task
(
    id              bigint        not null comment '主键 id'
        primary key,
    media_id        bigint        not null comment '关联 media 表 id',
    downloader_id   bigint        not null comment '关联 downloader 表 id',
    gid             varchar(100)  not null comment 'Aria2 任务 ID（通常是 16/64 位字符串）',
    episode_number  int           not null comment '集数',
    save_path       varchar(255)  not null comment '存储路径',
    out_name        varchar(255)  not null comment '文件名',
    video_width     int           null comment '媒体分辨率宽度',
    video_height    int           null comment '媒体分辨率高度',
    size            bigint        null comment '文件大小',
    status          int default 0 not null comment '任务状态（0: 等待中, 1: 下载中, 2: 已完成, 3: 失败）',
    resource_status int default 0 not null comment '资源状态。0无、1只保留视频和音频、2已经自动到影视目录',
    created_at      datetime      not null comment '创建时间',
    updated_at      int           null comment '更新时间'
)
    comment '下载器下载任务';





