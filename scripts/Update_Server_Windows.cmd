@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"
SET ScriptTitle=Minecraft Land Generator - Update Minecraft Server - Windows
title %ScriptTitle%
cls
echo %ScriptTitle%
echo %BINDIR%

java -jar MinecraftLandGenerator.jar -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar

