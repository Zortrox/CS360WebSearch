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

if ($querySplit[0] == "") {
	$endTime = microtime(true);
	$totalTime = round($endTime - $startTime, 3);
	echo "You must input a query. Time taken: $totalTime seconds.";
}
else if ($keysFound == 0) {
	$endTime = microtime(true);
	$totalTime = round($endTime - $startTime, 3);
	echo "Sorry, there are no matching result for <b>$query</b>. Time taken: $totalTime seconds.";
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

	//get all rows to ready for page-by-page nav
	$linkArray = array();
	while ($tempSite = $websiteRows->fetch_assoc()) {
		array_push($linkArray, $tempSite);
	}
	for ($i=0; $i<$sitesFound; $i++) {
		$title = $linkArray[$i]['name'];
		if ($title == "") $title = $linkArray[$i]['url'];
		$desc = $linkArray[$i]['description'];
		$url = $linkArray[$i]['url'];

		if (substr($url, 0, 4) != "http") {
			$url = "http://" . $url;
		}

		echo "<p><a href='$url'> <b> $title </b> </a> <br> $desc <br> <a href='$url'> $url </a></p>";
	}
}

/*
//Set up how many records in one page
　$pagesize=1;
　mysql_select_db("mydata",$conn);
　//get size of the records
　$rs=mysql_query("select count(*) from tb_product",$conn);
　$myrow = mysql_fetch_array($rs);
　$numrows=$myrow[0];
　//calculate how many pages to display those records

　$pages=intval($numrows/$pagesize);
　if ($numrows%$pagesize)
　　$pages++;
　//set up total pages
　if (isset($_GET['page'])){
　　$page=intval($_GET['page']);
　}
　else{
　　//set as the first page 
　　$page=1;
　}
　//calculate the offset of the records
　$offset=$pagesize*($page - 1);
　//read certain size of records
　$rs=mysql_query("select * from myTable order by id desc limit $offset,$pagesize",$conn);
　if ($myrow = mysql_fetch_array($rs))
　{
　　$i=0;
　　?>
　　<table border="0" width="80%">
　　<tr>
　　　<td width="50%" bgcolor="#E0E0E0">
　　　　<p align="center">Title</td>
　　　　<td width="50%" bgcolor="#E0E0E0">
　　　　<p align="center">Est Time</td>
　　</tr>
　　<?php
　　　do {
　　　　$i++;
　　　　?>
　　<tr>
　　　<td width="50%"><?=$myrow["news_title"]?></td>
　　　<td width="50%"><?=$myrow["news_cont"]?></td>
　　</tr>
　　　<?php>
　　　}
　　　while ($myrow = mysql_fetch_array($rs));
　　　　echo "</table>";
　　}
　　echo "<div align='center'>Total:".$pages."Pages(".$page."/".$pages.")";
　　for ($i=1;$i<$page;$i++)
　　　echo "<a href='fenye.php?page=".$i."'>[".$i ."]</a> ";
　　　echo "[".$page."]";
　　　for ($i=$page+1;$i<=$pages;$i++)
　　　　echo "<a href='fenye.php?page=".$i."'>[".$i ."]</a> ";
　　　　echo "</div>";
　　
　
	$first=1;
	$prev=$page-1;
	$next=$page+1;
	$last=$pages;

	if ($page > 1)
	{
　		echo "<a href='fenye.php?page=".$first."'>First Page</a> ";
　		echo "<a href='fenye.php?page=".$prev."'>Prev Page</a> ";
	}

	if ($page < $pages)
	{
　		echo "<a href='fenye.php?page=".$next."'>Next Page</a> ";
　		echo "<a href='fenye.php?page=".$last."'>End Page</a> ";
	}
*/
?>