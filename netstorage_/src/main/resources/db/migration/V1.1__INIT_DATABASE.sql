SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS `t_aria2` (
  `gid` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if NOT EXISTS  `t_device` (
  `id` bigint(20) NOT NULL,
  `custom_name` varchar(255) DEFAULT NULL,
  `device_name` varchar(255) DEFAULT NULL,
  `size` bigint(20) DEFAULT NULL,
  `folder_name` varchar(255) DEFAULT NULL,
  `rules` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if NOT EXISTS  `t_files` (
  `fid` bigint(20) NOT NULL,
  `create_date` datetime DEFAULT NULL,
  `is_dir` smallint(6) DEFAULT NULL,
  `parent_name` varchar(255) DEFAULT NULL,
  `self_name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`fid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_aria2_user
-- ----------------------------

CREATE TABLE if NOT EXISTS  `t_aria2_user` (
  `aria2file_gid` varchar(255) NOT NULL,
  `user_uid` bigint(20) NOT NULL,
  KEY `FKdlxti3ten0q7qhf8d9wh7igha` (`user_uid`),
  KEY `FKb5po7n0pl2q36j660uwqi8snd` (`aria2file_gid`),
  CONSTRAINT `FKb5po7n0pl2q36j660uwqi8snd` FOREIGN KEY (`aria2file_gid`) REFERENCES `t_aria2` (`gid`),
  CONSTRAINT `FKdlxti3ten0q7qhf8d9wh7igha` FOREIGN KEY (`user_uid`) REFERENCES `t_user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_files_aria2file
-- ----------------------------
 
# CREATE TABLE if NOT EXISTS  `t_files_aria2file` (
#   `files_fid` bigint(20) NOT NULL,
#   `aria2file_gid` varchar(255) NOT NULL,
#   PRIMARY KEY (`files_fid`,`aria2file_gid`),
#   KEY `FKg0nbjmub0acw9ofk69ct257rw` (`aria2file_gid`),
#   CONSTRAINT `FK99ves29ru7vd4i3rvrxajvt20` FOREIGN KEY (`files_fid`) REFERENCES `t_files` (`fid`),
#   CONSTRAINT `FKg0nbjmub0acw9ofk69ct257rw` FOREIGN KEY (`aria2file_gid`) REFERENCES `t_aria2` (`gid`)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_files_origin_file
-- ----------------------------
 
CREATE TABLE if NOT EXISTS  `t_files_origin_file` (
  `files_fid` bigint(20) NOT NULL,
  `origin_file_oid` bigint(20) NOT NULL,
  PRIMARY KEY (`files_fid`,`origin_file_oid`),
  KEY `FKd51pyjqfhklu0qp9ot1o9ps18` (`origin_file_oid`),
  CONSTRAINT `FKd51pyjqfhklu0qp9ot1o9ps18` FOREIGN KEY (`origin_file_oid`) REFERENCES `t_origin_file` (`oid`),
  CONSTRAINT `FKdmqnayf7enixsvxh64ihpqxjh` FOREIGN KEY (`files_fid`) REFERENCES `t_files` (`fid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_files_user` (
  `files_fid` bigint(20) NOT NULL,
  `user_uid` bigint(20) NOT NULL,
  KEY `FKggqn8gc1eyvlnxynexil8oa1n` (`user_uid`),
  KEY `FK9vha9ahu0dlow77avjmhekyja` (`files_fid`),
  CONSTRAINT `FK9vha9ahu0dlow77avjmhekyja` FOREIGN KEY (`files_fid`) REFERENCES `t_files` (`fid`),
  CONSTRAINT `FKggqn8gc1eyvlnxynexil8oa1n` FOREIGN KEY (`user_uid`) REFERENCES `t_user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if NOT EXISTS  `t_files_version` (
  `group_id` bigint(20) NOT NULL,
  `desc_` varchar(255) DEFAULT NULL,
  `group_name` varchar(255) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `version` double DEFAULT NULL,
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_files_version_origin_file_set` (
  `files_version_group_id` bigint(20) NOT NULL,
  `origin_file_set_oid` bigint(20) NOT NULL,
  PRIMARY KEY (`files_version_group_id`,`origin_file_set_oid`),
  KEY `FK1215jr95qq2yebe1l7stmhxjg` (`origin_file_set_oid`),
  CONSTRAINT `FK1215jr95qq2yebe1l7stmhxjg` FOREIGN KEY (`origin_file_set_oid`) REFERENCES `t_origin_file` (`oid`),
  CONSTRAINT `FKgi4o1xmqff9ag2rcenwk24n0u` FOREIGN KEY (`files_version_group_id`) REFERENCES `t_files_version` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_files_version_users` (
  `files_version_group_id` bigint(20) NOT NULL,
  `users_uid` bigint(20) NOT NULL,
  PRIMARY KEY (`files_version_group_id`,`users_uid`),
  KEY `FKgux13k3ufteqolyxvhdcqfe8p` (`users_uid`),
  CONSTRAINT `FKgux13k3ufteqolyxvhdcqfe8p` FOREIGN KEY (`users_uid`) REFERENCES `t_user` (`uid`),
  CONSTRAINT `FKk6tbx7q1mkbfwrsmh2lnseiyr` FOREIGN KEY (`files_version_group_id`) REFERENCES `t_files_version` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_folder` (
  `id` bigint(20) NOT NULL,
  `folder_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_folder_files` (
  `folder_id` bigint(20) NOT NULL,
  `files_fid` bigint(20) NOT NULL,
  PRIMARY KEY (`folder_id`,`files_fid`),
  KEY `FKcex5od6uv3840uv29kmtx2jgh` (`files_fid`),
  CONSTRAINT `FK4dltjgx9ps9rxa4v98v317irg` FOREIGN KEY (`folder_id`) REFERENCES `t_folder` (`id`),
  CONSTRAINT `FKcex5od6uv3840uv29kmtx2jgh` FOREIGN KEY (`files_fid`) REFERENCES `t_files` (`fid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_folder_origin_user` (
  `folder_id` bigint(20) NOT NULL,
  `origin_user_uid` bigint(20) NOT NULL,
  PRIMARY KEY (`folder_id`,`origin_user_uid`),
  KEY `FKd3deu16qun9fjvfu6n5ne8pkk` (`origin_user_uid`),
  CONSTRAINT `FKd3deu16qun9fjvfu6n5ne8pkk` FOREIGN KEY (`origin_user_uid`) REFERENCES `t_user` (`uid`),
  CONSTRAINT `FKhjjvg9funt2p71huph0icp3d3` FOREIGN KEY (`folder_id`) REFERENCES `t_folder` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_folder_users` (
  `folder_id` bigint(20) NOT NULL,
  `users_uid` bigint(20) NOT NULL,
  KEY `FKhy2q3m2kgf65n4f2egej1w5eu` (`users_uid`),
  KEY `FKnvp33iaji4b82ib7nuucf5iw4` (`folder_id`),
  CONSTRAINT `FKhy2q3m2kgf65n4f2egej1w5eu` FOREIGN KEY (`users_uid`) REFERENCES `t_user` (`uid`),
  CONSTRAINT `FKnvp33iaji4b82ib7nuucf5iw4` FOREIGN KEY (`folder_id`) REFERENCES `t_folder` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


 
CREATE TABLE if NOT EXISTS  `t_music_collection` (
  `id` bigint(20) NOT NULL,
  `collection_name` varchar(255) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `user` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK93ben3xxhu2vml4j7udtd37u6` (`user`),
  CONSTRAINT `FK93ben3xxhu2vml4j7udtd37u6` FOREIGN KEY (`user`) REFERENCES `t_user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_music_collection_song_files` (
  `music_collection_id` bigint(20) NOT NULL,
  `song_files_fid` bigint(20) NOT NULL,
  PRIMARY KEY (`music_collection_id`,`song_files_fid`),
  UNIQUE KEY `UK_6onj62cm49aft9b5o665pa4vt` (`song_files_fid`),
  CONSTRAINT `FKdi3bj1010w5e20unvj88g1tnj` FOREIGN KEY (`song_files_fid`) REFERENCES `t_files` (`fid`),
  CONSTRAINT `FKmay3wynrnju5n5awk7246p4u0` FOREIGN KEY (`music_collection_id`) REFERENCES `t_music_collection` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if NOT EXISTS  `t_origin_file` (
  `oid` bigint(20) NOT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `md5` varchar(255) DEFAULT NULL,
  `size` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_origin_file_hard_disk_device` (
  `origin_file_oid` bigint(20) NOT NULL,
  `hard_disk_device_id` bigint(20) NOT NULL,
  PRIMARY KEY (`origin_file_oid`,`hard_disk_device_id`),
  KEY `FKlruwh3ryx1u3p3msyp1ffh72u` (`hard_disk_device_id`),
  CONSTRAINT `FKlruwh3ryx1u3p3msyp1ffh72u` FOREIGN KEY (`hard_disk_device_id`) REFERENCES `t_device` (`id`),
  CONSTRAINT `FKp209ao9tx9twiv63rdc8cfgyq` FOREIGN KEY (`origin_file_oid`) REFERENCES `t_origin_file` (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_permission` (
  `pid` bigint(20) NOT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_plugin` (
  `id` bigint(20) NOT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `mapping` varchar(255) DEFAULT NULL,
  `plugin_name` varchar(255) DEFAULT NULL,
  `port` varchar(255) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_role` (
  `rid` bigint(20) NOT NULL,
  `name` varchar(20) NOT NULL,
  `status` bit(1) DEFAULT NULL,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if NOT EXISTS  `t_role_permission` (
  `role_rid` bigint(20) NOT NULL,
  `permission_pid` bigint(20) NOT NULL,
  PRIMARY KEY (`role_rid`,`permission_pid`),
  KEY `FKrbrki7fobj8qv3q2dy9jwi5ys` (`permission_pid`),
  CONSTRAINT `FK1q6072j7oh4h5pk07uthn3rvk` FOREIGN KEY (`role_rid`) REFERENCES `t_role` (`rid`),
  CONSTRAINT `FKrbrki7fobj8qv3q2dy9jwi5ys` FOREIGN KEY (`permission_pid`) REFERENCES `t_permission` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE if NOT EXISTS  `t_rss` (
  `id` bigint(20) NOT NULL,
  `cron` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `user` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKog18sbcm210lsw7x1ndb2l1vh` (`user`),
  CONSTRAINT `FKog18sbcm210lsw7x1ndb2l1vh` FOREIGN KEY (`user`) REFERENCES `t_user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if NOT EXISTS  `t_token` (
  `id` bigint(20) NOT NULL,
  `token` varchar(255) NOT NULL,
  `user` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6v50finhpx4gmna9why0jbcbp` (`user`,`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if NOT EXISTS  `t_user` (
  `uid` bigint(20) NOT NULL,
  `create_date` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `nick_name` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `UKi6qjjoe560mee5ajdg7v1o6mi` (`email`),
  KEY `FKoq4kvs7cl82x4jucwxbnmkqxe` (`role`),
  CONSTRAINT `FKoq4kvs7cl82x4jucwxbnmkqxe` FOREIGN KEY (`role`) REFERENCES `t_role` (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if NOT EXISTS  `t_user_role` (
  `user_uid` bigint(20) NOT NULL,
  `role_rid` bigint(20) NOT NULL,
  PRIMARY KEY (`user_uid`,`role_rid`),
  KEY `FKs0cuo6bf2th32lh8xg4vsqyj9` (`role_rid`),
  CONSTRAINT `FKgc4m89ebbmf1eyrxk21lfpsk7` FOREIGN KEY (`user_uid`) REFERENCES `t_user` (`uid`),
  CONSTRAINT `FKs0cuo6bf2th32lh8xg4vsqyj9` FOREIGN KEY (`role_rid`) REFERENCES `t_role` (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if NOT EXISTS  `t_video_collection` (
  `id` bigint(20) NOT NULL,
  `date` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `spec` varchar(255) DEFAULT NULL,
  `user` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmm9yi2nerh2rq353ww8pwljmv` (`user`),
  CONSTRAINT `FKmm9yi2nerh2rq353ww8pwljmv` FOREIGN KEY (`user`) REFERENCES `t_user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if NOT EXISTS  `t_video_collection_files` (
  `t_video_collection_id` bigint(20) NOT NULL,
  `files_fid` bigint(20) NOT NULL,
  PRIMARY KEY (`t_video_collection_id`,`files_fid`),
  UNIQUE KEY `UK_5frh92kx6gjk9tul07e3fa28r` (`files_fid`),
  CONSTRAINT `FK96lvpwdiis8xiufub0wottcmd` FOREIGN KEY (`t_video_collection_id`) REFERENCES `t_video_collection` (`id`),
  CONSTRAINT `FKpt2bg800xkohgtr9yd5bfk7fn` FOREIGN KEY (`files_fid`) REFERENCES `t_files` (`fid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
