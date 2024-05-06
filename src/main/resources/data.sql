-- un1/p1
INSERT INTO users(id, user_name, password)
VALUES (gen_random_uuid(), 'un1', '$2a$10$PvJ4wpK7WKGbqshfv9fc5e3SO/LrFi5ynKzqonD8nLHJLeC8pysxm');

-- un2/p2
INSERT INTO users(id, user_name, password)
VALUES (gen_random_uuid(), 'un2', '$2a$10$w8nhBiaCLms/G3Hc2P9rdO/Jf778AH9XTwwN7CutvFCqUkHEdhrLu');