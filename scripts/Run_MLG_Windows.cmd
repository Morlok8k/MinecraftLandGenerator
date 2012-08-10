@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"
echo Minecraft Land Generator - Run MLG - Windows
echo %BINDIR%

java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar

