@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"
echo Minecraft Land Generator - Update MLG - Windows
echo %BINDIR%

java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -update
java -jar MinecraftLandGenerator.jar -readme _MLG_Readme.txt

