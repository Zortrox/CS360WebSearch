<?php
//$query variable is the user's search

//timing setup
$startTime = microtime(true);
$endTime = null;

//setup user and connect
$user = "web";
$pass = "webSearch!";
$database = "webSearchEngine";
$mysqli = new mysqli("localhost", $user, $pass, $database);

/* check connection */
if (mysqli_connect_errno()) {
	printf("Connect failed: %s\n", mysqli_connect_error());
	exit();
}

//create array of strings in query **IN PROGRESS**
$moreStrings = false;
$queryPart = $query;
while ($moreStrings) {
	$stringPos1 = strpos($queryPart, "\"");
	$queryPart = substr($queryPart, 0);
}

function createConstruct($wordArray, $column, $useKey = false) {
	$construct = "";
	$firstRow = true;

	foreach( $wordArray as $wordKey => $wordValue ) {
		if ($firstRow == true) {
			if ($useKey) 
				$construct .= "$column LIKE '$wordKey'";
			else 
				$construct .= "$column LIKE '$wordValue'";
			$firstRow = false;
		else
			if ($useKey) 
				$construct .= " OR $column LIKE '$wordKey'";
			else 
				$construct .= " OR $column LIKE '$wordValue'";
	}

	return $construct;
}

$queryExploded = explode(" ", $query );
$construct = "SELECT * FROM keywords WHERE " . createConstruct($queryExploded, "word");
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

	//sort web array based on all key weights
	arsort($webArray);

	$websiteRowQuery = "SELECT * FROM locations WHERE " . createConstruct($webArray, "webID", true);
	
	foreach ($webArray as $webID => $wordWeight) {
		if ($firstRow == true) {
			$websiteRowQuery .= "webId LIKE '$webID'";
			$firstRow = false;
		}
		else
			$websiteRowQuery .= " OR webId LIKE '$webID'";
	}

	//display number of results found
	$sitesFound = count($webArray);
	$plural = $sitesFound > 1 ? "s" : "";
	echo "<p>$sitesFound result$plural found in ";

	$endTime = microtime(true);
	$totalTime = round($endTime - $startTime, 3);
	echo "$totalTime seconds</p>";

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