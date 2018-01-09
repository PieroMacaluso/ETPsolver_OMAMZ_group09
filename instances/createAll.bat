@echo off
FOR /L %%t IN (30,30,300) DO (
echo %%t secondi
	FOR /L %%i IN (1,1,7) DO (
		echo Instance 0%%i
		FOR /L %%g IN (1,1,10) DO (
			echo Giro %%g
			ETPsolver_OMAMZ_group09.exe instance0%%i -t %%t
			copy instance0%%i.sol instance0%%i-t%%t-g%%g.sol
		)
	)
)
echo ciao
pause