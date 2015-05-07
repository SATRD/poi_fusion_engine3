#!/bin/bash

apt-get -y update && \
apt-get -y install wget git curl nano && \
apt-get -y install openjdk-7-jdk && \
apt-get -y install postgresql postgresql-client && \
apt-get -y install postgis && \
apt-get -y install tomcat7 && 
apt-get -y install ant
apt-get -y install postgresql-9.3-postgis-scripts

# define working dir
cd /


# get the whole project from github
rm -rf poi_fusion_engine3
git clone https://github.com/satrd/poi_fusion_engine3

# The FE consists of two parts: (a) FE frontent and (b) FE service. Both are packed in the release dir

# -1. Change pg_hba.conf
mv /poi_fusion_engine3/ficontent2/docker/pg_hba.conf /etc/postgresql/9.3/main/pg_hba.conf
chown postgres:postgres /etc/postgresql/9.3/main/pg_hba.conf
service postgresql restart

# 0. Prepare the fusion log dir (for the FE service)
mkdir /home/fusion
chmod a+rwx /home/fusion

# 1. Start the frontend

service tomcat7 restart
cd poi_fusion_engine3
ant
cd poi_fusion_engine3/release/frontend
cp fic2_fe_v3_frontend.war /var/lib/tomcat7/webapps/
# Sleep for a while so that Tomcat can deploy the WAR file
sleep 10
# Change from tomcat8 to 7
sed -i 's/tomcat8/tomcat7/' /var/lib/tomcat7/webapps/fic2_fe_v3_frontend/config/dbScripts/ocd_valencia_demo.sql
sed -i 's/tomcat8/tomcat7/' /var/lib/tomcat7/webapps/fic2_fe_v3_frontend/config/dbScripts/ocd_tenerife_demo.sql
# Initialize the app with demo data (valencia_demo and tenerife_demo). This may take a while
wget http://localhost:8080/fic2_fe_v3_frontend/init.jsp


echo "You can configure and test the FE in the URL http://<ip_server>:8080/fic2_fe_v3_frontend/"


