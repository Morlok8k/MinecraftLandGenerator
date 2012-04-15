@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"

java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadlist MLG_Update_Files.txt