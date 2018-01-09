#!/usr/bin/python
from shutil import copyfile
from subprocess import Popen, PIPE
from pathlib import Path
import os.path
import signal
import msvcrt
import sys

best = {1: 9999.9, 2: 9999.9, 3: 9999.9, 4: 9999.9, 5: 9999.9, 6: 9999.9, 7: 9999.9}
instance = '../instance0'
time = '5'
fname = 'log.txt'

my_file = Path(fname)
if my_file.exists():
    i = 1
    with open(fname) as f:
        content = f.readlines()
    content = [x.strip() for x in content]
    i = 1
    for x in content:
        best[i] = x
        i = i+1

        # you may also want to remove whitespace characters like `\n` at the end of each line

while True:
    # for val in range(1):
    val = 1
    i = instance + str(val)
    sol = i + '.sol'
    b = i + '-best.sol'
    p = Popen(['java', '-jar', '../../JavaSA/target/ETPsolver_OMAMZ_group09.jar', i, '-t', time],
              stdin=PIPE, stdout=PIPE, stderr=PIPE, shell=True)
    output, err = p.communicate(b'stdin')
    cost = output.decode()
    f = float(cost)
    be = float(best[val])
    if f < be:
        best[val] = f
        # print(val)
        print(f)
        copyfile(sol, b)
    else:
        print(".")
