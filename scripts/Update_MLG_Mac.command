#!/bin/bash
cd "$(dirname "$0")"
echo Minecraft Land Generator - Update MLG - Mac OSX
echo $PWD

java -jar MinecraftLandGenerator.jar -update
chmod +x *.jar
java -jar MinecraftLandGenerator.jar -readme _MLG_Readme.txt

