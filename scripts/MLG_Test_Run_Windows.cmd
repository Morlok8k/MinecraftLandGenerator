@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"
SET ScriptTitle=Minecraft Land Generator - Test Run - Windows
title %ScriptTitle%
cls
echo %ScriptTitle%
echo %BINDIR%


java -jar MinecraftLandGenerator.jar -nowait 0 0 -w

pause
