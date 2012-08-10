#!/bin/bash
cd "$(dirname "$0")"
echo Minecraft Land Generator - Update Extra MLG Files - Mac OSX
echo $PWD

java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadlist "MLG_Update_Files_(Mac).txt"
chmod +x *.command
chmod +x *.jar

