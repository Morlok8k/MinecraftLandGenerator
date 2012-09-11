@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"
SET ScriptTitle=Minecraft Land Generator - Print Spawn - Windows
title %ScriptTitle%
cls
echo %ScriptTitle%
echo %BINDIR%

java -jar MinecraftLandGenerator.jar -printspawn

