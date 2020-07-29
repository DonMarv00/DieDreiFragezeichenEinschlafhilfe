<?php
header('Content-type: text/html; charset=utf-8');
include('simple_html_dom.php');
$int_counter = 0;
$counter_url = 1;
$string_data;
$text_file_data;



for($i = 0; $i <= 200; $i++){

	if($counter_url > 10){
		$string_data = file_get_contents('https://www.rocky-beach.com/hoerspiel/folgen/00' .$counter_url .'.html');
	}
	if($counter_url < 10 && $counter_url > 100){
		$string_data = file_get_contents('https://www.rocky-beach.com/hoerspiel/folgen/0' .$counter_url .'.html');
	}
	if($counter_url <= 100){
		$string_data = file_get_contents('https://www.rocky-beach.com/hoerspiel/folgen/' .$counter_url .'.html');
	}
	
	$hugenay = "Hugenay";
	if($string_data.toLowerCase().indexOf($hugenay.toLowerCase()) != -1 ){
		echo $string_data;
	}	
	
	
	
	$counter_url = $counter_url + 1;

}





?>