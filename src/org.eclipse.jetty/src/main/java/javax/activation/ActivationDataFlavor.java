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
import java.io.InputStream;

/**
 * @version $Rev: 467742 $ $Date: 2011/05/07 04:32:39 $
 */
public class ActivationDataFlavor extends DataFlavor {
    private final Class representationClass;
    private final String mimeType;
    private String humanPresentableName;

    public ActivationDataFlavor(Class representationClass, String mimeType, String humanPresentableName) {
        this.representationClass = representationClass;
        this.mimeType = mimeType;
        this.humanPresentableName = humanPresentableName;
    }

    public ActivationDataFlavor(Class representationClass, String humanPresentableName) {
        this.representationClass = representationClass;
        this.mimeType = "application/x-java-serialized-object";
        this.humanPresentableName = humanPresentableName;
    }

    public ActivationDataFlavor(String mimeType, String humanPresentableName) {
        this.mimeType = mimeType;
        this.representationClass = InputStream.class;
        this.humanPresentableName = humanPresentableName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Class getRepresentationClass() {
        return representationClass;
    }

    public String getHumanPresentableName() {
        return humanPresentableName;
    }

    public void setHumanPresentableName(String humanPresentableName) {
        this.humanPresentableName = humanPresentableName;
    }

    public boolean equals(DataFlavor dataFlavor) {
        return this.isMimeTypeEqual(dataFlavor.getMimeType()) && representationClass == dataFlavor.getRepresentationClass();
    }

    public boolean isMimeTypeEqual(String mimeType) {
        try {
            MimeType thisType = new MimeType(this.mimeType);
            MimeType thatType = new MimeType(mimeType);
            return thisType.match(thatType);
        } catch (MimeTypeParseException e) {
            return false;
        }
    }

    protected String normalizeMimeTypeParameter(String parameterName, String parameterValue) {
        return parameterName + "=" + parameterValue;
    }

    protected String normalizeMimeType(String mimeType) {
        return mimeType;
    }
}