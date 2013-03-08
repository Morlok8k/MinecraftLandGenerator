#!/bin/bash
cd "$(dirname "$0")"
echo Minecraft Land Generator - Test Run - Mac OSX
echo $PWD


java -jar MinecraftLandGenerator.jar -nowait 0 0 -w
