#!/bin/sh
# ----------------------------------------------------------------------------
mvn clean install
mvn clean && cd hasor-commons  && mvn clean deploy $@ && cd ../
mvn clean && cd hasor-core     && mvn clean deploy $@ && cd ../
mvn clean && cd hasor-db       && mvn clean deploy $@ && cd ../
mvn clean && cd hasor-web      && mvn clean deploy $@ && cd ../
mvn clean && cd hasor-dataql   && mvn clean deploy $@ && cd ../
mvn clean && cd hasor-rsf      && mvn clean deploy $@ && cd ../
mvn clean && cd hasor-registry && mvn clean deploy $@ && cd ../
mvn clean && cd hasor-plugins  && mvn clean deploy $@ && cd ../
mvn clean && mvn package $@