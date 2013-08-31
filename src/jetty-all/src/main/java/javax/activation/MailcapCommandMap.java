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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @version $Rev: 467742 $ $Date: 2011/05/07 04:32:39 $
 */
public class MailcapCommandMap extends CommandMap {
    private final Map mimeTypes = new HashMap();
    private final Map preferredCommands = new HashMap();
    private final Map allCommands = new HashMap();
    // the unparsed commands from the mailcap file.
    private final Map nativeCommands = new HashMap();
    // commands identified as fallbacks...these are used last, and also used as wildcards.
    private final Map fallbackCommands = new HashMap();
    private URL url;

    public MailcapCommandMap() {
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        // process /META-INF/mailcap.default
        try {
            InputStream is = MailcapCommandMap.class.getResourceAsStream("/META-INF/mailcap.default");
            if (is != null) {
                try {
                    parseMailcap(is);
                } finally {
                    is.close();
                }
            }
        } catch (IOException e) {
            // ignore
        }

        // process /META-INF/mailcap resources
        try {
            Enumeration e = contextLoader.getResources("META-INF/mailcap");
            while (e.hasMoreElements()) {
                url = ((URL) e.nextElement());
                try {
                    InputStream is = url.openStream();
                    try {
                        parseMailcap(is);
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

        // process ${java.home}/lib/mailcap
        try {
            File file = new File(System.getProperty("java.home"), "lib/mailcap");
            InputStream is = new FileInputStream(file);
            try {
                parseMailcap(is);
            } finally {
                is.close();
            }
        } catch (SecurityException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        }

        // process ${user.home}/lib/mailcap
        try {
            File file = new File(System.getProperty("user.home"), ".mailcap");
            InputStream is = new FileInputStream(file);
            try {
                parseMailcap(is);
            } finally {
                is.close();
            }
        } catch (SecurityException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        }
    }

    public MailcapCommandMap(String fileName) throws IOException {
        this();
        FileReader reader = new FileReader(fileName);
        try {
            parseMailcap(reader);
        } finally {
            reader.close();
        }
    }

    public MailcapCommandMap(InputStream is) {
        this();
        parseMailcap(is);
    }

    private void parseMailcap(InputStream is) {
        try {
            parseMailcap(new InputStreamReader(is));
        } catch (IOException e) {
            // spec API means all we can do is swallow this
        }
    }

    void parseMailcap(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            addMailcap(line);
        }
    }

    public synchronized void addMailcap(String mail_cap) {
        int index = 0;
        // skip leading whitespace
        index = skipSpace(mail_cap, index);
        if (index == mail_cap.length() || mail_cap.charAt(index) == '#') {
            return;
        }

        // get primary type
        int start = index;
        index = getToken(mail_cap, index);
        if (start == index) {
            return;
        }
        String mimeType = mail_cap.substring(start, index);

        // skip any spaces after the primary type
        index = skipSpace(mail_cap, index);
        if (index == mail_cap.length() || mail_cap.charAt(index) == '#') {
            return;
        }

        // get sub-type
        if (mail_cap.charAt(index) == '/') {
            index = skipSpace(mail_cap, ++index);
            start = index;
            index = getToken(mail_cap, index);
            mimeType = mimeType + '/' + mail_cap.substring(start, index);
        } else {

            mimeType = mimeType + "/*";
        }

        // we record all mappings using the lowercase version.
        mimeType = mimeType.toLowerCase();

        // skip spaces after mime type
        index = skipSpace(mail_cap, index);

        // expect a ';' to terminate field 1
        if (index == mail_cap.length() || mail_cap.charAt(index) != ';') {
            return;
        }
        // ok, we've parsed the mime text field, now parse the view field.  If there's something
        // there, then we add this to the native text.
        index = skipSpace(mail_cap, index + 1);
        // if the next encountered text is not a ";", then we have a view.  This gets added to the
        // native list.
        if (index == mail_cap.length() || mail_cap.charAt(index) != ';') {
            ArrayList nativeCommandList = (ArrayList)nativeCommands.get(mimeType);

            // if this is the first for this mimetype, create a holder
            if (nativeCommandList == null) {
                nativeCommandList = new ArrayList();
                nativeCommands.put(mimeType, nativeCommandList);
            }

            // now add this as an entry in the list.
            nativeCommandList.add(mail_cap);
            // now skip forward to the next field marker, if any
            index = getMText(mail_cap, index);
        }

        // we don't know which list this will be added to until we finish parsing, as there
        // can be an x-java-fallback-entry parameter that moves this to the fallback list.
        List commandList = new ArrayList();
        // but by default, this is not a fallback.
        boolean fallback = false;

        int fieldNumber = 0;

        // parse fields
        while (index < mail_cap.length() && mail_cap.charAt(index) == ';') {
            index = skipSpace(mail_cap, index + 1);
            start = index;
            index = getToken(mail_cap, index);
            String fieldName = mail_cap.substring(start, index).toLowerCase();
            index = skipSpace(mail_cap, index);
            if (index < mail_cap.length() && mail_cap.charAt(index) == '=') {
                index = skipSpace(mail_cap, index + 1);
                start = index;
                index = getMText(mail_cap, index);
                String value = mail_cap.substring(start, index);
                index = skipSpace(mail_cap, index);
                if (fieldName.startsWith("x-java-") && fieldName.length() > 7) {
                    String command = fieldName.substring(7);
                    value = value.trim();
                    if (command.equals("fallback-entry")) {
                        if (value.equals("true")) {
                            fallback = true;
                        }
                    }
                    else {
                        // create a CommandInfo item and add it the accumulator
                        CommandInfo info = new CommandInfo(command, value);
                        commandList.add(info);
                    }
                }
            }
        }
        addCommands(mimeType, commandList, fallback);
    }

    /**
     * Add a parsed list of commands to the appropriate command list.
     *
     * @param mimeType The mimeType name this is added under.
     * @param commands A List containing the command information.
     * @param fallback The target list identifier.
     */
    private void addCommands(String mimeType, List commands, boolean fallback) {
        // add this to the mimeType set
        mimeTypes.put(mimeType, mimeType);
        // the target list changes based on the type of entry.
        Map target = fallback ? fallbackCommands : preferredCommands;

        // now process
        for (Iterator i = commands.iterator(); i.hasNext();) {
            CommandInfo info = (CommandInfo)i.next();
            addCommand(target, mimeType, info);
            // if this is not a fallback position, then this to the allcommands list.
            if (!fallback) {
                List cmdList = (List) allCommands.get(mimeType);
                if (cmdList == null) {
                    cmdList = new ArrayList();
                    allCommands.put(mimeType, cmdList);
                }
                cmdList.add(info);
            }
        }
    }


    /**
     * Add a command to a target command list (preferred or fallback).
     *
     * @param commandList
     *                 The target command list.
     * @param mimeType The MIME type the command is associated with.
     * @param command  The command information.
     */
    private void addCommand(Map commandList, String mimeType, CommandInfo command) {

        Map commands = (Map) commandList.get(mimeType);
        if (commands == null) {
            commands = new HashMap();
            commandList.put(mimeType, commands);
        }
        commands.put(command.getCommandName(), command);
    }


    private int skipSpace(String s, int index) {
        while (index < s.length() && Character.isWhitespace(s.charAt(index))) {
            index++;
        }
        return index;
    }

    private int getToken(String s, int index) {
        while (index < s.length() && s.charAt(index) != '#' && !MimeType.isSpecial(s.charAt(index))) {
            index++;
        }
        return index;
    }

    private int getMText(String s, int index) {
        while (index < s.length()) {
            char c = s.charAt(index);
            if (c == '#' || c == ';' || Character.isISOControl(c)) {
                return index;
            }
            if (c == '\\') {
                index++;
                if (index == s.length()) {
                    return index;
                }
            }
            index++;
        }
        return index;
    }

    public synchronized CommandInfo[] getPreferredCommands(String mimeType) {
        // get the mimetype as a lowercase version.
        mimeType = mimeType.toLowerCase();

        Map commands = (Map) preferredCommands.get(mimeType);
        if (commands == null) {
            commands = (Map) preferredCommands.get(getWildcardMimeType(mimeType));
        }

        Map fallbackCommands = getFallbackCommands(mimeType);

        // if we have fall backs, then we need to merge this stuff.
        if (fallbackCommands != null) {
            // if there's no command list, we can just use this as the master list.
            if (commands == null) {
                commands = fallbackCommands;
            }
            else {
                // merge the two lists.  The ones in the commands list will take precedence.
                commands = mergeCommandMaps(commands, fallbackCommands);
            }
        }

        // now convert this into an array result.
        if (commands == null) {
            return new CommandInfo[0];
        }
        return (CommandInfo[]) commands.values().toArray(new CommandInfo[commands.size()]);
    }

    private Map getFallbackCommands(String mimeType) {
        Map commands = (Map) fallbackCommands.get(mimeType);

        // now we also need to search this as if it was a wildcard.  If we get a wildcard hit,
        // we have to merge the two lists.
        Map wildcardCommands = (Map)fallbackCommands.get(getWildcardMimeType(mimeType));
        // no wildcard version
        if (wildcardCommands == null) {
            return commands;
        }
        // we need to merge these.
        return mergeCommandMaps(commands, wildcardCommands);
    }


    private Map mergeCommandMaps(Map main, Map fallback) {
        // create a cloned copy of the second map.  We're going to use a PutAll operation to
        // overwrite any duplicates.
        Map result = new HashMap(fallback);
        result.putAll(main);

        return result;
    }

    public synchronized CommandInfo[] getAllCommands(String mimeType) {
        mimeType = mimeType.toLowerCase();
        List exactCommands = (List) allCommands.get(mimeType);
        if (exactCommands == null) {
            exactCommands = Collections.EMPTY_LIST;
        }
        List wildCommands = (List) allCommands.get(getWildcardMimeType(mimeType));
        if (wildCommands == null) {
            wildCommands = Collections.EMPTY_LIST;
        }

        Map fallbackCommands = getFallbackCommands(mimeType);
        if (fallbackCommands == null) {
            fallbackCommands = Collections.EMPTY_MAP;
        }


        CommandInfo[] result = new CommandInfo[exactCommands.size() + wildCommands.size() + fallbackCommands.size()];
        int j = 0;
        for (int i = 0; i < exactCommands.size(); i++) {
            result[j++] = (CommandInfo) exactCommands.get(i);
        }
        for (int i = 0; i < wildCommands.size(); i++) {
            result[j++] = (CommandInfo) wildCommands.get(i);
        }

        for (Iterator i = fallbackCommands.keySet().iterator(); i.hasNext();) {
            result[j++] = (CommandInfo) fallbackCommands.get((String)i.next());
        }
        return result;
    }

    public synchronized CommandInfo getCommand(String mimeType, String cmdName) {
        mimeType = mimeType.toLowerCase();
        // strip any parameters from the supplied mimeType
        int i = mimeType.indexOf(';');
        if (i != -1) {
            mimeType = mimeType.substring(0, i).trim();
        }

        // search for an exact match
        Map commands = (Map) preferredCommands.get(mimeType);
        if (commands == null) {
            // then a wild card match
            commands = (Map) preferredCommands.get(getWildcardMimeType(mimeType));
            if (commands == null) {
                // then fallback searches, both standard and wild card.
                commands = (Map) fallbackCommands.get(mimeType);
                if (commands == null) {
                    commands = (Map) fallbackCommands.get(getWildcardMimeType(mimeType));
                }
                if (commands == null) {
                    return null;
                }
            }
        }
        return (CommandInfo) commands.get(cmdName.toLowerCase());
    }

    private String getWildcardMimeType(String mimeType) {
        int i = mimeType.indexOf('/');
        if (i == -1) {
            return mimeType + "/*";
        } else {
            return mimeType.substring(0, i + 1) + "*";
        }
    }

    public synchronized DataContentHandler createDataContentHandler(String mimeType) {

        CommandInfo info = getCommand(mimeType, "content-handler");
        if (info == null) {
            return null;
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        try {
            return (DataContentHandler) cl.loadClass(info.getCommandClass()).newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InstantiationException e) {
            return null;
        }
    }

    /**
     * Get all MIME types known to this command map.
     *
     * @return A String array of the MIME type names.
     */
    public synchronized String[] getMimeTypes() {
        ArrayList types = new ArrayList(mimeTypes.values());
        return (String[])types.toArray(new String[types.size()]);
    }

    /**
     * Return the list of raw command strings parsed
     * from the mailcap files for a given mimeType.
     *
     * @param mimeType The target mime type
     *
     * @return A String array of the raw command strings.  Returns
     *         an empty array if the mimetype is not currently known.
     */
    public synchronized String[] getNativeCommands(String mimeType) {
        ArrayList commands = (ArrayList)nativeCommands.get(mimeType.toLowerCase());
        if (commands == null) {
            return new String[0];
        }
        return (String[])commands.toArray(new String[commands.size()]);
    }
}
