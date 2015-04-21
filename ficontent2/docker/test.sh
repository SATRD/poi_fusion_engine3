#!/bin/bash

wget http://$1:$2/fic2_fe_v3_frontend/citysdk/categories/search?list=poi&ocdName=valencia_demo -q -O down.json >/dev/null
wget http://$1:$2/fic2_fe_v3_frontend/citysdk/down_expexted.json -q -O down_ex.json >/dev/null
if diff ./down_ex.json ./down.json >/dev/null ; then
  echo "Test passed OK"
  echo 0
else
  echo "The test json file is not the same as expected"
  echo 1
fi
