@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"
echo Minecraft Land Generator - Update Extra MLG Files - Windows
echo %BINDIR%

java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadlist "MLG_Update_Files_(Windows).txt"

