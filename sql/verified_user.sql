CREATE TABLE `verified_user` (
	`discord_uid` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci',
	`ubisoft_uid` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci',
	`ubisoft_uname` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci',
	`current_mmr` INT(10) NOT NULL,
	`current_kills` INT(10) NOT NULL,
	`current_wins` INT(10) NOT NULL,
	PRIMARY KEY (`discord_uid`) USING BTREE,
	UNIQUE INDEX `ubisoft_uid` (`ubisoft_uid`) USING BTREE,
	UNIQUE INDEX `ubisoft_uname` (`ubisoft_uname`) USING BTREE
) COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;
