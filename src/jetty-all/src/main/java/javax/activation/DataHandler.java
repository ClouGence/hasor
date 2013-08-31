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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;

/**
 * @version $Rev: 467742 $ $Date: 2011/05/07 04:32:39 $
 */
public class DataHandler implements Transferable {
    private final DataSource ds;
    private final DataFlavor flavor;

    private CommandMap commandMap;
    private DataContentHandler dch;

    public DataHandler(DataSource ds) {
        this.ds = ds;
        this.flavor = new ActivationDataFlavor(ds.getContentType(), null);
    }

    public DataHandler(Object data, String type) {
        this.ds = new ObjectDataSource(data, type);
        this.flavor = new ActivationDataFlavor(data.getClass(), null);
    }

    public DataHandler(URL url) {
        this.ds = new URLDataSource(url);
        this.flavor = new ActivationDataFlavor(ds.getContentType(), null);
    }

    public DataSource getDataSource() {
        return ds;
    }

    public String getName() {
        return ds.getName();
    }

    public String getContentType() {
        return ds.getContentType();
    }

    public InputStream getInputStream() throws IOException {
        return ds.getInputStream();
    }

    public void writeTo(OutputStream os) throws IOException {
        if (ds instanceof ObjectDataSource) {
            ObjectDataSource ods = (ObjectDataSource) ds;
            DataContentHandler dch = getDataContentHandler();
            if (dch == null) {
                throw new UnsupportedDataTypeException(ods.mimeType);
            }
            dch.writeTo(ods.data, ods.mimeType, os);
        } else {
            byte[] buffer = new byte[1024];
            InputStream is = getInputStream();
            try {
                int count;
                while ((count = is.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                }
            } finally {
                is.close();
            }
        }
    }

    public OutputStream getOutputStream() throws IOException {
        return ds.getOutputStream();
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
        return getDataContentHandler().getTransferDataFlavors();
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        DataFlavor[] flavors = getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            DataFlavor dataFlavor = flavors[i];
            if (dataFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        DataContentHandler dch = getDataContentHandler();
        if (dch != null) {
            return dch.getTransferData(flavor, ds);
        } else if (this.flavor.match(flavor)) {
            if (ds instanceof ObjectDataSource) {
                return ((ObjectDataSource) ds).data;
            } else {
                return ds.getInputStream();
            }
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public CommandInfo[] getPreferredCommands() {
        return getCommandMap().getPreferredCommands(ds.getContentType());
    }

    public CommandInfo[] getAllCommands() {
        return getCommandMap().getAllCommands(ds.getContentType());
    }

    public CommandInfo getCommand(String cmdName) {
        return getCommandMap().getCommand(ds.getContentType(), cmdName);
    }

    public Object getContent() throws IOException {
        if (ds instanceof ObjectDataSource) {
            return ((ObjectDataSource) ds).data;
        } else {
            DataContentHandler dch = getDataContentHandler();
            if (dch != null) {
                return dch.getContent(ds);
            } else {
                return ds.getInputStream();
            }
        }
    }

    public Object getBean(CommandInfo cmdinfo) {
        try {
            return cmdinfo.getCommandObject(this, this.getClass().getClassLoader());
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * A local implementation of DataSouce used to wrap an Object and mime-type.
     */
    private class ObjectDataSource implements DataSource {
        private final Object data;
        private final String mimeType;

        public ObjectDataSource(Object data, String mimeType) {
            this.data = data;
            this.mimeType = mimeType;
        }

        public String getName() {
            return null;
        }

        public String getContentType() {
            return mimeType;
        }

        public InputStream getInputStream() throws IOException {
            final DataContentHandler dch = getDataContentHandler();
            if (dch == null) {
                throw new UnsupportedDataTypeException(mimeType);
            }
            final PipedInputStream is = new PipedInputStream();
            final PipedOutputStream os = new PipedOutputStream(is);
            Thread thread = new Thread("DataHandler Pipe Pump") {
                public void run() {
                    try {
                        try {
                            dch.writeTo(data, mimeType, os);
                        } finally {
                            os.close();
                        }
                    } catch (IOException e) {
                        // ignore, per spec - doh!
                    }
                }
            };
            thread.start();
            return is;
        }

        public OutputStream getOutputStream() throws IOException {
            return null;
        }
    }

    public synchronized void setCommandMap(CommandMap commandMap) {
        this.commandMap = commandMap;
        this.dch = null;
    }

    private synchronized CommandMap getCommandMap() {
        return commandMap != null ? commandMap : CommandMap.getDefaultCommandMap();
    }

    /**
     * Search for a DataContentHandler for our mime type.
     * The search is performed by first checking if a global factory has been set using
     * {@link #setDataContentHandlerFactory(DataContentHandlerFactory)};
     * if found then it is called to attempt to create a handler.
     * If this attempt fails, we then call the command map set using {@link #setCommandMap(CommandMap)}
     * (or if that has not been set, the default map returned by {@link CommandMap#getDefaultCommandMap()})
     * to create the handler.
     *
     * The resulting handler is cached until the global factory is changed.
     *
     * @return
     */
    private synchronized DataContentHandler getDataContentHandler() {
        DataContentHandlerFactory localFactory;
        synchronized (DataHandler.class) {
            if (factory != originalFactory) {
                // setDCHF was called - clear our cached copy of the DCH and DCHF
                dch = null;
                originalFactory = factory;
            }
            localFactory = originalFactory;
        }
        if (dch == null) {
            // get the main mime-type portion of the content.
            String mimeType = getMimeType(ds.getContentType());
            if (localFactory != null) {
                dch = localFactory.createDataContentHandler(mimeType);
            }
            if (dch == null) {
                if (commandMap != null) {
                    dch = commandMap.createDataContentHandler(mimeType);
                } else {
                    dch = CommandMap.getDefaultCommandMap().createDataContentHandler(mimeType);
                }
            }
        }
        return dch;
    }

    /**
     * Retrieve the base MIME type from a content type.  This parses
     * the type into its base components, stripping off any parameter
     * information.
     *
     * @param contentType
     *               The content type string.
     *
     * @return The MIME type identifier portion of the content type.
     */
    private String getMimeType(String contentType) {
        try {
            MimeType mimeType = new MimeType(contentType);
            return mimeType.getBaseType();
        } catch (MimeTypeParseException e) {
        }
        return contentType;
    }

    /**
     * This is used to check if the DataContentHandlerFactory has been changed.
     * This is not specified behaviour but this check is required to make this work like the RI.
     */
    private DataContentHandlerFactory originalFactory;

    {
        synchronized (DataHandler.class) {
            originalFactory = factory;
        }
    }

    private static DataContentHandlerFactory factory;

    /**
     * Set the DataContentHandlerFactory to use.
     * If this method has already been called then an Error is raised.
     *
     * @param newFactory the new factory
     * @throws SecurityException if the caller does not have "SetFactory" RuntimePermission
     */
    public static synchronized void setDataContentHandlerFactory(DataContentHandlerFactory newFactory) {
        if (factory != null) {
            throw new Error("javax.activation.DataHandler.setDataContentHandlerFactory has already been defined");
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkSetFactory();
        }
        factory = newFactory;
    }
}
