#!/bin/php
<?php

define('INPUT', 'test.stu');

$data = file_get_contents(INPUT);
if($data === false) {
	echo 'Cannot read '.INPUT.PHP_EOL;
	exit(1);
}
$lines = preg_split('/[\n\r]/', $data, -1, PREG_SPLIT_NO_EMPTY);
if(count($lines) <= 0) {
	echo 'Empty instance.stu'.PHP_EOL;
	exit(2);
}

echo 'Ho letto ' . count($lines) . ' righe'.PHP_EOL;

foreach($lines as $oneline) {
	$pieces = explode(' ', $oneline);
	$student = $pieces[0];
	$exam = $pieces[1];
	
	$esami_studenti[$exam][$student] = $student;
}

ksort($esami_studenti);
$chiavi = array_keys($esami_studenti);

foreach($esami_studenti as $esame => $studenti) {
	foreach($esami_studenti as $esameprimo => $studentiprimi) {
		$conflitti[$esame][$esameprimo] = 0;
	}
}

for($i = 0; $i < count($esami_studenti); $i++) {
	$esamek = $chiavi[$i];
	$esame = $esami_studenti[$esamek];
	for($j = $i+1; $j < count($esami_studenti); $j++) {
		$esameprimok = $chiavi[$j];
		$esameprimo = $esami_studenti[$esameprimok];
		echo 'Valuto ' . $esamek . ' e ' . $esameprimok . ': ';
		foreach($esame as $studente) {
			if(array_key_exists($studente, $esameprimo)) {
				if(isset($conflitti[$esamek][$esameprimok])) {
					$conflitti[$esamek][$esameprimok]++;
					$conflitti[$esameprimok][$esamek]++;
				} else {
					$conflitti[$esamek][$esameprimok] = 1;
					$conflitti[$esameprimok][$esamek] = 1;
				}
				echo '*';
			}
		}
		echo PHP_EOL;
	}
}

$risultato = '';
foreach($conflitti as $esame => $esamiprimi) {
	foreach($esamiprimi as $esameprimo => $nconflitti) {
		$risultato .= sprintf("%' 4d", $nconflitti) . ', ';
	}
	$risultato .= PHP_EOL;
}
file_put_contents('tabella.dat', $risultato);
