#!/bin/bash
cd "$(dirname "$0")"
echo Minecraft Land Generator - Initial Setup - Mac OSX
echo $PWD

java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -nowait -update
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -nowait -readme _MLG_Readme.txt
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -nowait -downloadlist "MLG_Update_Files_(Mac).txt"
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -nowait -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar
chmod +x *.command
chmod +x *.jar
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -nowait -conf
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -nowait 0 0 -w
rm -rf world

