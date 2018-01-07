@echo off
FOR /L %%i IN (1,1,7) DO (
	echo Instance 0%%i
		copy instance0%%i.sol ..\instances\instance0%%i_OMAMZ_group09.sol
)
pause