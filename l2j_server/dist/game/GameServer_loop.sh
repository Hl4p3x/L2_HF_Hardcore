#!/bin/bash

# exit codes of GameServer:
#  0 normal shutdown
#  2 reboot attempt

while :; do
	[ -f log/java0.log.0 ] && mv log/java0.log.0 "log/`date +%Y-%m-%d_%H-%M-%S`_java.log"
	[ -f log/stdout.log ] && mv log/stdout.log "log/`date +%Y-%m-%d_%H-%M-%S`_stdout.log"
	java -server -Djava.util.logging.manager=com.l2jserver.util.L2LogManager -Dlog4j.configuration=log4j.properties -Dpython.cachedir=../cachedir -Xms2048m -Xmx3696m -jar l2jserver.jar > log/stdout.log 2>&1
	[ $? -ne 2 ] && break
	sleep 10
done
