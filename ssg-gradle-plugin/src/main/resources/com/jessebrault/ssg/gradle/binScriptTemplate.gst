#!/usr/bin/env bash

if [ "\$1" == "--debug" ]; then
  shift
  java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:8192 -cp $cp com.jessebrault.ssg.StaticSiteGeneratorCli "\$@"
else
  java -cp $cp com.jessebrault.ssg.StaticSiteGeneratorCli "\$@"
fi
