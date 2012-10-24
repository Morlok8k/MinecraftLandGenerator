#!/bin/sh

## Minecraft Land Generator - GitHub Update Script
## Morlok8k - Updated 6/12/2012

## add source files to .jar
zip -r ./bin/MinecraftLandGenerator.jar ./src/ ./scripts/ ./lib/

## make .jar executable
cd ./bin/
chmod a+x ./MinecraftLandGenerator.jar

## generate new config file
java -jar ./MinecraftLandGenerator.jar -nowait -conf

## make new readme file
cd ..
java -jar ./bin/MinecraftLandGenerator.jar -nowait -readme README

## delete "unofficial" duplicate BuildID file (created by running/debugging MLG inside eclipse)
rm ./MLG-BuildID


## add files to git, and commit.
git add .
git commit -a
git push origin master
echo Pausing for 20sec
sleep 20

