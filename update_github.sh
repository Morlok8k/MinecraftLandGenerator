#!/bin/sh

## Minecraft Land Generator - GitHub Update Script
## Morlok8k - Updated 12/26/2011

zip ./bin/MinecraftLandGenerator.jar ./src/corrodias/minecraft/landgenerator/Main.java

cd ./bin/
chmod a+x ./MinecraftLandGenerator.jar

java -jar ./MinecraftLandGenerator.jar -conf

cd ..
java -jar ./bin/MinecraftLandGenerator.jar -readme README

rm ./MLG-BuildID



git add .
git commit -a
git push origin master
