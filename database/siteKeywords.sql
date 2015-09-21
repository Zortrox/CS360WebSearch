CREATE TABLE IF NOT EXISTS siteKeywords(
	webId int NOT NULL,
	keyId int NOT NULL,
	weight int NOT NULL DEFAULT 0,
	PRIMARY KEY(webId, keyId),
	FOREIGN KEY(webId) REFERENCES locations(webId),
	FOREIGN KEY(keyId) REFERENCES keywords(keyID)
	);