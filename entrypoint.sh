#!/bin/bash

source /home/web/.sdkman/bin/sdkman-init.sh

cd /app/backend_dist/backend-1.0/
./bin/backend -Dplay.evolutions.db.default.autoApply=true
