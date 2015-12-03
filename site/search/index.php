<?php
	$query = "";
	if (isset($_GET["q"])) $query = htmlspecialchars_decode($_GET["q"]);
?>

<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<title>WKU Search - Results</title>
	<link rel="icon" type="image/ico" href="../favicon.ico">
	<link rel="shortcut icon" href="../favicon.ico" type="image/x-icon">
	<link rel="stylesheet" type="text/css" href="../style.css">
</head>
<body>
<div id="wrapper">
	<div id="results-top-bar">
		<form id="form-results">
			<input id="input-query" type="text" name="q" value="<?php echo htmlspecialchars($query); ?>">
			<input id="input-search" type="submit" value="Search">
		</form>
		<a id="home-button" href="/"><div>Home</div></a>
	</div>
	<div id="results-list">
		<?php include "results.php"; ?>
	</div>
</div>
</body>
</html>