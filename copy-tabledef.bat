@echo off
echo ?? TableDef ??? target ? src...

cd emp-system

set SOURCE_DIR=target\generated-sources\annotations\com\ldjt\emp\entity\table
set DEST_DIR=src\main\java\com\ldjt\emp\entity\table

if not exist "%DEST_DIR%" mkdir "%DEST_DIR%"

xcopy /Y "%SOURCE_DIR%\*TableDef.java" "%DEST_DIR%\"

echo TableDef ???????
cd ..
pause
