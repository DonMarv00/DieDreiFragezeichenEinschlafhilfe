<?php
loadIntoDatabase();
function loadIntoDatabase(){
include('simple_html_dom.php');
$pdo = new PDO('mysql:host=localhost;dbname=d032c2aa', 'd032c2aa', 'SZpRccd3RwWmd9NJ');


$int_counter = file_get_contents("files/counter.txt");
$int_counter_new_ids = file_get_contents("files/new_ids.txt.txt");
$string_base_url = "https://www.projektoren-datenbank.com/pro/ans.php?id=";
$string_name;
$string_system = "Test";
$string_helligkeit = "Test";
$string_anwendung = "Test";
$string_lampenzeit = "Test";




$link_to_data = $string_base_url .$int_counter;
$html = file_get_html($link_to_data);

foreach($html->find('span.retailer_name') as $e){
		$string_name = $e->innertext;
}
$e = $html->find("TD.eneu");
$string_system = $e[1]->innertext;

$e = $html->find("TD.eneusmall");
$string_helligkeit = $e[0]->innertext;

$e = $html->find("TD.eneu");
$string_anwendung = $e[3]->innertext;

$e = $html->find("TD.eneu");
$string_lampenzeit = $e[5]->innertext;

$statement = $pdo->prepare("INSERT INTO daten (name, system, helligkeit,anwendung, id, lampenzeit) VALUES (?, ?, ?,?,?, ?)");
$statement->execute(array($string_name, $string_system, $string_helligkeit, $string_anwendung, $int_counter_new_ids, $string_lampenzeit));  

file_put_contents("files/counter.txt", $int_counter + 1);

file_put_contents("files/new_ids.txt.txt", $int_counter_new_ids + 1);
}
?>
