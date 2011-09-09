package org.more.webserver;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
/**
 * @author Administrator
 */
public class TomcatService {
    public String type = "AAAAAA";
    // ////////////////////////////////////////////////////////////////////
    public static void main(String[] args) throws Exception {
        String catalina_home = "d:/";
        Tomcat tomcat = new Tomcat();
        tomcat.setHostname("localhost");
        tomcat.setPort(8080); //设置工作目录,其实没什么用,tomcat需要使用这个目录进行写一些东西 
        tomcat.setBaseDir(catalina_home);
        StandardHost host = new StandardHost();
        tomcat.start();
        //
    }
}