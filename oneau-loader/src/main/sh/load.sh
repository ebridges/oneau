#!/usr/local/bin/bash4

# http://stackoverflow.com/questions/1494178/how-to-define-hash-tables-in-bash
declare -A WRITERS
WRITERS=(
    ["noop"]="com.oneau.loader.ephemeris.NoOpObservationWriter"
    ["jdbc"]="com.oneau.loader.ephemeris.JdbcObservationWriter"
    ["stdout"]="com.oneau.loader.ephemeris.StdoutObservationWriter"
    ["sql"]="com.oneau.loader.ephemeris.StdoutSqlObservationWriter"
)

w=$1
if [ -z "${w}" ];
then
	w="stdout"
fi

WRITER=${WRITERS[${w}]}
DATE=`date +%Y%m%d`
#FILE="../sql/hsql/data-v1.${DATE}.sql"

PROJ_HOME=../../../..
M2_REPO=~/.m2/repository

JDBC_PROPERTIES=${PROJ_HOME}/oneau-resources/src/main/resources/jdbc.properties

source ${JDBC_PROPERTIES}

CP=.
CP=${CP}:${M2_REPO}/org/hsqldb/hsqldb/2.2.1/hsqldb-2.2.1.jar
CP=${CP}:${M2_REPO}/postgresql/postgresql/8.4-701.jdbc3/postgresql-8.4-701.jdbc3.jar
CP=${CP}:${M2_REPO}/log4j/log4j/1.2.15/log4j-1.2.15.jar
CP=${CP}:${PROJ_HOME}/oneau-loader/target/classes
CP=${CP}:${PROJ_HOME}/oneau-api/target/classes
CP=${CP}:${PROJ_HOME}/oneau-core/target/classes
CP=${CP}:${PROJ_HOME}/oneau-data/target/classes
CP=${CP}:${PROJ_HOME}/oneau-resources/target/classes

# build up comma separated list of all ascp files from jar file:
## ascp2200.405 has issues because of an overlapping date range, so skipping.
EPH_FILES=`jar tf ${PROJ_HOME}/oneau-resources/target/oneau-resources.jar | egrep 'ascp|header' | grep -v ascp2200.405 | sort -r | perl -e 'while (<>) { chomp; if($notfirst) {print ",";} print "/$_"; $notfirst=1 }'`

# enable verbose GC info: -verbose
java -cp ${CP} \
   -Djava.util.logging.config.file=${PROJ_HOME}/oneau-resources/src/main/resources/logging.properties \
   -DjdbcUrl=${jdbc_url} \
   -DjdbcUsername=${jdbc_system_username} \
   -DjdbcPassword=${jdbc_system_password} \
   -DjdbcDriver=${jdbc_driver} \
   com.oneau.parser.ephemeris.EphemerisParser \
   --observation-writer ${WRITER} \
   --ephemeris-files ${EPH_FILES}

#gzip ${FILE}
