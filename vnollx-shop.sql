create table card_item
(
    id           bigint unsigned auto_increment comment '主键ID'
        primary key,
    uid          bigint unsigned                           not null comment '用户ID',
    pid          bigint unsigned                           not null comment '商品ID',
    number       bigint unsigned default '1'               not null comment '商品数量',
    unit_price   decimal(10, 2) unsigned                   not null comment '商品单价',
    create_time  datetime        default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime        default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    product_name varchar(64)                               null comment '商品名称',
    cover        varchar(255)                              null comment '商品图片地址',
    constraint uk_uid_pid
        unique (uid, pid)
)
    comment '购物车项表' collate = utf8mb4_unicode_ci;

create index idx_create_time
    on card_item (create_time);

create index idx_uid
    on card_item (uid);

create table order_item
(
    id           bigint auto_increment comment '订单项ID'
        primary key,
    oid          bigint                      not null comment '订单ID',
    pid          bigint                      not null comment '商品ID',
    number       int            default 1    not null comment '购买数量',
    cost         decimal(15, 2) default 0.00 not null comment '该商品的总花费',
    unit_price   decimal(15, 2) default 0.00 not null comment '商品单价',
    product_name varchar(255)                null comment '商品名称',
    cover        varchar(255)                null
)
    comment '订单项表';

create index idx_oid
    on order_item (oid)
    comment '订单ID索引';

create index idx_pid
    on order_item (pid)
    comment '商品ID索引';

create table orders
(
    id          bigint auto_increment comment '订单ID'
        primary key,
    uid         bigint                                   not null comment '用户ID',
    consignee   varchar(100)                             not null comment '收货人姓名',
    email       varchar(100)                             null comment '邮箱',
    address     varchar(500)                             not null comment '收货地址',
    remark      varchar(500)                             null comment '订单备注',
    total_cost  decimal(15, 2) default 0.00              not null comment '订单总金额',
    total_items int            default 0                 not null comment '订单总商品数',
    create_time datetime       default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '订单主表';

create index idx_consignee
    on orders (consignee)
    comment '收货人索引';

create index idx_create_time
    on orders (create_time)
    comment '创建时间索引';

create index idx_email
    on orders (email)
    comment '邮箱索引';

create index idx_uid
    on orders (uid)
    comment '用户ID索引';

create table product
(
    id          bigint                      not null comment '主键ID'
        primary key,
    name        varchar(255)                not null comment '商品名称',
    stock       bigint         default 0    null comment '库存',
    price       decimal(10, 2) default 0.00 null comment '价格',
    category    varchar(100)                null comment '分类',
    brand       varchar(100)                null comment '商品品牌',
    introduce   text                        null comment '商品简介',
    visit_count bigint         default 0    null comment '游览总数',
    like_count  bigint         default 0    null comment '点赞人数',
    score       decimal(3, 2)  default 0.00 null comment '评分',
    score_count bigint         default 0    null comment '评分人数',
    sales       bigint         default 0    null comment '销量',
    version     bigint         default 0    null comment '乐观锁版本号',
    cover       varchar(255)                null comment '商品封面',
    type        char           default '0'  null
)
    comment '商品表';

create index idx_product_category
    on product (category);

create index idx_product_name
    on product (name);

create index idx_product_price
    on product (price);

create index idx_product_score
    on product (score);

create table product_like
(
    id  bigint auto_increment
        primary key,
    uid bigint null,
    pid bigint null,
    constraint product_like_uid_pid_uindex
        unique (uid, pid)
)
    comment '关联用户和商品的点赞行为';

create table user
(
    name     varchar(255)     null comment '用户名',
    id       bigint           not null comment '用户ID(雪花算法生成)'
        primary key,
    password varchar(128)     null comment '用户密码',
    avatar   varchar(255)     null comment '用户头像地址',
    email    varchar(50)      null comment '用户邮箱',
    balance  double default 0 null comment '余额',
    salt     varchar(255)     null comment '盐值',
    constraint user_email_uindex
        unique (email)
)
    comment '用户表';

