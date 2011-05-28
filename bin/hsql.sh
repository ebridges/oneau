#!/bin/bash

M2_REPO=~/.m2/repository

CP=.
CP=${CP}:${M2_REPO}/org/hsqldb/hsqldb/2.0.0/hsqldb-2.0.0.jar

java -Xms1024m -Xmx2048m -cp ${CP} org.hsqldb.util.DatabaseManagerSwing
