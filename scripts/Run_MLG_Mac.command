#!/bin/bash
cd "$(dirname "$0")"
echo Minecraft Land Generator - Run MLG - Mac OSX
echo $PWD

java -Djava.awt.headless=true -Xms1024m -Xmx1024m -Xincgc -jar MinecraftLandGenerator.jar

