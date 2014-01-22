package org.noe.platform.modules.freemarker.loader.resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import org.noe.platform.modules.freemarker.loader.IResourceLoader;
/**
* 可以将多个{@link IResourceLoader}接口对象作为一个提供出去。。
* @version : 2011-9-17
* @author 赵永春 (zyc@byshell.org)
*/
public class MultiResourceLoader implements IResourceLoader {
    private ArrayList<IResourceLoader> resourceLoaders = new ArrayList<IResourceLoader>();
    //
    public MultiResourceLoader() {}
    public MultiResourceLoader(IResourceLoader[] loaders) {
        for (IResourceLoader loader : loaders)
            this.resourceLoaders.add(loader);
    }
    public MultiResourceLoader(Collection<IResourceLoader> loaders) {
        for (IResourceLoader loader : loaders)
            this.resourceLoaders.add(loader);
    }
    public void addResourceLoader(IResourceLoader loader) {
        this.resourceLoaders.add(loader);
    }
    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        for (int i = 0; i < resourceLoaders.size(); i++) {
            IResourceLoader loader = resourceLoaders.get(i);
            InputStream in = loader.getResourceAsStream(resourcePath);
            if (in != null)
                return in;
        }
        return null;
    }
    public URL getResource(String resourcePath) throws IOException {
        for (int i = 0; i < resourceLoaders.size(); i++) {
            IResourceLoader loader = resourceLoaders.get(i);
            URL url = loader.getResource(resourcePath);
            if (url != null)
                return url;
        }
        return null;
    }
}