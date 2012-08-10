#!/bin/bash
cd "$(dirname "$0")"
echo Minecraft Land Generator - Print Spawn - Mac OSX
echo $PWD

java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -printspawn

