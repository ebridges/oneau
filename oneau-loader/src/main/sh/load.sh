PROJ_HOME=../../../..
M2_REPO=~/.m2/repository

CP=.
CP=${CP}:${PROJ_HOME}/oneau-loader/target/classes
CP=${CP}:${PROJ_HOME}/oneau-core/target/oneau-core.jar
CP=${CP}:${PROJ_HOME}/oneau-resources/target/oneau-resources.jar
CP=${CP}:${M2_REPO}/log4j/log4j/1.2.15/log4j-1.2.15.jar

java -cp ${CP} com.oneau.parser.ephemeris.EphemerisParser
