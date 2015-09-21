CREATE TABLE IF NOT EXISTS locations(
	webId int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name varchar(15) NOT NULL,
	description varchar(300) NOT NULL,
	url varchar(1500) NOT NULL,
	hash varchar(300) NOT NULL
	);