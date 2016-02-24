FROM java:7
MAINTAINER ZhaoYongChun "zyc@hasor.net"

# work
RUN mkdir -p "/work"
ENV WORK_HOME /work
ADD . /work/sources


# maven
ENV MAVEN_VERSION 3.3.3
RUN curl -fsSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
        && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
        && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven
RUN mkdir -p "$WORK_HOME/repo" && \
    sed -i '/<!-- localRepository/i\<localRepository>'$WORK_HOME/repo'</localRepository>' $MAVEN_HOME/conf/settings.xml
# RUN sed -i '/<\/mirrors>/i\<mirror><id>nexus-osc</id><mirrorOf>*</mirrorOf><name>Nexus osc</name><url>http://maven.oschina.net/content/groups/public/</url></mirror>' $MAVEN_HOME/conf/settings.xml


# tomcat
ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH
RUN mkdir -p "$CATALINA_HOME"
WORKDIR $CATALINA_HOME
ENV TOMCAT_MAJOR 8
ENV TOMCAT_VERSION 8.0.32
ENV TOMCAT_TGZ_URL https://www.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz
RUN set -x && \
	curl -fSL "$TOMCAT_TGZ_URL" -o tomcat.tar.gz && \
	tar -xvf tomcat.tar.gz --strip-components=1 && \
	rm bin/*.bat && \
	rm tomcat.tar.gz*
RUN ln -s /work/sources/conf $WORK_HOME/conf &&  \
    rm -rf $CATALINA_HOME/conf && ln -s $WORK_HOME/conf $CATALINA_HOME/conf && \
    rm -rf $CATALINA_HOME/logs && ln -s $CATALINA_HOME/logs $WORK_HOME/logs
EXPOSE 8080


# === project ===
WORKDIR /work/sources
RUN mkdir -p "$WORK_HOME/webroot" && \
    mvn package -Dmaven.test.skip=true && \
    mv ./target/demo-web-1.0.war $WORK_HOME/webroot/ROOT.war && \
    mvn clean


WORKDIR $CATALINA_HOME
CMD ["catalina.sh", "run"]