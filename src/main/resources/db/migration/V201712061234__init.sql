# # CREATE TABLE `users` (
# #   `id` INT NOT NULL,
# #   `username` varchar(45) NOT NULL,
# #   `password` varchar(45) NOT NULL,
# #   `enabled` tinyint(4) NOT NULL DEFAULT '1',
# #   PRIMARY KEY (`id`)
# # ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
# #
# #
# #
# #
# # CREATE TABLE `user_roles` (
# #   `user_role_id` int(11) NOT NULL AUTO_INCREMENT,
# #   `id` int(11) NOT NULL AUTO_INCREMENT,
# #   `username` varchar(45) NOT NULL,
# #   `role` varchar(45) NOT NULL,
# #   PRIMARY KEY (`user_role_id`),
# #   UNIQUE KEY `uni_username_role` (`role`,`id`),
# #   KEY `fk_username_idx` (`id`),
# #   CONSTRAINT `fk_username` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
# # ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
# #
#
# #
# # CREATE TABLE IF NOT EXISTS `users_security` (
# #   `userId` LONG NOT NULL,
# #   `username` VARCHAR(45) NULL,
# #   `password` VARCHAR(45) NULL,
# #   `enabled` TINYINT NULL,
# #   PRIMARY KEY (`userId`))
# #   ENGINE = InnoDB;
#
# DROP TABLE IF EXISTS user_security;
# create table user_security(
#   userId Int not null PRIMARY KEY ,
#   username VARCHAR(50) not null,
#   password VARCHAR(50) not null);
# # create unique index ix_secu_username on user_security (username);
#
# # DROP TABLE IF EXISTS authorities;
# # create table authorities (
# #   user_role_id int(11) NOT NULL AUTO_INCREMENT primary key,
# #   userId BIGINT not null,
# #   username VARCHAR(50) not null,
# #   authority VARCHAR(50) not null,
# #   constraint fk_authorities_users foreign key(username) references user_security(username));
# #
# # create unique index ix_auth_username on authorities (username,authority);