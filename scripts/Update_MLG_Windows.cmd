@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"
SET ScriptTitle=Minecraft Land Generator - Update MLG - Windows
title %ScriptTitle%
cls
echo %ScriptTitle%
echo %BINDIR%

java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -update
java -jar MinecraftLandGenerator.jar -readme _MLG_Readme.txt

