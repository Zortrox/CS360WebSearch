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

//create array of strings in query **IN PROGRESS**
$moreStrings = false;
$queryPart = $query;
while ($moreStrings) {
	$stringPos1 = strpos($queryPart, "\"");
	$queryPart = substr($queryPart, 0);
}

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

$keysFound = $run->num_rows;

if ($keysFound == 0)
	echo "Sorry, there are no matching result for <b> $query </b>. </br> </br>";
else {
	$webArray = array();
	while ($resultsRow = $run->fetch_row()) {
		$resultID = $resultsRow[0];
		$webIDQuery = "SELECT webId FROM siteKeywords WHERE keyId LIKE '$resultID'";
		$webIDResults = $mysqli->query($webIDQuery);

		$siteKeywordsRow = $webIDResults->fetch_row();
		$webID = $siteKeywordsRow[0];
		$wordWeight = $siteKeywordsRow[2];
		if (in_array($webArray, $webID)) {
			$webArray[$webID] += $wordWeight;
		} else {
			$webArray[$webID] = $wordWeight;
		}
	}

	$sitesFound = count($webArray);
	$plural = $sitesFound > 1 ? "s" : "";
	echo "<p>$sitesFound result$plural found!</p>";

	//sort web array based on all key weights
	arsort($webArray);

	$websiteRowQuery = "SELECT * FROM locations WHERE ";
	$firstRow = true;
	foreach ($webArray as $webID => $wordWeight) {
		if ($firstRow == true) {
			$websiteRowQuery .= "webId LIKE '$webID'";
			$firstRow = false;
		}
		else
			$websiteRowQuery .= " OR webId LIKE '$webID'";
	}

	$websiteRows = $mysqli->query($websiteRowQuery);
	while ($website = $websiteRows->fetch_assoc()) { 
		$title = $website['name'];
		$desc = $website['description'];
		$url = $website['url'];

		if (substr($url, 0, 4) != "http") {
			$url = "http://" . $url;
		}

		echo "<p><a href='$url'> <b> $title </b> </a> <br> $desc <br> <a href='$url'> $url </a></p>";
	}
}

?>