FROM openjdk:7-jdk
MAINTAINER ZhaoYongChun "zyc@hasor.net"

# maven
ENV MAVEN_VERSION 3.3.9
RUN curl -fsSL http://project.hasor.net/hasor/develop/tools/apache/maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
        && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
        && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven
RUN mkdir -p "/home/repo" && \
    sed -i '/<!-- localRepository/i\<localRepository>/home/repo</localRepository>' $MAVEN_HOME/conf/settings.xml

#
# work
ENV WORK_HOME /usr/rsfcenter/worker
ENV RSF_HOME  /usr/rsfcenter
#ENV APP_CONFIG

#各种端口
EXPOSE 8000
EXPOSE 2180
EXPOSE 2181
EXPOSE 2182

ADD . /usr/rsfcenter/src
RUN cd $RSF_HOME/src && \
    ./build.sh && \
    cd `find ./build -name 'bin'` && \
    cp -R ../* $RSF_HOME
WORKDIR $RSF_HOME/bin

CMD ["./run.sh"]