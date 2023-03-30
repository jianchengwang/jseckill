create database `jseckill` default character set utf8mb4 collate utf8mb4_general_ci;

create table t_seckill_activity
(
    id              bigint auto_increment comment '主键'
        primary key,
    activity_name   varchar(64)        not null comment '活动名称',
    start_time      datetime           not null comment '开始时间',
    end_time        datetime           not null comment '截止时间',
    activity_status smallint default 0 not null comment '状态'
)
    comment '秒杀活动';

create table t_seckill_goods
(
    id            bigint auto_increment comment '主键'
        primary key,
    activity_id   bigint       not null comment '秒杀活动编号',
    goods_id      bigint       not null comment '商品编号',
    goods_name    varchar(64)  not null comment '商品名称',
    goods_price   bigint       not null comment '商品价格',
    sup_id        bigint       not null comment 'spu编号',
    sku_id        bigint       not null comment 'sku编号',
    content       varchar(256) not null comment '描述',
    seckill_price bigint       not null comment '秒杀价格',
    seckill_num   bigint       not null comment '秒杀数量',
    store_count   bigint       not null comment '剩余库存'
)
    comment '秒杀商品';

create table t_seckill_order
(
    id                 bigint auto_increment comment '主键'
        primary key,
    seckill_goods_id   bigint             not null comment '秒杀商品编号',
    pay_money          bigint             not null comment '支付金额',
    create_time        datetime           not null comment '下单时间',
    pay_time           datetime           null comment '支付时间',
    pay_type           smallint default 0 not null comment '支付类型',
    pay_transaction_id varchar(64)        not null comment '支付流水',
    user_id            bigint             not null comment '用户编号',
    order_status       smallint default 0 not null comment '订单状态'
)
    comment '秒杀订单';

create table t_user
(
    id            bigint auto_increment
        primary key,
    nickname      varchar(32) not null comment '昵称',
    mobile        varchar(32) not null comment '手机号',
    password      varchar(32) not null comment '密码',
    password_salt varchar(32) not null comment '密码盐值'
)
    comment '用户表';
