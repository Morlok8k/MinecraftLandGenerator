@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"
SET Minecraft Land Generator - Run MLG - Windows
title %ScriptTitle%
cls
echo %ScriptTitle%
echo %BINDIR%

java -jar MinecraftLandGenerator.jar

