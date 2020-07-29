<?php
header('Content-type: text/html; charset=utf-8');
include("database.php");
$nummer = $_GET['nummer'];
$old;
$new;
$json;
	
for($i=1; $i <= 200; $i++) {
   
$db_host = 'localhost'; 
	$db_name = 'd02c5381';
	$db_user = 'd02c5381'; 
	$db_password = 'X8cbzSZxM22Zd6wE';
	$pdo = new PDO("mysql:host=$db_host;dbname=$db_name", $db_user, $db_password);
	$sql = "SELECT * FROM folgen WHERE nummer = $i";
	$folge = $pdo->query($sql)->fetch();
	
	$string_beschreibung = utf8_encode($folge['beschreibung']);
	$string_name = $folge['name'];
	
	$old = $new;
	$new = $old ."{\"name\":\"" .$string_name ."\"," .'\n' ."\"beschreibung\":\"" .$string_beschreibung ."\"}, "  .'\n';
	
	
}

$json = "{\"user\":[" .$new ."]}";



file_put_contents("folgen.json", $json);
?>