package org.noe.platform.modules.freemarker.loader.resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.noe.platform.modules.freemarker.loader.IResourceLoader;
/**
 * 将一个File对象所代表的路径作为根路径，资源获取相对于该路径下。
 * @version : 2011-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class DirResourceLoader implements IResourceLoader {
    private File baseDir = null;
    public DirResourceLoader(File baseDir) throws IOException {
        this.baseDir = baseDir;
    }
    public URL getResource(String resourcePath) throws MalformedURLException {
        File resource = new File(this.baseDir, resourcePath);
        if (resource.canRead() == true)
            return resource.toURI().toURL();
        return null;
    }
    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        File resource = new File(this.baseDir, resourcePath);
        if (resource.canRead() == true)
            return new FileInputStream(resource);
        return null;
    }
}