#!/bin/bash
cd "$(dirname "$0")"
echo Minecraft Land Generator - Update Extra MLG Files All OS - Mac OSX
echo $PWD

java -jar MinecraftLandGenerator.jar -downloadlist "MLG_Update_Files.txt"
chmod +x *.command
chmod +x *.jar

