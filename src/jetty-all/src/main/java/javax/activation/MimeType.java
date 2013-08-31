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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


/**
 * @version $Rev: 467742 $ $Date: 2011/05/07 04:32:39 $
 */
public class MimeType implements Externalizable {
    private static final String SPECIALS = "()<>@,;:\\\"/[]?=";

    static boolean isSpecial(char c) {
        return Character.isWhitespace(c) || Character.isISOControl(c) || SPECIALS.indexOf(c) != -1;
    }

    private String primaryType = "application";
    private String subType = "*";
    private final MimeTypeParameterList parameterList = new MimeTypeParameterList();;

    public MimeType() {
    }

    public MimeType(String rawdata) throws MimeTypeParseException {
        parseMimeType(rawdata);
    }

    public MimeType(String primary, String sub) throws MimeTypeParseException {
        setPrimaryType(primary);
        setSubType(sub);
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(String primary) throws MimeTypeParseException {
        primaryType = parseToken(primary);
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String sub) throws MimeTypeParseException {
        subType = parseToken(sub);
    }

    public MimeTypeParameterList getParameters() {
        return parameterList;
    }

    public String getParameter(String name) {
        return parameterList.get(name);
    }

    public void setParameter(String name, String value) {
        parameterList.set(name, value);
    }

    public void removeParameter(String name) {
        parameterList.remove(name);
    }

    public String toString() {
        return getBaseType() + parameterList.toString();
    }

    public String getBaseType() {
        return getPrimaryType() + '/' + getSubType();
    }

    public boolean match(MimeType type) {
        if (!primaryType.equals(type.primaryType)) return false;
        if ("*".equals(subType)) return true;
        if ("*".equals(type.subType)) return true;
        return subType.equals(type.subType);
    }

    public boolean match(String rawdata) throws MimeTypeParseException {
        return match(new MimeType(rawdata));
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(toString());
        out.flush();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        try {
            parseMimeType(in.readUTF());
        } catch (MimeTypeParseException mtpex) {
            throw new IOException(mtpex.getMessage());
        }
    }

    private void parseMimeType(String rawData) throws MimeTypeParseException {
        int index = rawData.indexOf('/');
        if (index == -1) {
            throw new MimeTypeParseException("Expected '/'");
        }
        setPrimaryType(rawData.substring(0, index));
        int index2 = rawData.indexOf(';', index+1);
        if (index2 == -1) {
            setSubType(rawData.substring(index+1));
        } else {
            setSubType(rawData.substring(index+1, index2));
            parameterList.parse(rawData.substring(index2));
        }
    }

    private static String parseToken(String tokenString) throws MimeTypeParseException {
        tokenString = tokenString.trim();
        for (int i=0; i < tokenString.length(); i++) {
            char c = tokenString.charAt(i);
            if (isSpecial(c)) {
                throw new MimeTypeParseException("Special '" + c + "' not allowed in token");
            }
        }
        return tokenString;
    }
}
