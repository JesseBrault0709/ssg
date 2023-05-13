package com.jessebrault.ssg.buildscript

import groovy.transform.BaseScript
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@BaseScript
BuildScriptBase base

final Logger logger = LoggerFactory.getLogger('simple.groovy')

logger.debug('executing simple buildScript')

build(name: 'test') { }

