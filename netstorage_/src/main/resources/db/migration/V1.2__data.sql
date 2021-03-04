INSERT INTO `hibernate_sequence` VALUES ('44');
INSERT INTO `hibernate_sequence` VALUES ('44');
INSERT INTO `hibernate_sequence` VALUES ('44');
INSERT INTO `hibernate_sequence` VALUES ('44');
INSERT INTO `hibernate_sequence` VALUES ('44');
INSERT INTO `hibernate_sequence` VALUES ('44');
INSERT INTO `hibernate_sequence` VALUES ('44');
INSERT INTO `hibernate_sequence` VALUES ('44');
INSERT INTO `hibernate_sequence` VALUES ('44');

INSERT INTO `t_permission` VALUES ('19', '上传');
INSERT INTO `t_permission` VALUES ('20', '下载');
INSERT INTO `t_permission` VALUES ('37', '插件');
INSERT INTO `t_permission` VALUES ('38', '共享');
INSERT INTO `t_permission` VALUES ('39', '投屏');
INSERT INTO `t_permission` VALUES ('40', '外网访问');
INSERT INTO `t_permission` VALUES ('41', 'bt下载');
INSERT INTO `t_permission` VALUES ('42', '打印');
INSERT INTO `t_permission` VALUES ('43', '离线下载');

INSERT INTO `t_role` VALUES ('1', 'admin', '');
INSERT INTO `t_role` VALUES ('10', '普通用户', '');

INSERT INTO `t_role_permission` VALUES ('1', '19');
INSERT INTO `t_role_permission` VALUES ('1', '20');
INSERT INTO `t_role_permission` VALUES ('10', '19');
INSERT INTO `t_role_permission` VALUES ('10', '20');

INSERT INTO `t_user` VALUES ('6', '1607489465949', 'admin@test.com', 'admin', 'd7db29350633c6a73d2591a3f8101b0e', '1');


INSERT INTO `t_user_role` VALUES ('6', '1');
INSERT INTO `t_user_role` VALUES ('6', '10');
