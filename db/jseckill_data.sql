INSERT INTO t_user (id, username, mobile, nickname, password, password_salt, user_scope, user_status)
VALUES (1, 'admin', '15300000000', '管理员', '3baabd68b1c33897f1afd011391aab61', '508768e2-309f-4370-bef7-b90a082e', 2, 1),
(2, '15300000000', '15300000000', '小王', '3baabd68b1c33897f1afd011391aab61', '508768e2-309f-4370-bef7-b90a082e', 1, 1);

INSERT INTO t_sk_goods (id, goods_name, goods_price, sk_price, sk_num, stock_num, start_time, end_time, entry_key, buy_limit, create_by, update_by)
VALUES (1, '秒杀商品1', 10000, 5000, 1000, 1000, '2023-04-01 10:00:00', '2023-04-01 11:00:00', '3baabd68', 0, 1, 1);
