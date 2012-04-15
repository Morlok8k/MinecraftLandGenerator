@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"

java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar 2000 2000
