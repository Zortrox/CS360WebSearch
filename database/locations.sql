CREATE TABLE IF NOT EXISTS locations(
	webId int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name varchar(1000) NOT NULL,
	description varchar(10000) NOT NULL,
	url varchar(1500) NOT NULL,
	siteFullText varchar(15000) NOT NULL
	);