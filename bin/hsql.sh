#!/bin/bash

HSQL_VERSION='2.2.4'

#M2_REPO="~/.m2/repository"
M2_REPO="c:/Users/EdwardB/.m2/repository"

CP=${M2_REPO}/org/hsqldb/hsqldb/${HSQL_VERSION}/hsqldb-${HSQL_VERSION}.jar

java -Xms1024m -Xmx2048m -cp ${CP} org.hsqldb.util.DatabaseManagerSwing
