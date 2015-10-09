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
		<?php include "results.php"; ?>
	</div>
</div>
</body>
</html>