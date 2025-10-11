-- 插入测试用户（密码使用BCrypt加密，原始密码是123456）
DELETE FROM sys_user WHERE username IN ('admin', 'testuser');
INSERT INTO sys_user (username, password, email, status, create_time, update_time)
VALUES
    ('admin', '$2a$10$5Z11MPZhf0lgdA.CYVcTxOa09ZezI8vfoA66m4q6YBFcMsx/jA4sO', 'admin@example.com', 1, NOW(), NOW()),
    ('testuser', '$2a$10$5Z11MPZhf0lgdA.CYVcTxOa09ZezI8vfoA66m4q6YBFcMsx/jA4sO', 'test@example.com', 1, NOW(), NOW());