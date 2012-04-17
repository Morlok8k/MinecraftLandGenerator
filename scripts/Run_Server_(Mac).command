#!/bin/bash
cd "$(dirname "$0")"
echo Minecraft Land Generator - Run Minecraft Server - Mac OSX
echo $PWD

java -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true -Xms1024m -Xmx1024m -Xincgc -jar minecraft_server.jar

