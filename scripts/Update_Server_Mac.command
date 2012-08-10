#!/bin/bash
cd "$(dirname "$0")"
echo Minecraft Land Generator - Update Minecraft Server - Mac OSX
echo $PWD

java -Djava.awt.headless=true -Xms1024m -Xmx1024m -Xincgc -jar MinecraftLandGenerator.jar -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar
chmod +x *.jar

