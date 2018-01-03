#!/usr/bin/php
<?php

function printresults() {
	global $cose;
	foreach($cose as $inst => $best) {
		echo "Record istanza $inst: $best".PHP_EOL;
	}
	exit();
}

if (strtoupper(substr(PHP_OS, 0, 3)) === 'WIN') {
	echo 'WINDOWS!'.PHP_EOL;
	$windows = true;
} else {
	echo 'LINUX!'.PHP_EOL;
	$windows = false;
	function sig_handler($signo) {
		if($signo === SIGINT) {
			printresults();
		}
	}
	pcntl_signal(SIGINT, "sig_handler");
	declare(ticks=1);
}

$cose = [
		1 => 9999.9,
		2 => 9999.9,
		3 => 9999.9,
		4 => 9999.9,
		5 => 9999.9,
		6 => 9999.9,
		7 => 9999.9
		];
$l = strlen("Final solution: ");


while(true) {
	foreach($cose as $inst => &$best) {
		exec("java -jar ../JavaSA/target/ETPsolver_OMAMZ_group09.jar instance0$inst -t 30", $output);
		//exec("java -jar ETPsolver_OMAMZ_group09.jar instance0$inst -t 30", $output);

		if($windows && file_exists('stop.txt')) {
			echo PHP_EOL.'File di stop trovato :O'.PHP_EOL;
			printresults();
		}

		$last = array_pop($output);
		if(substr($last, 0, $l)) {
			$current = (double) substr($last, $l);
			if($current < $best) {
				$best = $current;
				echo $inst;
				//echo "Record istanza $inst: $current".PHP_EOL;
				copy("instance0$inst.sol", "instance0$inst-best.sol");
			} else {
				echo ".";
			}
		} else {
			echo 'Errore: ' . $last . PHP_EOL;
		}
	}
}
