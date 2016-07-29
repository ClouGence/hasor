FROM java:7
MAINTAINER ZhaoYongChun "zyc@hasor.net"

# maven
ENV MAVEN_VERSION 3.3.3
RUN curl -fsSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
        && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
        && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven
RUN mkdir -p "~/repo" && \
    sed -i '/<!-- localRepository/i\<localRepository>~/repo</localRepository>' $MAVEN_HOME/conf/settings.xml

# tomcat
ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH
RUN mkdir -p "$CATALINA_HOME"
WORKDIR $CATALINA_HOME
ENV TOMCAT_MAJOR 8
ENV TOMCAT_VERSION 8.0.32
ENV TOMCAT_TGZ_URL http://project.hasor.net/attachments/apache/tomcat/8.5.0/apache-tomcat-8.5.0.tar.gz
RUN set -x && \
	curl -fSL "$TOMCAT_TGZ_URL" -o tomcat.tar.gz && \
	tar -xvf tomcat.tar.gz --strip-components=1 && \
	rm bin/*.bat && \
	rm tomcat.tar.gz*

#
# work
ENV WORK_HOME /home/example
RUN mkdir -p "$WORK_HOME/logs"
ADD .                /home/example/sources
ADD ./conf/tomcat    /home/example/conf
ADD ./conf/work_home /home/example/work
RUN rm -rf $CATALINA_HOME/conf && ln -s $WORK_HOME/conf $CATALINA_HOME/conf && \
    rm -rf $CATALINA_HOME/logs && ln -s $CATALINA_HOME/logs $WORK_HOME/logs
EXPOSE 8080

# === project ===
WORKDIR /home/example/sources
RUN mkdir -p "$WORK_HOME/webroot" && \
    mvn package -Dmaven.test.skip=true && \
    mv ./target/demo-web-1.0.war $WORK_HOME/webroot/ROOT.war && \
    mvn clean

WORKDIR $CATALINA_HOME
CMD ["catalina.sh", "run -Dadmins=21756265276013726654"]