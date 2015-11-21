CREATE TABLE schema_version (
	`key` varchar(256) NOT NULL,
    `script_name` varchar(256),
    `when` timestamp NOT NULL default CURRENT_TIMESTAMP,
    primary key (`key`)
) ENGINE=InnoDB;


INSERT INTO schema_version(`key`, `script_name`) VALUES ('001', 'schema-init.sql');

CREATE TABLE centro_query (
	`id` int NOT NULL AUTO_INCREMENT,
	`starting_points` varchar(256) NOT NULL,
	`modes` varchar(256) NOT NULL,
	`meeting_type` varchar(35) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB;