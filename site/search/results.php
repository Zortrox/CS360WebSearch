<?php
//$query variable is the user's search

$user = "web";
$pass = "webSearch!"
$database = "webSearchEngine"

printf("start");

$mysqli = new mysqli("localhost", $user, $pass, $database);

/* check connection */
if (mysqli_connect_errno()) {
	printf("Connect failed: %s\n", mysqli_connect_error());
	exit();
}

$sql = "SELECT TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA=" . $database;

/* return name of current default database */
if ($result = $mysqli->query("SELECT * FROM keywords")) {
	if ($result->num_rows > 0) {
		$row = $result->fetch_row();
		printf("Default database is %s.\n", $row[0]);
		$result->close();
	} else {
		printf("No Data");
	}
}

printf("end");

?>