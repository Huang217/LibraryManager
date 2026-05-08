-- =====================================================
-- 模拟 Oracle 特性：用户、表空间、序列、触发器、存储过程、视图、大对象
-- MySQL 版本要求 5.7+ / 8.0+
-- =====================================================

-- 1. “表空间” 使用独立数据库模拟
CREATE DATABASE IF NOT EXISTS TS_LIBRARY CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE TS_LIBRARY;

-- 2. 用户授权（模拟 Oracle 用户，可在 MySQL 中执行）
-- DROP USER IF EXISTS 'lib_admin'@'%';
-- CREATE USER 'lib_admin'@'%' IDENTIFIED BY 'Lib123456';
-- GRANT ALL PRIVILEGES ON TS_LIBRARY.* TO 'lib_admin'@'%';
-- FLUSH PRIVILEGES;

-- 3. 序列表 (模拟 Oracle SEQUENCE)
DROP TABLE IF EXISTS seq_table;
CREATE TABLE seq_table (
    seq_name VARCHAR(50) PRIMARY KEY,
    current_val BIGINT NOT NULL,
    increment_val INT NOT NULL DEFAULT 1
) ENGINE=InnoDB;

INSERT INTO seq_table VALUES ('borrow_id_seq', 1000, 1);
INSERT INTO seq_table VALUES ('user_id_seq', 100, 1);   -- 备用，实际使用自增

-- 模拟序列的 NEXTVAL 函数
DELIMITER //
CREATE FUNCTION nextval(seq_name_param VARCHAR(50)) RETURNS BIGINT
READS SQL DATA
DETERMINISTIC
BEGIN
    UPDATE seq_table SET current_val = current_val + increment_val WHERE seq_name = seq_name_param;
    RETURN (SELECT current_val FROM seq_table WHERE seq_name = seq_name_param);
END //
DELIMITER ;

-- 4. 用户表 (角色：admin / reader)
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(10) NOT NULL CHECK (role IN ('admin','reader')),
    email VARCHAR(100),
    status TINYINT DEFAULT 1 COMMENT '1:正常 0:禁用'
) ENGINE=InnoDB;

-- 5. 图书表 (包含大对象存储封面)
DROP TABLE IF EXISTS books;
CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) UNIQUE,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100),
    press VARCHAR(100),
    category VARCHAR(50),
    cover LONGBLOB COMMENT '图书封面（大对象）',
    total INT DEFAULT 1,
    remain INT DEFAULT 1,
    status TINYINT DEFAULT 1 COMMENT '1:可借 0:下架'
) ENGINE=InnoDB;

-- 6. 借阅表 (主键使用序列生成)
DROP TABLE IF EXISTS borrows;
CREATE TABLE borrows (
    borrow_id BIGINT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    return_time DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
) ENGINE=InnoDB;

-- 7. 索引 (常用查询列)
CREATE INDEX idx_books_category ON books(category);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_borrows_user ON borrows(user_id);

-- 8. 视图 (借阅详情)
CREATE OR REPLACE VIEW v_borrow_detail AS
SELECT b.borrow_id,
       u.username,
       bk.title,
       bk.isbn,
       b.borrow_time,
       b.return_time,
       CASE WHEN b.return_time IS NULL THEN '借出' ELSE '已还' END AS state
FROM borrows b
JOIN users u ON b.user_id = u.user_id
JOIN books bk ON b.book_id = bk.book_id;

-- 9. 存储过程：借书 (使用序列生成 borrow_id)
DELIMITER //
CREATE PROCEDURE sp_borrow_book(
    IN p_user_id INT,
    IN p_book_id INT,
    OUT p_result VARCHAR(100)
)
BEGIN
    DECLARE v_remain INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = '借阅失败：系统错误';
    END;
    START TRANSACTION;
    SELECT remain INTO v_remain FROM books WHERE book_id = p_book_id FOR UPDATE;
    IF v_remain > 0 THEN
        INSERT INTO borrows (borrow_id, user_id, book_id)
        VALUES (nextval('borrow_id_seq'), p_user_id, p_book_id);
        UPDATE books SET remain = remain - 1 WHERE book_id = p_book_id;
        COMMIT;
        SET p_result = '借阅成功';
    ELSE
        ROLLBACK;
        SET p_result = '借阅失败：无可用库存';
    END IF;
END //
DELIMITER ;

-- 10. 存储过程：还书
DELIMITER //
CREATE PROCEDURE sp_return_book(
    IN p_borrow_id BIGINT,
    OUT p_result VARCHAR(100)
)
BEGIN
    DECLARE v_book_id INT;
    DECLARE v_return_time DATETIME;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = '还书失败';
    END;
    SELECT return_time INTO v_return_time FROM borrows WHERE borrow_id = p_borrow_id;
    IF v_return_time IS NOT NULL THEN
        SET p_result = '该书已归还';
    ELSE
        START TRANSACTION;
        SELECT book_id INTO v_book_id FROM borrows WHERE borrow_id = p_borrow_id;
        UPDATE borrows SET return_time = NOW() WHERE borrow_id = p_borrow_id;
        UPDATE books SET remain = remain + 1 WHERE book_id = v_book_id;
        COMMIT;
        SET p_result = '还书成功';
    END IF;
END //
DELIMITER ;

-- 11. 触发器：记录借阅日志
DROP TABLE IF EXISTS borrow_log;
CREATE TABLE borrow_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    borrow_id BIGINT,
    action VARCHAR(20),
    log_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

DELIMITER //
CREATE TRIGGER trg_borrow_after_insert AFTER INSERT ON borrows
FOR EACH ROW
BEGIN
    INSERT INTO borrow_log (borrow_id, action) VALUES (NEW.borrow_id, 'BORROW');
END //
DELIMITER ;

-- 12. 初始数据
INSERT INTO users (username, password, role) VALUES ('admin', '123456', 'admin');
INSERT INTO users (username, password, role) VALUES ('reader1', '123456', 'reader');
INSERT INTO books (isbn, title, author, press, category, total, remain)
VALUES ('978-7-111-40797-5', 'Java编程思想', 'Bruce Eckel', '机械工业出版社', '计算机', 3, 3);
INSERT INTO books (isbn, title, author, press, category, total, remain)
VALUES ('978-7-302-36363-5', '数据库系统概念', 'Abraham Silberschatz', '机械工业出版社', '计算机', 2, 2);
INSERT INTO books (isbn, title, author, press, category, total, remain)
VALUES ('978-7-115-46896-8', '深入浅出MySQL', '祝定泽', '人民邮电出版社', '数据库', 2, 2);