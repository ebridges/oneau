#!/usr/local/bin/bash4

VERSION=$1

if [ -z ${VERSION} ] ; then
    echo 'MVN Release version required.'
    exit;
fi

REMOTE_REPO_ADDR="deploy@1au.com"
REMOTE_REPO_DIR=/home/deploy/repository/ephemeris-db


DATE=`date +%Y%m%d`

M2_REPO=~/.m2/repository
PROJ_HOME=..

TARGET="${PROJ_HOME}/target"

CP=.
CP=${CP}:${M2_REPO}/org/hsqldb/hsqldb/2.2.1/hsqldb-2.2.1.jar

JDBC_PROPERTIES=${PROJ_HOME}/oneau-resources/src/main/resources/jdbc.properties

source ${JDBC_PROPERTIES}

#jdbc:hsqldb:file:/var/local/db/oneau/ephemeris-db
DB_TYPE=`echo ${jdbc_url} | cut -d':' -f 2 `
if [ ${DB_TYPE} != 'hsqldb' ] ; then
    echo 'This script only works with HSQL Databases'
    exit 1;
fi

DB_HOME=`echo ${jdbc_url} | cut -d':' -f 4`
echo ${DB_HOME}
DB_NAME=`basename ${DB_HOME}`
DB_ARCHIVE="${DB_NAME}-${VERSION}.tar"
DB_BAK="${TARGET}/${DB_ARCHIVE}"
TMP_DIR="${TARGET}/ephemeris-db-${DATE}"

if [ -e ${TMP_DIR} ] ; then
    /bin/rm -rf ${TMP_DIR}
fi

mkdir ${TMP_DIR}
for i in ${DB_HOME}* ; do
    cp $i ${TMP_DIR}
done

echo 'readonly=true' >> "${TMP_DIR}/${DB_NAME}.properties"

java -cp ${CP} org.hsqldb.lib.tar.DbBackup --save "${DB_BAK}" "${TMP_DIR}/${DB_NAME}"

echo  gzip -c ${DB_BAK} | ssh ${REMOTE_REPO_ADDR} "cd ${REMOTE_REPO_DIR} && cat -> ${DB_ARCHIVE}.gz"

#/bin/rm -rf ${TMP_DIR}
#rm ${DB_BAK}

echo "done"
