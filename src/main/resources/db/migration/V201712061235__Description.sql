# # CREATE TABLE `persistent_logins` (
# #   `username` varchar(64) NOT NULL,
# #   `series` varchar(64) NOT NULL,
# #   `token` varchar(64) NOT NULL,
# #   `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
# #   PRIMARY KEY (`series`)
# # ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
#
#
# create table ApplicationUser(
#   userId BIGINT not null primary key,
#   username VARCHAR(50) not null,
#   password VARCHAR(50) not null);