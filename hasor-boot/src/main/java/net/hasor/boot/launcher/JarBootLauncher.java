package net.hasor.boot.launcher;
import net.hasor.utils.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
public class JarBootLauncher {
    public static void main(String[] args) throws Exception {
        new JarBootLauncher().run(args);
    }
    public void run(String[] args) throws Exception {
        // .prepare jars
        final List<URL> arrayURLs = new ArrayList<URL>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL inputStreamURL = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        File bootJarFile = new File(inputStreamURL.getFile());
        if (bootJarFile.isFile()) {
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(inputStreamURL.getFile());
                Enumeration<JarEntry> jes = jarFile.entries();
                while (jes.hasMoreElements()) {
                    JarEntry e = jes.nextElement();
                    String eventName = e.getName();
                    if (eventName.startsWith("/BOOT-INF/lib") && eventName.endsWith(".jar")) {
                        if (!e.isDirectory()) {
                            arrayURLs.add(classLoader.getResource(eventName));
                        }
                    }
                }
            } finally {
                if (jarFile != null) {
                    jarFile.close();
                }
            }
        } else {
            bootJarFile = new File(bootJarFile, "/BOOT-INF/lib");
            if (bootJarFile.exists()) {
                bootJarFile.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        if (pathname.getName().endsWith(".jar")) {
                            try {
                                arrayURLs.add(pathname.toURI().toURL());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                });
            }
        }
        // .build ClassLoader
        ClassLoader loader = new URLClassLoader(arrayURLs.toArray(new URL[arrayURLs.size()]),//
                Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(loader);
        //
        // .found Main
        InputStream manifestStream = classLoader.getResourceAsStream("META-INF/MANIFEST.MF");
        if (manifestStream == null) {
            throw new FileNotFoundException("MANIFEST.MF not found.");
        }
        Manifest manifest = new Manifest();
        manifest.read(manifestStream);
        Attributes mainAttr = manifest.getMainAttributes();
        String bootMainClass = mainAttr.getValue("Start-Class");
        if (StringUtils.isBlank(bootMainClass)) {
            throw new ClassNotFoundException("start-class Undefined.");
        }
        Class<?> loadClass = loader.loadClass(bootMainClass);
        Method mainMethod = loadClass.getMethod("main", String[].class);
        //
        mainMethod.invoke(null, args);
    }
}