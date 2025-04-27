-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID，主键，自增',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，唯一',
    password VARCHAR(100) NOT NULL COMMENT '密码，MD5加密存储',
    email VARCHAR(100) COMMENT '邮箱地址',
    phone VARCHAR(20) COMMENT '手机号码',
    role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '用户角色：admin-超级管理员，user-普通用户',
    status INT NOT NULL DEFAULT 1 COMMENT '用户状态：1-启用，0-禁用',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间'
) COMMENT '用户信息表';

-- 创建积分表
CREATE TABLE IF NOT EXISTS points (
    point_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '积分记录ID，主键，自增',
    user_id INT NOT NULL COMMENT '用户ID，关联users表的user_id',
    point_type VARCHAR(20) NOT NULL COMMENT '积分类型：reward-奖励积分，consume-消费积分',
    points INT NOT NULL COMMENT '积分值：正数表示获得，负数表示消费',
    recorded_at DATETIME NOT NULL COMMENT '记录时间',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE RESTRICT
) COMMENT '用户积分记录表';

-- 创建积分变动日志表
CREATE TABLE IF NOT EXISTS point_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID，主键，自增',
    user_id INT NOT NULL COMMENT '用户ID，关联users表的user_id',
    point_id INT NOT NULL COMMENT '积分记录ID，关联points表的point_id',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：add-增加积分，deduct-扣减积分，modify-修改积分',
    points_before INT NOT NULL COMMENT '变动前的积分余额',
    points_change INT NOT NULL COMMENT '变动的积分数量',
    points_after INT NOT NULL COMMENT '变动后的积分余额',
    operation_reason VARCHAR(200) COMMENT '操作原因',
    operation_time DATETIME NOT NULL COMMENT '操作时间',
    operator_id INT COMMENT '操作人ID，关联users表的user_id',
    operator_name VARCHAR(50) COMMENT '操作人用户名',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (point_id) REFERENCES points(point_id) ON UPDATE CASCADE ON DELETE RESTRICT
) COMMENT '积分变动日志表';

-- 初始化超级管理员账户
-- 用户名：admin
-- 密码：admin123（MD5加密后：0192023a7bbd73250516f069df18b500）
INSERT INTO users (username, password, email, role, status, created_at, updated_at)
VALUES ('admin', '0192023a7bbd73250516f069df18b500', 'admin@example.com', 'admin', 1, NOW(), NOW());

-- 初始化测试用户
-- 用户名：user1
-- 密码：123456（MD5加密后：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO users (username, password, email, role, status, created_at, updated_at)
VALUES ('user1', 'e10adc3949ba59abbe56e057f20f883e', 'user1@example.com', 'user', 1, NOW(), NOW());

-- 初始化测试积分数据
INSERT INTO points (user_id, point_type, points, recorded_at)
VALUES
    (1, 'reward', 100, NOW()); -- 管理员获得100奖励积分
INSERT INTO points (user_id, point_type, points, recorded_at)
VALUES
    (2, 'reward', 50, NOW()); -- 测试用户获得50奖励积分
INSERT INTO points (user_id, point_type, points, recorded_at)
VALUES
    (2, 'consume', -20, NOW()); -- 测试用户消费20积分

-- 初始化测试积分日志数据
INSERT INTO point_logs (user_id, point_id, operation_type, points_before, points_change, points_after, operation_reason, operation_time, operator_id, operator_name)
VALUES
    (1, 1, 'add', 0, 100, 100, '系统初始化奖励', NOW(), 1, 'admin');
INSERT INTO point_logs (user_id, point_id, operation_type, points_before, points_change, points_after, operation_reason, operation_time, operator_id, operator_name)
VALUES
    (2, 2, 'add', 0, 50, 50, '系统初始化奖励', NOW(), 1, 'admin');
INSERT INTO point_logs (user_id, point_id, operation_type, points_before, points_change, points_after, operation_reason, operation_time, operator_id, operator_name)
VALUES
    (2, 3, 'deduct', 50, -20, 30, '购买商品', NOW(), 1, 'admin');
