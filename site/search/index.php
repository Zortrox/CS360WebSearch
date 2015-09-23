<?php
	$query = htmlspecialchars_decode($_GET["q"]);
?>

<!DOCTYPE html>
<html>
<head>
	<title>Super Awesome Web Search - Results</title>
	<link rel="stylesheet" type="text/css" href="../style.css">
</head>
<body>
<div id="wrapper">
	<div id="results-top-bar">
		<form id="form-results">
			<input id="input-query" type="text" name="q" value="<?php echo $query ?>">
			<input id="input-search" type="submit" value="Search">
		</form>
		<a id="home-button" href="/"><div >Home</div></a>
	</div>
	<div id="results-list">
		<p>WOW, LOOK AT ALL THE RESULTS!</p>
		<?php 
			$mysqli = new mysqli("localhost", "web", "webSearch!", "webSearchEngine");

			/* check connection */
			if (mysqli_connect_errno()) {
				printf("Connect failed: %s\n", mysqli_connect_error());
				exit();
			}

			/* return name of current default database */
			if ($result = $mysqli->query("SELECT * FROM keywords")) {
				if ($result->num_rows > 0) {
					$row = $result->fetch_row();
					printf("Default database is %s.\n", $row[0]);
					$result->close();
				} else {
					printf("No Data");
				}
				
			}
		?>
	</div>
</div>
</body>
</html>