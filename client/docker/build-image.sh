#!/usr/bin/env bash

CURRENT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

ONTOP_HOME=${CURRENT_DIR}/../..

cd ${ONTOP_HOME}
${ONTOP_HOME}/mvnw clean install -DskipTests
cd build/distribution
${ONTOP_HOME}/mvnw assembly:assembly
cd target
rm -rf ontop
unzip -o ontop-cli*.zip -d ontop
cd ontop
rm -r ontop.bat ontop-completion.sh jdbc
docker build -t ontop/ontop-endpoint -f ${ONTOP_HOME}/client/docker/Dockerfile .
cd ${CURRENT_DIR}
