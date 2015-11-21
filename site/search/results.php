<?php

error_reporting(E_ERROR | E_PARSE);
$doDebug = false;
if (isset($_GET["debug"])) $doDebug = ($_GET["debug"] === "true");

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

function echoDebug($info) {
	global $doDebug;
	if ($doDebug) echo $info . "<br>";
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

//determine wildcards and properly escape
$queryWildcard = str_replace("%", "\%", $query);
$queryWildcard = str_replace("*", "%", $queryWildcard);

//get string searches and put them into an array
//this array contains 2 string arrays:
//		[0]->"sample" (with escaped quotes)
//		[1]->sample (without quotes)
$stringSearch = array();
preg_match_all("/\"([^\"]*)\"/", $queryWildcard, $stringSearch);

//get array of webIds that could contain the string
$stringIds = array();
$stringIndex = 0;
foreach ($stringSearch[1] as $search) {
	$words = preg_split('/\s+/', trim($search));
	$keyIdQuery = "SELECT keyId FROM keywords WHERE word LIKE \"" . $words[0] . "\"";
	$keyIdRows = $mysqli->query($keyIdQuery);

	if ($keyIdRows->num_rows > 0) {
		$keyResult = $keyIdRows->fetch_row();
		$webIdQuery = "SELECT * FROM siteKeywords WHERE keyId LIKE " . $keyResult[0];
		$webIdRows = $mysqli->query($webIdQuery);

		$thisWordArray = array();
		while ($webId = $webIdRows->fetch_row()) {
			array_push($thisWordArray, $webId[0]);
		}
		$stringIds[$stringIndex] = $thisWordArray;
	}

	$stringIndex++;
}

if ($doDebug) {
	print_r($stringSearch);
	echo "<br>";
	print_r($stringIds);
	echo "<br>";
}

//create array based on user-inputted words
//get all keyword rows from database based on user-inputted words
$queryBase = preg_replace("/\"([^\"]*)\"/", "", $queryWildcard);
echoDebug($queryBase);
$querySplit = preg_split('/\s+/', trim($queryBase));
$keywordQuery = "SELECT * FROM keywords WHERE " . createConstruct($querySplit, "word");
echoDebug($keywordQuery);
$keywordRows = $mysqli->query($keywordQuery);

//determine if keys were found in either;
$keysFound = $keywordRows->num_rows;
echoDebug($keysFound . " individual keywords found.");

if ($querySplit[0] == "" && count($stringSearch[1]) == 0) { //if no query tell user
	$endTime = microtime(true);
	$totalTime = round($endTime - $startTime, 3);
	echo "You must input a query. Time taken: $totalTime seconds.";
}
else if ($keysFound == 0 && count($stringIds) == 0) { //if query returns no results, inform user 
	$endTime = microtime(true);
	$totalTime = round($endTime - $startTime, 3);
	echo "Sorry, there are no matching result for <b> $query </b>. Time taken: $totalTime seconds.";
}
else {
	//sorted array based on keyword/string weights
	$webArray = array();

	//if there were words found
	if ($keysFound != 0 ) {
		//get all keyIds of the user-inputted keywords
		$keyArray = array();
		while ($resultsRow = $keywordRows->fetch_row()) {
			array_push($keyArray, $resultsRow[0]);
		}

		//gather all webIds of websites based on keywords found
		//sort in descending order based on cumulative word weights
		$webIdQuery = "SELECT * FROM siteKeywords WHERE " . createConstruct($keyArray, "keyId");
		$webIdResults = $mysqli->query($webIdQuery);
		while ($siteKeywordsRow = $webIdResults->fetch_row()) {
			$webId = $siteKeywordsRow[0];
			$wordWeight = $siteKeywordsRow[2];
			if (in_array($webArray, $webId)) {
				$webArray[$webId] += $wordWeight;
			} else {
				$webArray[$webId] = $wordWeight;
			}
		}
	}
	if (count($stringIds) != 0) {
		//default string word weight
		$stringWordWeight = 50;

		foreach ($stringIds as $stringNum => $webIdArray) {
			//search fullText of site for string[stringNum]
			$fullTextQuery = "SELECT webId, siteFullText FROM locations WHERE " . createConstruct($webIdArray, "webId");
			echoDebug($fullTextQuery);
			$fullTextRows = $mysqli->query($fullTextQuery);
			echoDebug($fullTextRows->num_rows . " results.");
			//convert back to regular percent sign
			$stringSearchOrigin = array();
			preg_match_all("/\"([^\"]*)\"/", $query, $stringSearchOrigin);
			while ($fullText = $fullTextRows->fetch_row()) {
				//if string is found, add each word to weight * additional string weight (constant)
				if (stripos($fullText[1], $stringSearchOrigin[1][$stringNum]) != 0) {
					$fullSplit = preg_split('/\s+/', trim($stringSearch[1][$stringNum]));
					$fullSplitQuery = "SELECT keyId FROM keywords WHERE " . createConstruct($fullSplit, "word");
					echoDebug($fullSplitQuery);
					$fullSplitRows = $mysqli->query($fullSplitQuery);
					echoDebug($fullSplitRows->num_rows . " results.");
					if ($fullSplitRows->num_rows != 0) {
						$fullKeyArray = array();
						while ($fullRow = $fullSplitRows->fetch_row()) {
							array_push($fullKeyArray, $fullRow[0]);
						}
						$webIdQuery = "SELECT * FROM siteKeywords WHERE (" . createConstruct($fullKeyArray, "keyId") . ") AND webId LIKE " . $fullText[0];
						echoDebug($webIdQuery);
						$webIDResults = $mysqli->query($webIdQuery);
						echoDebug($webIDResults->num_rows . " results.");
						while ($siteKeywordsRow = $webIDResults->fetch_row()) {
							echoDebug("Word weight " . $siteKeywordsRow[2] . " added.");
							$webId = $fullText[0];
							$wordWeight = $siteKeywordsRow[2];
							if (in_array($webArray, $fullText[0])) {
								$webArray[$webId] += $wordWeight * $stringWordWeight;
							} else {
								$webArray[$webId] = $wordWeight * $stringWordWeight;
							}
						}
					}
				}
			}
		}
	}

	//determine if any sites were found/how many
	$sitesFound = count($webArray);

	if ($sitesFound > 0) {
		//sort websites based on weight
		arsort($webArray);

		//get all location data based on webIds found
		//$websiteRowQuery = "SELECT * FROM locations WHERE " . createConstruct($webArray, "webId", true);
		$websiteRowQuery = "SELECT * FROM locations WHERE webId IN (" . orderArray($webArray) . ") ORDER BY FIELD (webId," . orderArray($webArray) . ")";
		$websiteRows = $mysqli->query($websiteRowQuery);

		//display number of results found in how much time
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
	} else {
		//if no sites found (occurs if no full text strings found)
		$endTime = microtime(true);
		$totalTime = round($endTime - $startTime, 3);
		echo "Sorry, there are no matching result for <b> $query </b>. Time taken: $totalTime seconds.";
	}
}

?>