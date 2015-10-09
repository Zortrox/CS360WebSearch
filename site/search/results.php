<?php
//$query variable is the user's search

$user = "web";
$pass = "webSearch!";
$database = "webSearchEngine";

$mysqli = new mysqli("localhost", $user, $pass, $database);

/* check connection */
if (mysqli_connect_errno()) {
	printf("Connect failed: %s\n", mysqli_connect_error());
	exit();
}

/*
$sql = "SELECT * FROM keywords";
//return name of current default database
if ($result = $mysqli->query($sql)) {
	if ($result->num_rows > 0) {
		$row = $result->fetch_row();
		printf("Default keyword is %s. <br>", $row[1]);
		$result->close();
	} else {
		printf("No Data");
	}
}
*/

//mysql_select_db($database);

$query_exploded = explode(" ", $query );
$x = 0;
$construct = "";
foreach( $query_exploded as $query_each ) {
	$x++;
	if( $x == 1 )
			$construct .= "word LIKE '$query_each'";
	else		
			$construct .= " OR word LIKE '$query_each'";
}
	
$construct = "SELECT * FROM keywords WHERE $construct";
$run = $mysqli->query($construct);

$foundnum = $run->num_rows;

if ($foundnum == 0)
	echo "Sorry, there are no matching result for <b> $query </b>. </br> </br>";   
else {		 
	echo "<p>$foundnum result(s) found!</p>";

	while ($resultsRow = $run->fetch_row()) {
		$resultID = $resultsRow[0];
		$webIDQuery = "SELECT webId FROM siteKeywords WHERE keyId LIKE '$resultID'";
		$webIDResults = $mysqli->query($webIDQuery);

		$webID = $webIDResults->fetch_row()[0];
		$website = $mysqli->query("SELECT * FROM locations WHERE webId LIKE '$webID'");

		$runrows = $website->fetch_assoc();
		$title = $runrows ['name'];
		$desc = $runrows ['description'];
		$url = $runrows ['url'];
		
		echo "<p><a href='http://$url'> <b> $title </b> </a> <br> $desc <br> <a href='http://$url'> $url </a></p>";
	}
}

?>