#!/bin/bash
CF_APP='timetracking'
CF_APPS_DOMAIN='cfapps.io'

mvn clean install package
if [ "$?" -ne "0" ]; then
  exit $?
fi

DEPLOYED_VERSION_CMD=$(CF_COLOR=false cf routes | awk '$2=="timetracking" { print $4; }' | cut -d"," -f1 | cut -d"-" -f2)
DEPLOYED_VERSION="$DEPLOYED_VERSION_CMD"
echo "Deployed Version: $DEPLOYED_VERSION"
CURRENT_VERSION="blue"
if [ ! -z "$DEPLOYED_VERSION" -a "$DEPLOYED_VERSION" == "blue" ]; then
  CURRENT_VERSION="green"
fi
# push a new version and map the route
cf cs cleardb spark timetrackingdb
cf p "$CF_APP-$CURRENT_VERSION" -n "$CF_APP-$CURRENT_VERSION" -b https://github.com/cloudfoundry/java-buildpack.git --no-start
cf set-env "$CF_APP-$CURRENT_VERSION" spring.datasource.platform mysql
cf bs "$CF_APP-$CURRENT_VERSION" timetrackingdb
cf map-route "$CF_APP-$CURRENT_VERSION" $CF_APPS_DOMAIN -n $CF_APP
cf st "$CF_APP-$CURRENT_VERSION"
if [ ! -z "$DEPLOYED_VERSION" ]; then
  # Unmap the route and delete
  cf unmap-route "$CF_APP-$DEPLOYED_VERSION" $CF_APPS_DOMAIN -n $CF_APP
  # Scaling down
  cf scale "$CF_APP-$DEPLOYED_VERSION" -i 1
fi
