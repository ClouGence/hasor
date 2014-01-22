package org.noe.platform.modules.freemarker.loader.loader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.noe.platform.modules.freemarker.loader.FmTemplateLoader;
import org.noe.platform.modules.freemarker.loader.IResourceLoader;
import org.noe.platform.modules.freemarker.loader.resource.DirResourceLoader;
import freemarker.cache.FileTemplateLoader;
/**
 * 实现了{@link IResourceLoader}接口的{@link FileTemplateLoader}类。
 * @version : 2011-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class DirTemplateLoader extends FileTemplateLoader implements FmTemplateLoader, IResourceLoader {
    private DirResourceLoader dirResourceLoader = null;
    //
    public DirTemplateLoader(File templateDir) throws IOException {
        super(templateDir);
        this.dirResourceLoader = new DirResourceLoader(this.baseDir);
    }
    public String getType() {
        return this.getClass().getSimpleName();
    }
    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        resourcePath = resourcePath.replaceAll("/{2}", "/");
        return this.dirResourceLoader.getResourceAsStream(resourcePath);
    }
    public URL getResource(String resourcePath) throws IOException {
        resourcePath = resourcePath.replaceAll("/{2}", "/");
        return this.dirResourceLoader.getResource(resourcePath);
    }
}