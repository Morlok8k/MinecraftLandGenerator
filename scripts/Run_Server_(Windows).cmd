@echo off
SET BINDIR=%~dp0
CD /D "%BINDIR%"

java -client -showversion -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true -Xms1024m -Xmx1024m -Xincgc -jar minecraft_server.jar
