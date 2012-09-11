@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"
SET ScriptTitle=Minecraft Land Generator - Initial Setup - Windows
title %ScriptTitle%
cls
echo %ScriptTitle%
echo %BINDIR%

java -jar MinecraftLandGenerator.jar -nowait -update
java -jar MinecraftLandGenerator.jar -nowait -readme _MLG_Readme.txt
java -jar MinecraftLandGenerator.jar -nowait -downloadlist "MLG_Update_Files_Windows.txt"
java -jar MinecraftLandGenerator.jar -nowait -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar
java -jar MinecraftLandGenerator.jar -nowait -conf
java -jar MinecraftLandGenerator.jar -nowait 0 0 -w
RMDIR /Q /S world

pause
