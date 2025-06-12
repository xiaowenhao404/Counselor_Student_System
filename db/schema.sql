-- 创建数据库
CREATE DATABASE IF NOT EXISTS counselor_student_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE counselor_student_system;

-- 设置外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for 院系
-- ----------------------------
DROP TABLE IF EXISTS `院系`;
CREATE TABLE `院系` (
  `院系编号` VARCHAR(50) NOT NULL,
  `院系名称` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`院系编号`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for 专业
-- ----------------------------
DROP TABLE IF EXISTS `专业`;
CREATE TABLE `专业` (
  `专业编号` VARCHAR(50) NOT NULL,
  `专业名称` VARCHAR(100) NOT NULL,
  `院系编号` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`专业编号`),
  CONSTRAINT `专业_ibfk_1` FOREIGN KEY (`院系编号`) REFERENCES `院系` (`院系编号`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for 辅导员
-- ----------------------------
DROP TABLE IF EXISTS `辅导员`;
CREATE TABLE `辅导员` (
  `辅导员工号` VARCHAR(50) NOT NULL,
  `姓名` VARCHAR(100) NOT NULL,
  `性别` VARCHAR(10),
  `出生日期` DATE,
  `手机号码` VARCHAR(20),
  `密码` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`辅导员工号`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for 班级
-- ----------------------------
DROP TABLE IF EXISTS `班级`;
CREATE TABLE `班级` (
  `专业编号` VARCHAR(50) NOT NULL,
  `年级编号` VARCHAR(10) NOT NULL,
  `班级编号` VARCHAR(10) NOT NULL,
  `辅导员工号` VARCHAR(10) NULL,
  PRIMARY KEY (`专业编号`, `年级编号`, `班级编号`),
  CONSTRAINT `班级_ibfk_1` FOREIGN KEY (`专业编号`) REFERENCES `专业` (`专业编号`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `班级_ibfk_2` FOREIGN KEY (`辅导员工号`) REFERENCES `辅导员` (`辅导员工号`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for 学生
-- ----------------------------
DROP TABLE IF EXISTS `学生`;
CREATE TABLE `学生` (
  `学生学号` VARCHAR(50) NOT NULL,
  `专业编号` VARCHAR(50) NOT NULL,
  `年级编号` VARCHAR(10) NOT NULL,
  `班级编号` VARCHAR(10) NOT NULL,
  `姓名` VARCHAR(100) NOT NULL,
  `性别` VARCHAR(10),
  `出生日期` DATE,
  `手机号码` VARCHAR(20),
  `密码` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`学生学号`),
  CONSTRAINT `学生_ibfk_1` FOREIGN KEY (`专业编号`) REFERENCES `专业` (`专业编号`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `学生_ibfk_2` FOREIGN KEY (`专业编号`, `年级编号`, `班级编号`) REFERENCES `班级` (`专业编号`, `年级编号`, `班级编号`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for 咨询
-- ----------------------------
DROP TABLE IF EXISTS `咨询`;
CREATE TABLE `咨询` (
  `Q编号` VARCHAR(10) NOT NULL,
  `学生学号` VARCHAR(50) NOT NULL,
  `类别` ENUM('学习', '生活', '其他') NOT NULL,
  `状态` ENUM('未回复', '已解决', '仍需解决') NOT NULL DEFAULT '未回复',
  `提问标题` VARCHAR(50) NOT NULL,
  `提问内容` TEXT NOT NULL,
  `提问时间` DATETIME NOT NULL,
  `是否加精` BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (`Q编号`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for 收藏
-- ----------------------------
DROP TABLE IF EXISTS `收藏`;
CREATE TABLE `收藏` (
  `Q编号` VARCHAR(10) NOT NULL,
  `学生学号` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`Q编号`, `学生学号`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for 回复
-- ----------------------------
DROP TABLE IF EXISTS `回复`;
CREATE TABLE `回复` (
  `R编号` VARCHAR(10) NOT NULL,
  `Q编号` VARCHAR(10) NOT NULL,
  `回复内容` TEXT NOT NULL,
  `回复时间` DATETIME NOT NULL,
  PRIMARY KEY (`R编号`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for 追问
-- ----------------------------
DROP TABLE IF EXISTS `追问`;
CREATE TABLE `追问` (
  `F编号` VARCHAR(10) NOT NULL,
  `Q编号` VARCHAR(10) NOT NULL,
  `追问内容` TEXT NOT NULL,
  `追问时间` DATETIME NOT NULL,
  PRIMARY KEY (`F编号`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- View structure for 学生视图
-- ----------------------------
DROP VIEW IF EXISTS `学生视图`;
CREATE VIEW `学生视图` AS
SELECT 
  s.学生学号,
  s.姓名,
  s.性别,
  s.出生日期,
  s.手机号码,
  m.专业名称,
  s.年级编号,
  s.班级编号,
  c.姓名 AS 辅导员姓名
FROM 学生 s
JOIN 专业 m ON s.专业编号 = m.专业编号
JOIN 班级 b ON (s.专业编号 = b.专业编号 AND s.年级编号 = b.年级编号 AND s.班级编号 = b.班级编号)
LEFT JOIN 辅导员 c ON b.辅导员工号 = c.辅导员工号;

-- ----------------------------
-- View structure for 辅导员视图
-- ----------------------------
DROP VIEW IF EXISTS `辅导员视图`;
CREATE VIEW `辅导员视图` AS
SELECT 
  co.辅导员工号,
  co.姓名,
  co.性别,
  co.出生日期,
  co.手机号码,
  d.院系名称,
  GROUP_CONCAT(DISTINCT CONCAT(m.专业名称, ' ', b.年级编号, '级', b.班级编号, '班') SEPARATOR ', ') AS 负责班级
FROM 辅导员 co
LEFT JOIN 班级 b ON co.辅导员工号 = b.辅导员工号
LEFT JOIN 专业 m ON b.专业编号 = m.专业编号
LEFT JOIN 院系 d ON m.院系编号 = d.院系编号
GROUP BY co.辅导员工号, d.院系名称;

-- ----------------------------
-- View structure for 班级视图
-- ----------------------------
DROP VIEW IF EXISTS `班级视图`;
CREATE VIEW `班级视图` AS
SELECT 
  c.专业编号,
  c.年级编号,
  c.班级编号,
  c.辅导员工号,
  m.专业名称,
  coalesce(t.学生人数, 0) AS 学生人数,
  co.姓名 AS 辅导员姓名
FROM 班级 c
LEFT JOIN 专业 m ON c.专业编号 = m.专业编号
LEFT JOIN 辅导员 co ON c.辅导员工号 = co.辅导员工号
LEFT JOIN (
  SELECT 专业编号, 年级编号, 班级编号, COUNT(*) AS 学生人数
  FROM 学生
  GROUP BY 专业编号, 年级编号, 班级编号
) t ON c.专业编号 = t.专业编号 AND c.年级编号 = t.年级编号 AND c.班级编号 = t.班级编号;

-- ----------------------------
-- View structure for 咨询汇总视图
-- ----------------------------
DROP VIEW IF EXISTS `咨询汇总视图`;
CREATE VIEW `咨询汇总视图` AS
SELECT 
  c.Q编号,
  c.学生学号,
  MAX(s.姓名) AS 学生姓名,
  MAX(c.类别) AS 类别,
  MAX(c.状态) AS 状态,
  MAX(c.提问标题) AS 提问标题,
  MAX(c.提问内容) AS 提问内容,
  MAX(c.提问时间) AS 提问时间,
  COUNT(DISTINCT r.R编号) AS 回复次数,
  COUNT(DISTINCT f.F编号) AS 追问次数,
  MAX(c.是否加精) AS 是否加精
FROM 咨询 c
LEFT JOIN 回复 r ON c.Q编号 = r.Q编号
LEFT JOIN 追问 f ON c.Q编号 = f.Q编号
JOIN 学生 s ON c.学生学号 = s.学生学号
GROUP BY c.Q编号, c.学生学号;

-- ----------------------------
-- Triggers
-- ----------------------------

-- 删除咨询触发器
DELIMITER ;;
CREATE TRIGGER `删除咨询前`
BEFORE DELETE ON `咨询`
FOR EACH ROW
BEGIN
    DELETE FROM 回复 WHERE Q编号 = OLD.Q编号;
    DELETE FROM 追问 WHERE Q编号 = OLD.Q编号;
    DELETE FROM 收藏 WHERE Q编号 = OLD.Q编号;
END;;
DELIMITER ;

-- 删除学生触发器
DELIMITER ;;
CREATE TRIGGER `删除学生前`
BEFORE DELETE ON `学生`
FOR EACH ROW
BEGIN
    -- 只删除收藏记录，保留咨询历史
    DELETE FROM 收藏 WHERE 学生学号 = OLD.学生学号;
END;;
DELIMITER ;

-- 删除辅导员触发器
DELIMITER ;;
CREATE TRIGGER `删除辅导员前`
BEFORE DELETE ON `辅导员`
FOR EACH ROW
BEGIN
    UPDATE 班级 SET 辅导员工号 = NULL WHERE 辅导员工号 = OLD.辅导员工号;
END;;
DELIMITER ;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1; 