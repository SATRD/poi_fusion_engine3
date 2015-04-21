#!/bin/bash

/etc/init.d/postgresql start
/etc/init.d/tomcat7 start
# 2. Start the FE service component. As it is and endless loop it can serve for the last line
cd /
cd poi_fusion_engine3/release/service
java -jar fic2_fe_v3.jar

#exec tail -f /var/log/tomcat7/catalina.out
