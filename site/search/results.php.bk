<?php

error_reporting(E_ERROR | E_PARSE);

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

function orderArray($wordArray){
	$construct = "";
	
	foreach( $wordArray as $wordKey => $wordValue ) {
		$construct .= $wordKey . ",";
	}
	
	$construct = trim($construct, ",");
	
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

if ($querySplit[0] == "") { //if no query tell user 
	$endTime = microtime(true);
	$totalTime = round($endTime - $startTime, 3);
	echo "You must input a query. Time taken: $totalTime seconds."; 
}
	
else if ($keysFound == 0) { //if query returns no results, inform user 
	$endTime = microtime(true);
	$totalTime = round($endTime - $startTime, 3);
	echo "Sorry, there are no matching result for <b> $query </b>. Time taken: $totalTime seconds.";
}
else {
	//get all keyIds of the user-inputted keywords
	$keyArray = array();
	while ($resultsRow = $keywordRows->fetch_row()) {
		array_push($keyArray, $resultsRow[0]);
	}

	//gather all webIds of websites based on keywords found
	//sort in descending order based on cumulative word weights
	$webArray = array();
	$webIDQuery = "SELECT * FROM siteKeywords WHERE " . createConstruct($keyArray, "keyId");
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
	//$websiteRowQuery = "SELECT * FROM locations WHERE " . createConstruct($webArray, "webId", true);
	$websiteRowQuery = "SELECT * FROM locations WHERE webID IN (" . orderArray($webArray) . ") ORDER BY FIELD (webID," . orderArray($webArray) . ")";
	$websiteRows = $mysqli->query($websiteRowQuery);

	//display number of results found in how much time
	$sitesFound = count($webArray);
	$plural = $sitesFound > 1 ? "s" : "";
	echo "<p>$sitesFound result$plural found in ";
	$endTime = microtime(true);
	$totalTime = round($endTime - $startTime, 3);
	echo "$totalTime seconds</p>";

	//Set up how many records in one page
	$pagesize = 10;

	//calculate how many pages to display those records
	$pages = intval($sitesFound/$pagesize);
	if ($sitesFound%$pagesize) $pages++;

	//get current page
	if (isset($_GET['p'])){
		$page=intval($_GET['p']);
	}
	else{
		//set as the first page 
		$page = 1;
	}
	//adds bounds to pages
	if ($page < 1) $page = 1;
	else if ($page > $pages) $page = $pages;

	//calculate the offset of the records
	$offset = $pagesize*($page - 1);

	//get all rows to ready for page-by-page nav
	$linkArray = array();
	while ($tempSite = $websiteRows->fetch_assoc()) {
		array_push($linkArray, $tempSite);
	}
	for ($i=$offset; $i<$sitesFound; $i++) {
		$title = $linkArray[$i]['name'];
		if ($title == "") $title = $linkArray[$i]['url'];
		$desc = $linkArray[$i]['description'];
		$url = $linkArray[$i]['url'];

		if (substr($url, 0, 4) != "http") {
			$url = "http://" . $url;
		}

		echo "<p><a href='$url' class='title' style='font-family:ariel,sans-serif;'> <b> $title </b> </a> <br> $desc <br> <a href='$url' style='text-decoration:none;'> $url </a></p>";

		//only go for 10 pages
		if ($i-$offset == 9) break;
	}

	//write pages at bottom
	$first = 1;
	$prev = $page-1;
	$next = $page+1;
	$last = $pages;
	echo "<div id='page-change'>";
	for ($i=1; $i<=$pages; $i++) {
		if ($i==$page) {
			echo "$page ";
		} else {
			echo "<a href='?q=$query&p=$i'>$i</a> ";
		}
	}
	echo "<br>";
	if ($page > 1)
	{
		echo "<a href='?q=$query&p=$first'>First</a> ";
		echo "<a href='?q=$query&p=$prev'>Prev</a> ";
	}
	if ($page < $pages)
	{
		echo "<a href='?q=$query&p=$next'>Next</a> ";
		echo "<a href='?q=$query&p=$last'>End</a> ";
	}
	echo "</div>";
}

?>