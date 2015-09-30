/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50155
Source Host           : localhost:3306
Source Database       : cattle

Target Server Type    : MYSQL
Target Server Version : 50155
File Encoding         : 65001

Date: 2015-07-31 10:12:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent` bigint(20) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_time` timestamp NULL DEFAULT NULL,
  `file_type` smallint(6) NOT NULL DEFAULT '0' COMMENT '0:文件,1:文件夹',
  PRIMARY KEY (`id`),
  KEY `parent` (`parent`),
  CONSTRAINT `file_ibfk_1` FOREIGN KEY (`parent`) REFERENCES `file` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of file
-- ----------------------------
INSERT INTO `file` VALUES ('1', null, '调度系统', '2015-07-27 21:50:16', '2015-07-27 21:50:16', '1');
INSERT INTO `file` VALUES ('13', '1', '新文件夹', '2015-07-29 18:02:47', null, '1');
INSERT INTO `file` VALUES ('14', '1', 'ggggg', '2015-07-29 18:02:50', null, '0');
INSERT INTO `file` VALUES ('16', '1', 'ttt', '2015-07-30 15:50:41', null, '0');

-- ----------------------------
-- Table structure for job
-- ----------------------------
DROP TABLE IF EXISTS `job`;
CREATE TABLE `job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `cron` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `dependencies` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `job_type` int(11) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `schedule_status` int(11) DEFAULT NULL,
  `schedule_type` int(11) DEFAULT NULL,
  `script` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `file_id` bigint(20) NOT NULL,
  `allocation_type` int(255) DEFAULT '0' COMMENT '0:自动 ，1：指定目标',
  `execution_machine` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `file_id` (`file_id`),
  CONSTRAINT `job_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of job
-- ----------------------------
INSERT INTO `job` VALUES ('2', null, '0 0 0 * * ?', '', '1', 'ggggg', null, '0', '请编辑修改ff', '14', '0', '12345');
INSERT INTO `job` VALUES ('4', null, '0 0 0 * * ?', '', '0', 'ttt', '1', '0', '请编辑修改', '16', '0', '');
INSERT INTO `job` VALUES ('5', null, '*/10 * * * * ?', '', '0', 'hhhh', '1', '0', '10s job', '16', '0', '');
INSERT INTO `job` VALUES ('6', null, '*/5 * * * * ?', '', '0', 'ggggg', '1', '0', '5s job', '16', '0', '');

-- ----------------------------
-- Table structure for job_history
-- ----------------------------
DROP TABLE IF EXISTS `job_history`;
CREATE TABLE `job_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `result` smallint(6) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `trigger_type` int(11) DEFAULT NULL,
  `job_id` bigint(20) NOT NULL,
  `execution_machine` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `job_id` (`job_id`),
  CONSTRAINT `job_history_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of job_history
-- ----------------------------


-- ----------------------------
-- Table structure for workers
-- ----------------------------
DROP TABLE IF EXISTS `workers`;
CREATE TABLE `workers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `worker_base_url` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of workers
-- ----------------------------
INSERT INTO `workers` VALUES ('1', 'http://127.0.0.1:8082', '本地测试', '2015-09-29 17:43:55');


