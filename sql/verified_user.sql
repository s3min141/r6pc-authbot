CREATE TABLE `verified_user` (
	`discord_uid` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci',
	`ubisoft_uid` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci',
	`ubisoft_uname` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci',
	`current_mmr` INT(10) NOT NULL
) COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;