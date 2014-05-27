/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package javax.activation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.net.URL;

/**
 * @version $Rev: 467742 $ $Date: 2011/05/07 04:32:39 $
 */
public class MimetypesFileTypeMap extends FileTypeMap {
    private static final String DEFAULT_TYPE = "application/octet-stream";

    private final Map types = new HashMap();

    public MimetypesFileTypeMap() {
        // defaults from /META-INF/mimetypes.default
        try {
            InputStream is = MimetypesFileTypeMap.class.getResourceAsStream("/META-INF/mimetypes.default");
            if (is != null) {
                try {
                    loadStream(is);
                } finally {
                    is.close();
                }
            }
        } catch (IOException e) {
            // ignore
        }

        // defaults from resources called /META-INF/mime.types
        try {
            ClassLoader cl = MimetypesFileTypeMap.class.getClassLoader();
            Enumeration e = cl.getResources("/META-INF/mime.types");
            while (e.hasMoreElements()) {
                URL url = (URL) e.nextElement();
                try {
                    InputStream is = url.openStream();
                    try {
                        loadStream(is);
                    } finally {
                        is.close();
                    }
                } catch (IOException e1) {
                    continue;
                }
            }
        } catch (SecurityException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        }

        // defaults from ${java.home}/lib/mime.types
        try {
            File file = new File(System.getProperty("java.home"), "lib/mime.types");
            InputStream is = new FileInputStream(file);
            try {
                loadStream(is);
            } finally {
                is.close();
            }
        } catch (SecurityException e) {
            // ignore
        } catch (FileNotFoundException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        }

        // defaults from ${user.home}/.mime.types
        try {
            File file = new File(System.getProperty("user.home"), ".mime.types");
            InputStream is = new FileInputStream(file);
            try {
                loadStream(is);
            } finally {
                is.close();
            }
        } catch (SecurityException e) {
            // ignore
        } catch (FileNotFoundException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        }
    }

    public MimetypesFileTypeMap(String mimeTypeFileName) throws IOException {
        this();
        BufferedReader reader = new BufferedReader(new FileReader(mimeTypeFileName));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                addMimeTypes(line);
            }
            reader.close();
        } catch (IOException e) {
            try {
                reader.close();
            } catch (IOException e1) {
                // ignore to allow original cause through
            }
            throw e;
        }
    }

    public MimetypesFileTypeMap(InputStream is) {
        this();
        try {
            loadStream(is);
        } catch (IOException e) {
            // ignore as the spec's signature says we can't throw IOException - doh!
        }
    }

    private void loadStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            addMimeTypes(line);
        }
    }

    public synchronized void addMimeTypes(String mime_types) {
        int hashPos = mime_types.indexOf('#');
        if (hashPos != -1) {
            mime_types = mime_types.substring(0, hashPos);
        }
        StringTokenizer tok = new StringTokenizer(mime_types);
        if (!tok.hasMoreTokens()) {
            return;
        }
        String contentType = tok.nextToken();
        while (tok.hasMoreTokens()) {
            String fileType = tok.nextToken();
            types.put(fileType, contentType);
        }
    }

    public String getContentType(File f) {
        return getContentType(f.getName());
    }

    public synchronized String getContentType(String filename) {
        int index = filename.lastIndexOf('.');
        if (index == -1 || index == filename.length()-1) {
            return DEFAULT_TYPE;
        }
        String fileType = filename.substring(index + 1);
        String contentType = (String) types.get(fileType);
        return contentType == null ? DEFAULT_TYPE : contentType;
    }
}
