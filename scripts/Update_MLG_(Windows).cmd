@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"

java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -update
java -jar MinecraftLandGenerator.jar -readme _MLG_Readme.txt
