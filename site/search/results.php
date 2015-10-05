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
		printf("Default database is %s.\n", $row[0]);
		$result->close();
	} else {
		printf("No Data");
	}
}

?>
---------------------------------------------------------------------------
<?php

	$button = $_GET [ 'submit' ];
	$search = $_GET [ 'search' ]; 
	
	$user = "web";
	$pass = "webSearch!";
	$database = "webSearchEngine";
	
	
	if( !$button )
		echo "you didn't submit a keyword";
    else {
			if( strlen( $search ) <= 1 )
				echo "you keyword is too short";
			else {
			     echo "You searched for <b> $search </b> <hr size='1' > </ br > ";
				 mysql_connect( "localhost","web","webSearch!") ; 
				 mysql_select_db("webSearchEngine");
				 
				 $search_exploded = explode ( " ", $search );
				 $x = 0; 
				 foreach( $search_exploded as $search_each ) {
						$x++;
					    $construct = "";
					    if( $x == 1 )
								$construct .="keywords LIKE '%$search_each%'";
						else		
								$construct .="AND keywords LIKE '%$search_each%'";
                    }
					
					$construct = " SELECT * FROM SEARCH_ENGINE WHERE $construct ";
					$run = mysql_query( $construct );
					
					$foundnum = mysql_num_rows($run);

					if ($foundnum == 0)
							echo "Sorry, there are no matching result for <b> $search </b>. </br> </br> 
							     
						else {		 
								echo "$foundnum results found !<p>";
							
							while( $runrows = mysql_fetch_assoc( $run ) ) { 
								$title = $runrows ['title']; 
								$desc = $runrows ['description']; 
								$url = $runrows ['url'];
								
								echo "<a href='$url'> <b> $title </b> </a> <br> $desc <br> <a href='$url'> $url </a> <p>";
								}
					}			
			}
	}
?>
