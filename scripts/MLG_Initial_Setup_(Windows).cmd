@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"
echo Minecraft Land Generator - Initial Setup - Windows
echo %BINDIR%

java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -update
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -readme _MLG_Readme.txt
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadlist "MLG_Update_Files_(Windows).txt"
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -conf
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar 0 0 -w

