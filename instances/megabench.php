#!/usr/bin/php
<?php

function printresults() {
	global $top, $best, $worst, $sum, $iterations;
	echo PHP_EOL;
	foreach($top as $inst => $record) {
		$max = $worst[$inst];
		$avg = $sum[$inst]/$iterations[$inst];
		$min = $best[$inst];
		echo "\033[0mIstanza $inst (MIN/AVG/MAX/RECORD): $min/$avg/$max/$record".PHP_EOL;
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

$top = [
		1 => 157.075,
		2 => 36.466,
		3 => 34.329,
		4 => 8.264,
		5 => 13.820,
		6 => 3.506,
		7 => 10.487
		];
$best = [
		1 => 99999.9,
		2 => 99999.9,
		3 => 99999.9,
		4 => 99999.9,
		5 => 99999.9,
		6 => 99999.9,
		7 => 99999.9
		];
$worst = [
		1 => 0,
		2 => 0,
		3 => 0,
		4 => 0,
		5 => 0,
		6 => 0,
		7 => 0
		];
$sum = [
		1 => 0,
		2 => 0,
		3 => 0,
		4 => 0,
		5 => 0,
		6 => 0,
		7 => 0
		];
$iterations = [
		1 => 0,
		2 => 0,
		3 => 0,
		4 => 0,
		5 => 0,
		6 => 0,
		7 => 0
		];
$l = strlen("Final solution: ");


while(true) {
	foreach($best as $inst => &$topcurrent) {
		exec("java -jar ../JavaSA/target/ETPsolver_OMAMZ_group09.jar instance0$inst -t 30", $output);
		//exec("java -jar ETPsolver_OMAMZ_group09.jar instance0$inst -t 30", $output);

		if($windows && file_exists('stop.txt')) {
			echo PHP_EOL.'File di stop trovato :O'.PHP_EOL;
			printresults();
		}

		$last = array_pop($output);
		if(substr($last, 0, $l)) {
			$current = (double) substr($last, $l);
			if($current > $worst[$inst] && $current < $best[$inst]) {
				$worst[$inst] = $current;
				$best[$inst] = $current;
				if($current < $topcurrent) {
					$topcurrent = $current;
					echo "\033[0;31m$inst";
					copy("instance0$inst.sol", "instance0$inst-best.sol");
					echo $windows ? 'R' : "\033[m\033[42m$inst\033[m";
				} else {
					echo $windows ? '.' : "\033[0;34m$inst";
				}
			} else if($current > $worst[$inst]) {
				$worst[$inst] = $current;
				echo $windows ? '.' : "\033[0;31m$inst";
			} else if($current < $best[$inst]) {
				$best[$inst] = $current;
				if($current < $topcurrent) {
					$topcurrent = $current;
					echo "\033[0;31m$inst";
					//echo "Record istanza $inst: $current".PHP_EOL;
					copy("instance0$inst.sol", "instance0$inst-best.sol");
					echo $windows ? 'R' : "\033[m\033[42m$inst\033[m";
				} else {
					echo $windows ? 'b' : "\033[0;32m$inst";
				}
			}
			file_put_contents("log_$inst.txt", $current.PHP_EOL, FILE_APPEND);
			$sum[$inst] += $current;
			$iterations[$inst]++;
		} else {
			echo 'Errore: ' . $last . PHP_EOL;
		}
	}
}
