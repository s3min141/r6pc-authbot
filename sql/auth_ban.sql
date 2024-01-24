CREATE TABLE `auth_ban` (
	`discord_uid` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci',
	`start_date` DATETIME NOT NULL,
	`end_date` DATETIME NOT NULL,
	`ban_reason` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci',
	PRIMARY KEY (`discord_uid`) USING BTREE
) COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;