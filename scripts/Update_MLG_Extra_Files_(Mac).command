#!/bin/bash
cd "$(dirname "$0")"

java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadlist MLG_Update_Files.txt
chmod +x *.command
