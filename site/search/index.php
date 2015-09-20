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
	<form id="form-results">
		<input id="input-query" type="text" name="q" value="<?php echo $query ?>">
		<input id="input-search" type="submit" value="Search">
	</form>
	<div id="results-list">
		<p>WOW, LOOK AT ALL THE RESULTS!</p>
	</div>
</div>
</body>
</html>