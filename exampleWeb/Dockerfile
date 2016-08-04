FROM java:7
MAINTAINER ZhaoYongChun "zyc@hasor.net"

# maven
ENV MAVEN_VERSION 3.3.9
RUN curl -fsSL http://project.hasor.net/attachments/apache/maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
        && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
        && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven
RUN mkdir -p "/home/repo" && \
    sed -i '/<!-- localRepository/i\<localRepository>/home/repo</localRepository>' $MAVEN_HOME/conf/settings.xml

# tomcat
ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH
RUN mkdir -p "$CATALINA_HOME"
WORKDIR $CATALINA_HOME
ENV TOMCAT_MAJOR 8
ENV TOMCAT_VERSION 8.5.0
ENV TOMCAT_TGZ_URL http://project.hasor.net/attachments/apache/tomcat/$TOMCAT_VERSION/apache-tomcat-$TOMCAT_VERSION.tar.gz
RUN set -x && \
	curl -fSL "$TOMCAT_TGZ_URL" -o tomcat.tar.gz && \
	tar -xvf tomcat.tar.gz --strip-components=1 && \
	rm bin/*.bat && \
	rm tomcat.tar.gz*

#
# work
ENV EXAMPLE_HOME /home/example
RUN mkdir -p "$EXAMPLE_HOME/logs"
ADD . /home/example/sources

RUN ln -s $EXAMPLE_HOME/sources/conf/online_home $EXAMPLE_HOME && \
    ln -s $EXAMPLE_HOME/sources/conf/tomcat $EXAMPLE_HOME/tomcat && \
    rm -rf $CATALINA_HOME/conf && ln -s $EXAMPLE_HOME/tomcat $CATALINA_HOME/conf && \
    rm -rf $CATALINA_HOME/logs && ln -s $CATALINA_HOME/logs $EXAMPLE_HOME/logs
EXPOSE 8080

# === project ===
WORKDIR /home/example/sources
RUN mkdir -p "$EXAMPLE_HOME/webroot" && \
    mvn package -Dmaven.test.skip=true && \
    mv ./target/demo-web-1.0.war $EXAMPLE_HOME/webroot/ROOT.war && \
    mvn clean

WORKDIR $CATALINA_HOME
CMD ["catalina.sh", "run -DWORK_HOME=$EXAMPLE_HOME"]