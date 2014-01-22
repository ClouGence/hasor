package org.noe.platform.modules.freemarker.loader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
/**
 * 给定资源路径装载该资源对象。
 * @version : 2011-9-14
 * @author 赵永春 (zyc@byshell.org) 
 */
public interface IResourceLoader {
    public URL getResource(String resourcePath) throws IOException;
    /**装载指定资源。*/
    public InputStream getResourceAsStream(String resourcePath) throws IOException;
}