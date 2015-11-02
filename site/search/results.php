<?php

//timing setup
$startTime = microtime(true);
$endTime = null;

//setup user and connect to the database
$user = "web";
$pass = "webSearch!";
$database = "webSearchEngine";
$mysqli = new mysqli("localhost", $user, $pass, $database);

//check connection
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

//creates a partial query that finds values in columns
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
		} else {
			if ($useKey) 
				$construct .= " OR $column LIKE '$wordKey'";
			else 
				$construct .= " OR $column LIKE '$wordValue'";
		}
	}

	return $construct;
}

//remove all string searches and put them into an array
$stringSearch = array();
preg_match_all("/([\"'])(?:(?=(\\\?))\\2.)*\\1/", $query, $stringSearch);
$query = preg_replace("/([\"'])(?:(?=(\\\?))\\2.)*?\\1/", "", $query);

//create array based on user-inputted words
//get all keyword rows from database based on user-inputted words
//$query variable is the user's search
$querySplit = preg_split('/\s+/', trim($query));
$keywordQuery = "SELECT * FROM keywords WHERE " . createConstruct($querySplit, "word");
$keywordRows = $mysqli->query($keywordQuery);
$keysFound = $keywordRows->num_rows;

if ($querySplit[0] == "")
	echo "You must input a query.";
else if ($keysFound == 0)
	echo "Sorry, there are no matching result for <b> $query </b>.";
else {
	//get all keyIds of the user-inputted keywords
	$keyArray = array();
	while ($resultsRow = $keywordRows->fetch_row()) {
		array_push($keyArray, $resultsRow[0]);
	}

	//gather all webIds of websites based on keywords found
	//sort in descending order based on cumulative word weights
	$webArray = array();
	$webIDQuery = "SELECT webId FROM siteKeywords WHERE " . createConstruct($keyArray, "keyId");
	$webIDResults = $mysqli->query($webIDQuery);
	while ($siteKeywordsRow = $webIDResults->fetch_row()) {
		$webID = $siteKeywordsRow[0];
		$wordWeight = $siteKeywordsRow[2];
		if (in_array($webArray, $webID)) {
			$webArray[$webID] += $wordWeight;
		} else {
			$webArray[$webID] = $wordWeight;
		}
	}
	arsort($webArray);

	//get all location data based on webIds found
	$websiteRowQuery = "SELECT * FROM locations WHERE " . createConstruct($webArray, "webId", true);
	$websiteRows = $mysqli->query($websiteRowQuery);

	//display number of results found in how much time
	$sitesFound = count($webArray);
	$plural = $sitesFound > 1 ? "s" : "";
	echo "<p>$sitesFound result$plural found in ";
	$endTime = microtime(true);
	$totalTime = round($endTime - $startTime, 3);
	echo "$totalTime seconds</p>";

	while ($website = $websiteRows->fetch_assoc()) {
		$title = $website['name'];
		if ($title == "") $title = $website['url'];
		$desc = $website['description'];
		$url = $website['url'];

		if (substr($url, 0, 4) != "http") {
			$url = "http://" . $url;
		}

		echo "<p><a href='$url'> <b> $title </b> </a> <br> $desc <br> <a href='$url'> $url </a></p>";
	}
}

?>