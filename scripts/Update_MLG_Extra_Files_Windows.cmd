@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"
SET ScriptTitle=Minecraft Land Generator - Update Extra MLG Files - Windows
title %ScriptTitle%
cls
echo %ScriptTitle%
echo %BINDIR%

java -jar MinecraftLandGenerator.jar -downloadlist "MLG_Update_Files_Windows.txt"

