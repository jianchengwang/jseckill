INSERT INTO t_user (id, username, mobile, nickname, password, password_salt, user_scope, user_status) VALUES (1, '15300000000', '15300000000', '小王', '3baabd68b1c33897f1afd011391aab61', '508768e2-309f-4370-bef7-b90a082e', 1, 1);

INSERT INTO t_seckill_activity (id, activity_name, start_time, end_time, activity_status, entry_key) VALUES (1, '秒杀活动1', '2023-03-31 17:45:54', '2023-03-30 18:46:02', 0, '55677307-eb53-406f-b80e-0bcec2fa8ed6');
INSERT INTO t_seckill_goods (id, activity_id, goods_id, goods_name, goods_price, sup_id, sku_id, content, seckill_price, seckill_num, store_count) VALUES (1, 1, 1, '秒杀商品1', 10000, 1, 1, '秒杀商品1', 5000, 1000, 1000);
