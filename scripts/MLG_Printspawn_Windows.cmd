@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"
SET ScriptTitle=Minecraft Land Generator - Print Spawn - Windows
title %ScriptTitle%
cls
echo %ScriptTitle%
echo %BINDIR%

java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -printspawn

