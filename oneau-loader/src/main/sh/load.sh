
DATE=`date +%Y%m%d`
FILE="../sql/hsql/data-v1.${DATE}.sql"

PROJ_HOME=../../../..
M2_REPO=~/.m2/repository

JDBC_PROPERTIES=${PROJ_HOME}/oneau-resources/src/main/resources/jdbc.properties

source ${JDBC_PROPERTIES}

CP=.
CP=${CP}:${PROJ_HOME}/oneau-loader/target/classes
CP=${CP}:${PROJ_HOME}/oneau-api/target/oneau-api.jar
CP=${CP}:${PROJ_HOME}/oneau-core/target/oneau-core.jar
CP=${CP}:${PROJ_HOME}/oneau-resources/target/oneau-resources.jar
CP=${CP}:${M2_REPO}/hsqldb/hsqldb/1.8.0.10/hsqldb-1.8.0.10.jar
CP=${CP}:${M2_REPO}/log4j/log4j/1.2.15/log4j-1.2.15.jar


java -cp ${CP} \
   -DjdbcUrl=${jdbc_url} \
   -DjdbcUsername=${jdbc_username} \
   -DjdbcPassword=${jdbc_password} \
   -DjdbcDriver=${jdbc_driver} \
   com.oneau.parser.ephemeris.EphemerisParser \
   -w com.oneau.loader.ephemeris.JdbcObservationWriter

#gzip ${FILE}
