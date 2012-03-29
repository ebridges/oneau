#!/bin/bash

###
### Intended to be run from the machine that the application is to be installed on.
###

REPOSITORY=/home/deploy/repository
RELEASE_MANIFEST=release.mf
JBOSS_PIDFILE=jboss.pid
APP_VERSION=$1
DB_VERSION=$2

if [ -z ${APP_VERSION} ] ; then
    echo "$0 [app version] [db version]";
    exit 1;
fi

if [ -z ${DB_VERSION} ] ; then
    echo "$0 [app version] [db version]";
    exit 1;
fi

WARFILE=`printf '%s/com/oneau/web/oneau-web/%s/oneau-web-%s.war' ${REPOSITORY} ${APP_VERSION} ${APP_VERSION}`
DBFILE=`printf '%s/ephemeris-db/ephemeris-db-%s.tar.gz' ${REPOSITORY} ${DB_VERSION}`

JBOSS_HOME=/opt/jboss
HSQL_HOME=/var/local/db/oneau

# shutdown apache
sudo /etc/init.d/apache2 stop

# shutdown jboss
if [ -e ${JBOSS_PIDFILE} ] ; then
    PID=`cat ${JBOSS_PIDFILE}`
else
    PID=`ps -eo pid,args  | grep jboss.Main | grep -v grep | awk '{ print $1; }`
fi
if [ -z ${PID} ] ; then
    echo "JBoss currently not running."
else 
    echo "Shutting down JBoss (${PID})"
    sudo kill ${PID}
    sleep 15
fi

# install warfile
if [ -e ${JBOSS_HOME}/server/oneau/deploy/oneau-web*.war ] ; then
    echo "Removing previous war file"
    /bin/rm -rf ${JBOSS_HOME}/server/oneau/deploy/oneau-web*.war
fi
echo "Installing war file (${WARFILE})"
/bin/cp ${WARFILE} ${JBOSS_HOME}/server/oneau/deploy

# install database
if [ -e ${HSQL_HOME}/ephemeris-db.properties ] ; then
    echo "Removing previous database"
    sudo /bin/rm -rf ${HSQL_HOME}
    sudo mkdir -p ${HSQL_HOME}
fi
echo "Installing database (${DBFILE})"
cat ${DBFILE} | (cd ${HSQL_HOME} && sudo tar xzf -)

sudo chown -R jboss:jboss ${HSQL_HOME}

# starting jboss
echo "Starting JBoss"
export JBOSS_CONF=oneau
export LAUNCH_JBOSS_IN_BACKGROUND=1 
sudo nohup ${JBOSS_HOME}/bin/run.sh -c ${JBOSS_CONF} &
echo $! > ${JBOSS_PIDFILE}

sleep 15

# starting apache
sudo /etc/init.d/apache2 start


DATE=`date +%Y%m%d`
echo `printf "%s,%s,%s" ${DATE} ${APP_VERSION} ${DB_VERSION}` >> ${RELEASE_MANIFEST}

echo "done"