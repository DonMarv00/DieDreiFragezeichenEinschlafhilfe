<?php
header('Content-type: text/html; charset=utf-8');
//Copyright 2019 Marvin Stelter
$string_data = $_GET['key'];

if($string_data == "1"){
	$min = 1;
	$max = 50;
	select_episode($min, $max);
}
if($string_data == "2"){
	$min = 1;
	$max = 100;
	select_episode($min, $max);
}
if($string_data == "3"){
	$min = 1;
	$max = 196;
	select_episode($min, $max);
}
if($string_data == "4"){
	$min = "dd";
	$max = 0;
	select_episode($min, $max);
}
if($string_data == "5"){
	$min = 1;
	$max = 150;
	select_episode($min, $max);
}
if($string_data == "6"){
	$min = 150;
	$max = 196;
	select_episode($min, $max);
}
function select_episode($min, $max){
	if($min != "dd"){
		
			$db_host = 'localhost'; 
			$db_name = '';
			$db_user = ''; 
			$db_password = '';
			$pdo = new PDO("mysql:host=$db_host;dbname=$db_name", $db_user, $db_password);
	
			$random_nummer = rand($min,$max);
			$sql = "SELECT * FROM folgen WHERE nummer = $random_nummer";
			$folge = $pdo->query($sql)->fetch();
			echo $folge['name'] .":" .$folge['nummer'];
			daten_log($folge['name']);
	}else{
			$db_host = 'localhost'; 
			$db_name = '';
			$db_user = ''; 
			$db_password = '';
			$pdo = new PDO("mysql:host=$db_host;dbname=$db_name", $db_user, $db_password);
		
			$random_nummer = rand(1,8);
			$sql = "SELECT * FROM folgen_dd WHERE nummer = $random_nummer";
			$folge = $pdo->query($sql)->fetch();
			
			if($folge['nummer'] == "6"){
				echo "Die dr3i - Tödliche Regie:6";
			}else if($folge['nummer'] == "5"){
				echo "Die dr3i - Das Haus der 1.000 Rätsel:5";
			}else{
				echo $folge['name'] .":" .$folge['nummer'];
			}
			
		
			daten_log($folge['name']);
		
		
		
	}
	
}

function daten_log($daten){
	$timestamp = time();
	$datum = date("d.m.Y - H:i", $timestamp);
	
	
	$counter = file_get_contents("counter.txt");
	$counter++; 
	file_put_contents("counter.txt", $counter); 
	
	$file_log = "log.txt";
	
	$string_old_content = file_get_contents("log.txt");

	file_put_contents("log.txt", "Folgendaten: (Folgentitel, Nummer und Bewertung): <br>" ."\r\n" .$daten ." Datum:".$datum ."\r\n"  .$string_old_content);
	
	
	
	
}

?>