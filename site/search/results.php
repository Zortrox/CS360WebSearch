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

$sql = "SELECT word FROM keywords";

/* return name of current default database */
if ($result = $mysqli->query($sql)) {
	if ($result->num_rows > 0) {
		$row = $result->fetch_row();
		printf("Default keyword is %s. <br>", $row[0]);
		$result->close();
	} else {
		printf("No Data");
	}
}

//mysql_select_db($database);

$query_exploded = explode(" ", $query );
$x = 0;
$construct = "";
foreach( $query_exploded as $query_each ) {
	$x++;
	if( $x == 1 )
			$construct .= "word LIKE $query_each";
	else		
			$construct .= " AND word LIKE $query_each";
}
	
$construct = "SELECT * FROM keywords WHERE $construct";
$run = mysql_query( $construct );

$foundnum = mysql_num_rows($run);

if ($foundnum == 0)
	echo "Sorry, there are no matching result for <b> $query </b>. </br> </br>";   
else {		 
	echo "$foundnum results found !<p>";
	
	while( $runrows = mysql_fetch_assoc( $run ) ) {
		$title = $runrows ['title']; 
		$desc = $runrows ['description']; 
		$url = $runrows ['url'];
		
		echo "<a href='$url'> <b> $title </b> </a> <br> $desc <br> <a href='$url'> $url </a> <p>";
	}
}

?>