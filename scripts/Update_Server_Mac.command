#!/bin/bash
cd "$(dirname "$0")"
echo Minecraft Land Generator - Update Minecraft Server - Mac OSX
echo $PWD

java -jar MinecraftLandGenerator.jar -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar
chmod +x *.jar

