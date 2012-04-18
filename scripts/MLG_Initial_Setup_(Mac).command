#!/bin/bash
cd "$(dirname "$0")"
echo Minecraft Land Generator - Initial Setup - Mac OSX
echo $PWD

java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -update
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -readme _MLG_Readme.txt
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadlist "MLG_Update_Files_(Mac).txt"
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar
chmod +x *.command
chmod +x *.jar
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -conf
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar 0 0 -w
rm -rf world

