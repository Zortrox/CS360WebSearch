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

$foundnum = $run->num_rows;

if ($foundnum == 0)
	echo "Sorry, there are no matching result for <b> $query </b>. </br> </br>";   
else {		 
	echo "<p>$foundnum result(s) found!</p>";

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

	//sort web array based on all key weights
	arsort($webArray);

	$websiteRowQuery = "SELECT * FROM locations ";
	$firstRow = true;
	foreach ($webArray as $webID => $wordWeight) {
		if ($firstRow == true) {
			$websiteRowQuery += "WHERE webId LIKE '$webID'";
			$firstRow = false;
		}
		else
			$websiteRowQuery += " OR WHERE webId LIKE '$webID'";
	}

	$websiteRows = $mysqli->query($websiteRowQuery);
	while ($website = $websiteRows->fetch_row()) {
		$runrows = $website->fetch_assoc();
		$title = $runrows ['name'];
		$desc = $runrows ['description'];
		$url = $runrows ['url'];
		
		echo "<p><a href='http\://$url'> <b> $title </b> </a> <br> $desc <br> <a href='http\://$url'> $url </a></p>";
	}
}

?>