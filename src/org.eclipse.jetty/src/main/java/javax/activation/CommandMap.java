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

/**
 * @version $Rev: 513099 $ $Date: 2011/05/07 04:32:39 $
 */
public abstract class CommandMap {
    private static CommandMap defaultCommandMap = new MailcapCommandMap();

    /**
     * Return the default CommandMap. If this has not been explictly set
     * using setDefaultCommandMap() then a MailcapCommandMap is returned.
     * @return the default CommandMap
     */
    public static CommandMap getDefaultCommandMap() {
        return defaultCommandMap;
    }

    /**
     * Set the default CommandMap.
     *
     * @param commandMap the new default CommandMap; if null resets to a MailcapCommandMap
     * @throws SecurityException if the caller does not have "SetFactory" RuntimePermission
     */
    public static void setDefaultCommandMap(CommandMap commandMap) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkSetFactory();
        }
        defaultCommandMap = commandMap == null ? new MailcapCommandMap() : commandMap;
    }

    public CommandMap() {
    }

    /**
     * Get the preferred commands for the given
     * mimetype, as modified by the DataSource.  The
     * default implementation is just a delegation to
     * getPreferredCommands(mimeType), but the concrete
     * implementations can define additional behavior.
     *
     * @param mimeType The mimeType name.
     * @param ds       The datasource providing the type object.
     *
     * @return The array of CommandInfo[] objects associated with
     *         this mimeType/DataSource combo.
     */
    public CommandInfo[] getPreferredCommands(String mimeType, DataSource ds) {
        return getPreferredCommands(mimeType);
    }

    /**
     * Get the preferred commands for the given
     * mimetype.  The concrete implementations define the
     * actual behaviour.
     *
     * @param mimeType The mimeType name.
     *
     * @return The array of CommandInfo[] objects associated with
     *         this mimeType combo.
     */
    public abstract CommandInfo[] getPreferredCommands(String mimeType);


    /**
     * Get the entire command set for the given
     * mimetype, as modified by the DataSource.  The
     * default implementation is just a delegation to
     * getAllCommands(mimeType), but the concrete
     * implementations can define additional behavior.
     *
     * @param mimeType The mimeType name.
     * @param ds       The datasource providing the type object.
     *
     * @return The array of CommandInfo[] objects associated with
     *         this mimeType/DataSource combo.
     */
    public CommandInfo[] getAllCommands(String mimeType, DataSource ds) {
        return getAllCommands(mimeType);
    }


    /**
     * Get all available commands for the given
     * mimetype.  The concrete implementations define the
     * actual behaviour.
     *
     * @param mimeType The mimeType name.
     *
     * @return The array of CommandInfo[] objects associated with
     *         this mimeType combo.
     */
    public abstract CommandInfo[] getAllCommands(String mimeType);

    /**
     * Get the default command implementation for a
     * given mimeType/DataSource combo.
     *
     * The default implementation just delegates to
     * getCommand(mimeType, cmdName).  Subclasses may
     * provide more specialized behavior.
     *
     * @param mimeType The name of the mime type
     * @param cmdName  The command action we wish to perform.
     * @param ds       The modifying DataSource.
     *
     * @return A CommandInfo object corresponding to the command
     *         mapping.
     */
    public CommandInfo getCommand(String mimeType, String cmdName, DataSource ds) {
        return getCommand(mimeType, cmdName);
    }

    /**
     * Get the default command implementation for a
     * give mimeType
     *
     * @param mimeType The name of the mime type
     * @param cmdName  The command action we wish to perform.
     *
     * @return A CommandInfo object corresponding to the command
     *         mapping.
     */
    public abstract CommandInfo getCommand(String mimeType, String cmdName);

    /**
     * Locate a DataContentHandler for the given mime
     * type.  The concrete implementations determine
     * how this mapping is performed.
     *
     * @param mimeType The target MIME type.
     * @param ds       The DataSource associated with this request.
     *
     * @return The DataContentHandler for the MIME type.
     */
    public DataContentHandler createDataContentHandler(String mimeType, DataSource ds) {
        return createDataContentHandler(mimeType);
    }

    /**
     * Locate a DataContentHandler for the given mime
     * type.  The concrete implementations determine
     * how this mapping is performed.
     *
     * @param mimeType The target MIME type.
     *
     * @return The DataContentHandler for the MIME type.
     */
    public abstract DataContentHandler createDataContentHandler(String mimeType);

    /**
     * Return all mime types known to this CommandMap, or null if none.
     *
     * @return a String array of all mime types known to this CommandMap
     */
    public String[] getMimeTypes() {
        return null;
    }
}