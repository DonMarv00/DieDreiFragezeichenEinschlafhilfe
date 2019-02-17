<?php
//Copyright 2018 by DonMarv aka Marvin Stelter
//Header zur UTF-( Kodierung
header('Content-type: text/html; charset=utf-8');
//Datenbank verbindung
$db_host = 'localhost'; 
$db_name = '';
$db_user = ''; 
$db_password = '';
$pdo = new PDO("mysql:host=$db_host;dbname=$db_name", $db_user, $db_password);

//Folgen auswählen lassen
$app_daten = $_POST["data"];
$app_daten_b = $_GET["data"];
$app_daten_c = $_GET["bewerten"];
$app_daten_d = $_POST["bewerten"]; 


if($app_daten != ""){
		FolgenAuswählen($app_daten);
}
function string_folgen_data($string_nummer){
	
}

function FolgenAuswählen($app_daten){
	

	$db_host = 'localhost'; 
	$db_name = '';
	$db_user = ''; 
	$db_password = '';
	$pdo = new PDO("mysql:host=$db_host;dbname=$db_name", $db_user, $db_password);
	
	if($app_daten == "1"){
			$random_nummer = rand(1,50);
			$sql = "SELECT * FROM folgen WHERE nummer = $random_nummer";
			$folge = $pdo->query($sql)->fetch();
			echo $folge['name'] .":" .$folge['nummer'] .":" .$folge['likes'] .":" .$folge['dislikes'];
			Logfile($folge['name']);
	}
	if($app_daten == "2"){
	//Bis Hundert
	$random_nummer = rand(1,100);
			$sql = "SELECT * FROM folgen WHERE nummer = $random_nummer";
			$folge = $pdo->query($sql)->fetch();
			echo $folge['name'] .":" .$folge['nummer'] .":" .$folge['likes'] .":" .$folge['dislikes'];
			Logfile($folge['name']);
	}
	if($app_daten == "3"){
	//Bis Hundertfünfzig
	$random_nummer = rand(1,150);
			$sql = "SELECT * FROM folgen WHERE nummer = $random_nummer";
			$folge = $pdo->query($sql)->fetch();
			echo $folge['name'] .":" .$folge['nummer'] .":" .$folge['likes'] .":" .$folge['dislikes'];
			Logfile($folge['name']);
	}
	if($app_daten == "4"){
	//Alle Folgen
	$random_nummer = rand(1,196);
			$sql = "SELECT * FROM folgen WHERE nummer = $random_nummer";
			$folge = $pdo->query($sql)->fetch();
			echo $folge['name'] .":" .$folge['nummer'] .":" .$folge['likes'] .":" .$folge['dislikes'];
			Logfile($folge['name']);
	}
	if($app_daten == "5"){
	//Nur "Die Dr3i" Folgen
	$random_nummer = rand(1,8);
	
			$sql = "SELECT * FROM folgen_dd WHERE nummer = $random_nummer";
			$folge = $pdo->query($sql)->fetch();
			echo $folge['name'] .":" .$folge['nummer'] .":" .$folge['likes'] .":" .$folge['dislikes'];
			Logfile($folge['name']);
	}
	if($app_daten == "6"){
	//Folgen von 100 bis 150
	$random_nummer = rand(150,196);
			$sql = "SELECT * FROM folgen WHERE nummer = $random_nummer";
			$folge = $pdo->query($sql)->fetch();
			echo $folge['name'] .":" .$folge['nummer'] .":" .$folge['likes'] .":" .$folge['dislikes'];
			Logfile($folge['name']);
	}
	
}
function Logfile($folgennummer){
	$timestamp = time();
	$datum = date("d.m.Y - H:i", $timestamp);
	
	$string_log = "log.txt";
	if(!file_exists($string_log)){
		file_put_contents($string_log, "");
	}
	$string_old_content = file_get_contents("log.txt");
	
	file_put_contents($string_log, "Folgendaten: (Folgentitel, Nummer und Bewertung): " ."\r\n" .$folgennummer ." Datum:".$datum ."\r\n"  .$string_old_content);
}
?>
