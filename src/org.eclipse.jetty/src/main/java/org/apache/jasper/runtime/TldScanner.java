/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jasper.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletException;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.Options;
import org.apache.jasper.xmlparser.ParserUtils;
import org.apache.jasper.xmlparser.TreeNode;
import org.apache.jasper.compiler.Localizer;

/**
 * A container for all tag libraries that are defined "globally"
 * for the web application.
 * 
 * Tag Libraries can be defined globally in one of two ways:
 *   1. Via <taglib> elements in web.xml:
 *      the uri and location of the tag-library are specified in
 *      the <taglib> element.
 *   2. Via packaged jar files that contain .tld files
 *      within the META-INF directory, or some subdirectory
 *      of it. The taglib is 'global' if it has the <uri>
 *      element defined.
 *
 * A mapping between the taglib URI and its associated TaglibraryInfoImpl
 * is maintained in this container.
 * Actually, that's what we'd like to do. However, because of the
 * way the classes TagLibraryInfo and TagInfo have been defined,
 * it is not currently possible to share an instance of TagLibraryInfo
 * across page invocations. A bug has been submitted to the spec lead.
 * In the mean time, all we do is save the 'location' where the
 * TLD associated with a taglib URI can be found.
 *
 * When a JSP page has a taglib directive, the mappings in this container
 * are first searched (see method getLocation()).
 * If a mapping is found, then the location of the TLD is returned.
 * If no mapping is found, then the uri specified
 * in the taglib directive is to be interpreted as the location for
 * the TLD of this tag library.
 *
 * @author Pierre Delisle
 * @author Jan Luehe
 * @author Kin-man Chung servlet 3.0 JSP plugin, tld cache etc
 */

public class TldScanner implements ServletContainerInitializer {

    // Logger
    private static Logger log =
            Logger.getLogger(TldScanner.class.getName());

    /**
     * The types of URI one may specify for a tag library
     */
    public static final int ABS_URI = 0;
    public static final int ROOT_REL_URI = 1;
    public static final int NOROOT_REL_URI = 2;

    private static final String WEB_XML = "/WEB-INF/web.xml";
    private static final String FILE_PROTOCOL = "file:";
    private static final String JAR_FILE_SUFFIX = ".jar";

    // Names of system Uri's that are ignored if referred in WEB-INF/web.xml
    private static HashSet<String> systemUris = new HashSet<String>();
    private static HashSet<String> systemUrisJsf = new HashSet<String>();

    // A Cache is used for system jar files.
    // The key is the name of the jar file, the value is an array of
    // TldInfo, one for each of the TLD in the jar file
    private static Map<String, TldInfo[]> jarTldCache =
        new ConcurrentHashMap<String, TldInfo[]>();

    private static final String EAR_LIB_CLASSLOADER =
        "org.glassfish.javaee.full.deployment.EarLibClassLoader";

    private static final String IS_STANDALONE_ATTRIBUTE_NAME =
        "org.glassfish.jsp.isStandaloneWebapp";

    /**
     * The mapping of the 'global' tag library URI (as defined in the tld) to
     * the location (resource path) of the TLD associated with that tag library.
     * The location is returned as a String array:
     *    [0] The location of the tld file or the jar file that contains the tld
     *    [1] If the location is a jar file, this is the location of the tld.
     */
    private HashMap<String, String[]> mappings;

    /**
     * A local cache for keeping track which jars have been scanned.
     */
    private Map<String, TldInfo[]> jarTldCacheLocal =
        new HashMap<String, TldInfo[]>();

    private ServletContext ctxt;
    private boolean isValidationEnabled;
    private boolean useMyFaces = false;
    private boolean scanListeners;  // true if scan tlds for listeners
    private boolean doneScanning;   // true if all tld scanning done


    //*********************************************************************
    // Constructor and Initilizations
    
    /*
     * Initializes the set of JARs that are known not to contain any TLDs
     */
    static {
        systemUrisJsf.add("http://java.sun.com/jsf/core");
        systemUrisJsf.add("http://java.sun.com/jsf/html");
        systemUris.add("http://java.sun.com/jsp/jstl/core");
    }

    /**
     * Default Constructor.
     * This is only used for implementing ServletContainerInitializer.
     * ServletContext will be supplied in the method onStartUp;
     */
    public TldScanner() {
    }

    /**
     * Constructor used in Jasper
     */
    public TldScanner(ServletContext ctxt, boolean isValidationEnabled) {
        this.ctxt = ctxt;
        this.isValidationEnabled = isValidationEnabled;
        Boolean b = (Boolean) ctxt.getAttribute("com.sun.faces.useMyFaces");
        if (b != null) {
            useMyFaces = b.booleanValue();
        }
    }


    public void onStartup(java.util.Set<java.lang.Class<?>> c,
               ServletContext ctxt) throws ServletException {
        this.ctxt = ctxt;
        Boolean b = (Boolean) ctxt.getAttribute("com.sun.faces.useMyFaces");
        if (b != null) {
            useMyFaces = b.booleanValue();
        }
        ServletRegistration reg = ctxt.getServletRegistration("jsp");
        if (reg == null) {
            return;
        }
        String validating = reg.getInitParameter("validating");
        isValidationEnabled = "true".equals(validating);

        scanListeners = true;
        scanTlds();

        ctxt.setAttribute(Constants.JSP_TLD_URI_TO_LOCATION_MAP, mappings);
    }

    /**
     * Gets the 'location' of the TLD associated with the given taglib 'uri'.
     *
     * Returns null if the uri is not associated with any tag library 'exposed'
     * in the web application. A tag library is 'exposed' either explicitly in
     * web.xml or implicitly via the uri tag in the TLD of a taglib deployed
     * in a jar file (WEB-INF/lib).
     * 
     * @param uri The taglib uri
     *
     * @return An array of two Strings: The first element denotes the real
     * path to the TLD. If the path to the TLD points to a jar file, then the
     * second element denotes the name of the TLD entry in the jar file.
     * Returns null if the uri is not associated with any tag library 'exposed'
     * in the web application.
     *
     * This method may be called when the scanning is in one of states:
     * 1. Called from jspc script, then a full tld scan is required.
     * 2. The is the first call after servlet initialization, then system jars
     *    that are knwon to have tlds but not listeners need to be scanned.
     * 3. Sebsequent calls, no need to scans.
     */

    @SuppressWarnings("unchecked")
    public String[] getLocation(String uri) throws JasperException {

        if (mappings == null) {
            // Recovering the map done in onStart.
            mappings = (HashMap<String, String[]>) ctxt.getAttribute(
                            Constants.JSP_TLD_URI_TO_LOCATION_MAP);
        }

        if (mappings != null && mappings.get(uri) != null) {
            // if the uri is in, return that, and dont bother to do full scan
            return mappings.get(uri);
        }

        if (! doneScanning) {
            scanListeners = false;
            scanTlds();
            doneScanning = true;
        }
        if (mappings == null) {
            // Should never happend
            return null;
        }
        return mappings.get(uri);
    }

    @SuppressWarnings("unchecked")
    Map<URI, List<String>> getTldMap() {
        /*
         * System jars with tlds may be passed as a special
         * ServletContext attribute
         * Map key: a JarURI
         * Map value: list of tlds in the jar file
         */
        return (Map<URI, List<String>>)
            ctxt.getAttribute("com.sun.appserv.tld.map");
    }

    @SuppressWarnings("unchecked")
    Map<URI, List<String>> getTldListenerMap() {
        /*
         * System jars with tlds that are known to contain a listener, and
         * may be passed as a special ServletContext attribute
         * Map key: a JarURI
         * Map value: list of tlds in the jar file
         */
        return (Map<URI, List<String>>)
            ctxt.getAttribute("com.sun.appserv.tldlistener.map");
    }

    /** 
     * Returns the type of a URI:
     *     ABS_URI
     *     ROOT_REL_URI
     *     NOROOT_REL_URI
     */
    public static int uriType(String uri) {
        if (uri.indexOf(':') != -1) {
            return ABS_URI;
        } else if (uri.startsWith("/")) {
            return ROOT_REL_URI;
        } else {
            return NOROOT_REL_URI;
        }
    }

    /**
     * Scan the all the tlds accessible in the web app.
     * For performance reasons, this is done in two stages.  At servlet
     * initialization time, we only scan the jar files for listeners.  The
     * container passes a list of system jar files that are known to contain
     * tlds with listeners.  The rest of the jar files will be scanned when
     * a JSP page with a tld referenced is compiled.
     */
    private void scanTlds() throws JasperException {

        mappings = new HashMap<String, String[]>();

        // Make a local copy of the system jar cache 
        jarTldCacheLocal.putAll(jarTldCache);

        try {
            processWebDotXml();
            scanJars();
            processTldsInFileSystem("/WEB-INF/");
        } catch (JasperException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JasperException(
                Localizer.getMessage("jsp.error.internal.tldinit"),
                ex);
        }
    }

    /*
     * Populates taglib map described in web.xml.
     */    
    private void processWebDotXml() throws Exception {


        // Skip if we are only looking for listeners
        if (scanListeners) {
            return;
        }

        JspConfigDescriptor jspConfig = ctxt.getJspConfigDescriptor();
        if (jspConfig == null) {
            return;
        }

        for (TaglibDescriptor taglib: jspConfig.getTaglibs()) {

            if (taglib == null) {
                continue;
            }
            String tagUri = taglib.getTaglibURI();
            String tagLoc = taglib.getTaglibLocation();
            if (tagUri == null || tagLoc == null) {
                continue;
            }
            // Ignore system tlds in web.xml, for backward compatibility
            if (systemUris.contains(tagUri)
                        || (!useMyFaces && systemUrisJsf.contains(tagUri))) {
                continue;
            }
            // Save this location if appropriate
            if (uriType(tagLoc) == NOROOT_REL_URI)
                    tagLoc = "/WEB-INF/" + tagLoc;
            String tagLoc2 = null;
            if (tagLoc.endsWith(JAR_FILE_SUFFIX)) {
                tagLoc = ctxt.getResource(tagLoc).toString();
                tagLoc2 = "META-INF/taglib.tld";
            }
            if (log.isLoggable(Level.FINE)) {
                log.fine( "Add tld map from web.xml: " + tagUri + "=>" + tagLoc+ "," + tagLoc2);
            }
            mappings.put(tagUri, new String[] { tagLoc, tagLoc2 });
        }
    }

    /**
     * Scans the given JarURLConnection for TLD files located in META-INF
     * (or a subdirectory of it).  If the scanning in is done as part of the
     * ServletContextInitializer, the listeners in the tlds in this jar file
     * are added to the servlet context, and for any  TLD that has a <uri>
     * element, an implicit map entry is added to the taglib map.
     *
     * @param conn The JarURLConnection to the JAR file to scan
     * @param tldNames the list of tld element to scan. The null value
     *         indicates all the tlds in this case.
     * @param isLocal True if the jar file is under WEB-INF
     *         false otherwise
     */
    private void scanJar(JarURLConnection conn, List<String> tldNames,
                         boolean isLocal)
            throws JasperException {

        String resourcePath = conn.getJarFileURL().toString();
        TldInfo[] tldInfos = jarTldCacheLocal.get(resourcePath);

        // Optimize for most common cases: jars known to NOT have tlds
        if (tldInfos != null && tldInfos.length == 0) {
            return;
        }

        // scan the tld if the jar has not been cached.
        if (tldInfos == null) {
            JarFile jarFile = null;
            ArrayList<TldInfo> tldInfoA = new ArrayList<TldInfo>();
            try {
                conn.setUseCaches(false);
                jarFile = conn.getJarFile();
                if (tldNames != null) {
                    for (String tldName : tldNames) {
                        JarEntry entry = jarFile.getJarEntry(tldName);
                        InputStream stream = jarFile.getInputStream(entry);
                        tldInfoA.add(scanTld(resourcePath, tldName, stream));
                    }
                } else {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (!name.startsWith("META-INF/")) continue;
                        if (!name.endsWith(".tld")) continue;
                        InputStream stream = jarFile.getInputStream(entry);
                        tldInfoA.add(scanTld(resourcePath, name, stream));
                    }
                }
            } catch (IOException ex) {
                if (resourcePath.startsWith(FILE_PROTOCOL) &&
                        !((new File(resourcePath)).exists())) {
                    if (log.isLoggable(Level.WARNING)) {
                        log.log(Level.WARNING,
                            Localizer.getMessage("jsp.warn.nojar",
                                                 resourcePath),
                            ex);
                    }
                } else {
                    throw new JasperException(
                        Localizer.getMessage("jsp.error.jar.io", resourcePath),
                        ex);
                }
            } finally {
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    } catch (Throwable t) {
                        // ignore
                    }
                }
            }
            // Update the jar TLD cache
            tldInfos = tldInfoA.toArray(new TldInfo[tldInfoA.size()]);
            jarTldCacheLocal.put(resourcePath, tldInfos);
            if (!isLocal) {
                // Also update the global cache;
                jarTldCache.put(resourcePath, tldInfos);
            }
        }

        // Iterate over tldinfos to add listeners or to map tldlocations
        for (TldInfo tldInfo: tldInfos) {
            if (scanListeners) {
                addListener(tldInfo, isLocal);
            }
            mapTldLocation(resourcePath, tldInfo, isLocal);
        }
    }

    private void addListener(TldInfo tldInfo, boolean isLocal) {
        String uri = tldInfo.getUri();
        if (!systemUrisJsf.contains(uri)
                    || (isLocal && useMyFaces)
                    || (!isLocal && !useMyFaces)) {
            for (String listenerClassName: tldInfo.getListeners()) {
                if (log.isLoggable(Level.FINE)) {
                    log.fine( "Add tld listener " + listenerClassName);
                }
                ctxt.addListener(listenerClassName);
            }
        }
    }

    private void mapTldLocation(String resourcePath, TldInfo tldInfo,
                                boolean isLocal) {

        String uri = tldInfo.getUri();
        if (uri == null) {
            return;
        }

        if ((isLocal
                // Local tld files override the tlds in the jar files,
                // unless it is in a system jar (except when using myfaces)
                && mappings.get(uri) == null
                && !systemUris.contains(uri)
                && (!systemUrisJsf.contains(uri) || useMyFaces)
            ) ||
            (!isLocal
                // Jars are scanned bottom up, so jars in WEB-INF override
                // thos in the system (except when using myfaces)
                && (mappings.get(uri) == null
                    || systemUris.contains(uri)
                    || (systemUrisJsf.contains(uri) && !useMyFaces)
                   )
            )
           ) {
            String entryName = tldInfo.getEntryName();
            if (log.isLoggable(Level.FINE)) {
                log.fine("Add tld map from tld in " +
                    (isLocal? "WEB-INF": "jar: ") + uri + "=>" +
                    resourcePath + "," + entryName);
            }
            mappings.put(uri, new String[] {resourcePath, entryName});
        }
    }


    /*
     * Searches the filesystem under /WEB-INF for any TLD files, and scans
     * them for <uri> and <listener> elements.
     */
    private void processTldsInFileSystem(String startPath)
            throws JasperException {

        Set dirList = ctxt.getResourcePaths(startPath);
        if (dirList != null) {
            Iterator it = dirList.iterator();
            while (it.hasNext()) {
                String path = (String) it.next();
                if (path.endsWith("/")) {
                    processTldsInFileSystem(path);
                }
                if (!path.endsWith(".tld")) {
                    continue;
                }
                if (path.startsWith("/WEB-INF/tags/")
                        && !path.endsWith("implicit.tld")) {
                    throw new JasperException(
                        Localizer.getMessage(
                                "jsp.error.tldinit.tldInWebInfTags",
                                path));
                }
                InputStream stream = ctxt.getResourceAsStream(path);
                TldInfo tldInfo = scanTld(path, null, stream);
                // Add listeners or to map tldlocations for this TLD
                if (scanListeners) {
                    addListener(tldInfo, true);
                }
                mapTldLocation(path, tldInfo, true);
            }
        }
    }

    /**
     * Scan the given TLD for uri and listeners elements.
     *
     * @param resourcePath the resource path for the jar file or the tld file.
     * @param entryName If the resource path is a jar file, then the name of
     *        the tld file in the jar, else should be null.
     * @param stream The input stream for the tld
     * @return The TldInfo for this tld
     */
    private TldInfo scanTld(String resourcePath, String entryName,
                         InputStream stream)
                throws JasperException {
        try {
            // Parse the tag library descriptor at the specified resource path
            TreeNode tld = new ParserUtils().parseXMLDocument(
                                resourcePath, stream, isValidationEnabled);

            String uri = null;
            TreeNode uriNode = tld.findChild("uri");
            if (uriNode != null) {
                uri = uriNode.getBody();
            }

            ArrayList<String> listeners = new ArrayList<String>();

            Iterator<TreeNode>listenerNodes = tld.findChildren("listener");
            while (listenerNodes.hasNext()) {
                TreeNode listener = listenerNodes.next();
                TreeNode listenerClass = listener.findChild("listener-class");
                if (listenerClass != null) {
                    String listenerClassName = listenerClass.getBody();
                    if (listenerClassName != null) {
                        listeners.add(listenerClassName);
                    }
                }
            }

            return new TldInfo(uri, entryName,
                               listeners.toArray(new String[listeners.size()]));

        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable t) {
                    // do nothing
                }
            }
        }
    }

    /*
     * Scans all JARs accessible to the webapp's classloader and its
     * parent classloaders for TLDs.
     * 
     * The list of JARs always includes the JARs under WEB-INF/lib, as well as
     * all shared JARs in the classloader delegation chain of the webapp's
     * classloader.
     *
     * Considering JARs in the classloader delegation chain constitutes a
     * Tomcat-specific extension to the TLD search
     * order defined in the JSP spec. It allows tag libraries packaged as JAR
     * files to be shared by web applications by simply dropping them in a 
     * location that all web applications have access to (e.g.,
     * <CATALINA_HOME>/common/lib).
     */
    private void scanJars() throws Exception {

        ClassLoader webappLoader =
            Thread.currentThread().getContextClassLoader();
        ClassLoader loader = webappLoader;

        Map<URI, List<String>> tldMap;
        if (scanListeners) {
            tldMap = getTldListenerMap();
        } else {
            tldMap= getTldMap();
        }

        Boolean isStandalone = (Boolean)
            ctxt.getAttribute(IS_STANDALONE_ATTRIBUTE_NAME);

        while (loader != null) {
            if (loader instanceof URLClassLoader) {
                boolean isLocal = (loader == webappLoader);
                URL[] urls = ((URLClassLoader) loader).getURLs();
                List<String> extraJars = new ArrayList<String>();

                for (int i=0; i<urls.length; i++) {
                    URLConnection conn = urls[i].openConnection();
                    JarURLConnection jconn = null;
                    if (conn instanceof JarURLConnection) {
                        jconn = (JarURLConnection) conn;
                    } else {
                        String urlStr = urls[i].toString();
                        if (urlStr.startsWith(FILE_PROTOCOL)
                                && urlStr.endsWith(JAR_FILE_SUFFIX)) {
                            URL jarURL = new URL("jar:" + urlStr + "!/");
                            jconn = (JarURLConnection) jarURL.openConnection();
                        }
                    }
                    if (jconn != null) {
                        if (isLocal) {
                            // For local jars, collect the jar files in the
                            // Manifest Class-Path, to be scanned later.
                            addManifestClassPath(null, extraJars, jconn);
                        }
                        scanJar(jconn, null, isLocal);
                    }
                }

                // Scan the jars collected from manifest class-path.  Expand
                // the list to include jar files from their manifest classpath.
                if (extraJars.size() > 0) {
                    List<String> newJars;
                    do {
                        newJars = new ArrayList<String>();
                        for (String jar: extraJars) {
                            URL jarURL = new URL("jar:" + jar + "!/");
                            JarURLConnection jconn =
                                    (JarURLConnection) jarURL.openConnection();
                            if (addManifestClassPath(extraJars,newJars,jconn)){
                                scanJar(jconn, null, true);
                            }
                        }
                        extraJars.addAll(newJars);
                    } while (newJars.size() != 0);
                }
            }

            if (tldMap != null && isStandalone != null) {
                if (isStandalone.booleanValue()) {
                    break;
                } else {
                    if (EAR_LIB_CLASSLOADER.equals(
                            loader.getClass().getName())) {
                        // Do not walk up classloader delegation chain beyond
                        // EarLibClassLoader
                        break;
                    }
                }
            }

            loader = loader.getParent();
        }

        if (tldMap != null) {
            for (URI uri : tldMap.keySet()) {
                URL jarURL = new URL("jar:" + uri.toString() + "!/");
                scanJar((JarURLConnection)jarURL.openConnection(),
                        tldMap.get(uri), false);
            }
        }
    }

    /*
     * Add the jars in the manifest Class-Path to the list "jars"
     * @param scannedJars List of jars that has been previously scanned
     * @param newJars List of jars from Manifest Class-Path
     * @return true is the jar file exists
     */
    private boolean addManifestClassPath(List<String> scannedJars,
                                         List<String> newJars,
                                         JarURLConnection jconn){

        Manifest manifest;
        try {
            manifest = jconn.getManifest();
        } catch (IOException ex) {
            // Maybe non existing jar, ignored
            return false;
        }

        String file = jconn.getJarFileURL().toString();
        if (! file.contains("WEB-INF")) {
            // Only jar in WEB-INF is considered here
            return true;
        }

        if (manifest == null)
            return true;

        java.util.jar.Attributes attrs = manifest.getMainAttributes();
        String cp = (String) attrs.getValue("Class-Path");
        if (cp == null)
            return true;

        String[] paths = cp.split(" ");
        int lastIndex = file.lastIndexOf('/');
        if (lastIndex < 0) {
            lastIndex = file.lastIndexOf('\\');
        }
        String baseDir = "";
        if (lastIndex > 0) {
            baseDir = file.substring(0, lastIndex+1);
        }
        for (String path: paths) {
            String p;
            if (path.startsWith("/") || path.startsWith("\\")){
                p = "file:" + path;
            } else {
                p = baseDir + path;
            }
            if ((scannedJars == null || !scannedJars.contains(p)) &&
                !newJars.contains(p) ){
                     newJars.add(p);
            }
        }
        return true;
    }

    static class TldInfo {
        private String entryName;        // The name of the tld file
        private String uri;              // The uri name for the tld
        private String[] listeners;      // The listeners in the tld

        public TldInfo(String uri, String entryName, String[] listeners) {
            this.uri = uri;
            this.entryName = entryName;
            this.listeners = listeners;
        }

        public String getEntryName() {
            return entryName;
        }

        public String getUri() {
            return uri;
        }

        public String[] getListeners() {
            return listeners;
        }
    }
}
