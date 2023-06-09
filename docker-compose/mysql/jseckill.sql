create database `jseckill` default character set utf8mb4 collate utf8mb4_general_ci;
use `jseckill`;

create table t_user
(
    id            bigint auto_increment primary key,
    username     varchar(32) not null comment '用户名',
    nickname      varchar(32) not null comment '昵称',
    mobile        varchar(32) not null comment '手机号',
    password      varchar(32) not null comment '密码',
    password_salt varchar(32) not null comment '密码盐值',
    user_scope    smallint default 1 not null comment '用户归属',
    user_status   smallint default 1 not null comment '用户状态'
)
    comment '用户表';
create unique index t_user_username_uindex
    on t_user (username);

create table t_sk_goods
(
    id            bigint auto_increment comment '主键' primary key,
    goods_name    varchar(64)  not null comment '商品名称',
    goods_price   bigint       not null comment '商品价格',
    sk_price bigint       not null comment '秒杀价格',
    sk_num   bigint       not null comment '秒杀数量',
    stock_num    bigint       not null comment '库存数量',
    start_time      datetime           not null comment '开始时间',
    end_time        datetime           not null comment '截止时间',
    entry_key       varchar(64)        null comment '入口密钥',
    buy_limit        bigint            default -1 not null comment '购买限制',
    create_at       datetime           null comment '创建时间',
    create_by       bigint             null comment '创建人',
    update_at       datetime           null comment '更新时间',
    update_by       bigint             null comment '更新人'
)
    comment '秒杀商品';

create table t_sk_order
(
    id                 bigint auto_increment comment '主键' primary key,
    order_no           varchar(128)        not null comment '订单编号',
    sk_goods_id        bigint             not null comment '秒杀商品编号',
    sk_price           bigint          not null comment '秒杀价',
    buy_num            bigint             not null comment '购买数量',
    order_money        bigint             not null comment '订单金额',
    order_time         datetime           not null comment '下单时间',
    pay_money          bigint             default 0 not null comment '支付金额',
    pay_time           datetime           null comment '支付时间',
    pay_method         smallint default 0 not null comment '支付方式，0：未支付，1：支付宝，2：微信',
    pay_transaction_id varchar(128)       null comment '支付流水',
    user_id            bigint             not null comment '用户编号',
    order_status       smallint default 0 not null comment '订单状态'
)
    comment '秒杀订单';
create unique index t_sk_order_order_no_uindex
    on t_sk_order (order_no);

create table t_sk_pay_notify
(
    id                 bigint auto_increment comment '主键' primary key,
    out_trade_no       varchar(128)        not null comment '业务订单编号',
    pay_money          bigint             not null comment '支付金额',
    pay_time           datetime           not null comment '支付时间',
    pay_method         smallint default 0 not null comment '支付方式，0：未支付，1：支付宝，2：微信',
    pay_transaction_id varchar(128)        not null comment '支付流水',
    sign                varchar(64)        not null comment '签名',
    success           smallint           default 0 not null comment '是否成功',
    error_message     varchar(256)       null comment '错误信息',
    create_at         datetime           not null comment '创建时间'
)
    comment '秒杀支付通知';
create unique index t_sk_pay_notify_out_trade_no_uindex
    on t_sk_pay_notify (out_trade_no);
