REM plink and pscddp must be present in your system variable PATH
REM Deploy to Hortonworks sandbox:
REM deploy
REM  root
REM  hadoop
REM  127.0.0.1
REM  2222
REM  D:\src\git\hadoop_hello_world\logs_analysis\src\main\input\000000
REM  D:\src\git\hadoop_hello_world\logs_analysis\target\scala-2.11\logs_analysis-assembly-0.1-SNAPSHOT.jar

set USER=%1
set PASS=%2
set HOST=%3
set PORT=%4

set INPUT_PATH=%5
for %%a in ("%INPUT_PATH%") do (
    SET INPUT_NAME=%%~nxa
)

set JAR_PATH=%6
for %%a in ("%JAR_PATH%") do (
    SET JAR_NAME=%%~nxa
)

REM prepare input file
pscp -pw %PASS% -P %PORT% %INPUT_PATH% %USER%@%HOST%:/home/%USER%
plink -pw %PASS% -P %PORT% %USER%@%HOST% hadoop fs -mkdir /user
plink -pw %PASS% -P %PORT% %USER%@%HOST% hadoop fs -mkdir /user/%USER%
plink -pw %PASS% -P %PORT% %USER%@%HOST% hadoop fs -mkdir /user/%USER%/hw_himr_1
plink -pw %PASS% -P %PORT% %USER%@%HOST% hadoop fs -mkdir /user/%USER%/hw_himr_1/input
plink -pw %PASS% -P %PORT% %USER%@%HOST% hadoop fs -put /home/%USER%/%INPUT_NAME%  /user/%USER%/hw_himr_1/input

REM deploy & run job
pscp -pw %PASS% -P %PORT% %JAR_PATH% %USER%@%HOST%:/home/%USER%
plink -pw %PASS% -P %PORT% %USER%@%HOST% hadoop fs -rm -r /user/%USER%/hw_himr_1/output
plink -pw %PASS% -P %PORT% %USER%@%HOST% hadoop jar /home/%USER%/%JAR_NAME% -Ddir.input=/user/%USER%/hw_himr_1/input/ -Ddir.output=/user/%USER%/hw_himr_1/output/ -Dcompress=false

REM Print out compressed file
REM plink -pw %PASS% -P %PORT% %USER%@%HOST%  hadoop fs -libjars /home/%USER%/%JARNAME% -text /user/%USER%/hw_himr_1/output/part-r-00000
REM Print out plain file
REM plink -pw %PASS% -P %PORT% %USER%@%HOST%  hadoop fs -cat /user/%USER%/hw_himr_1/output/part-r-00000
