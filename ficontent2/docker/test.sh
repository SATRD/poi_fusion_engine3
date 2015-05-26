#!/bin/bash

wget -q -O down.json http://$1:$2/fic2_fe_v3_frontend/citysdk/categories/search?list=poi&ocdName=valencia_demo  >/dev/null
wget -q -O down_ex.json http://$1:$2/fic2_fe_v3_frontend/citysdk/down_expexted.json  >/dev/null
if diff ./down_ex.json ./down.json >/dev/null ; then
  echo "Test passed OK"
  echo 0
else
  echo "The test json file is not the same as expected"
  echo 1
fi
