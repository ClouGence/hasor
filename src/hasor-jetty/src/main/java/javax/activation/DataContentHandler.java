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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @version $Rev: 467742 $ $Date: 2011/05/07 04:32:39 $
 */
public interface DataContentHandler {
    public DataFlavor[] getTransferDataFlavors();

    public Object getTransferData(DataFlavor df, DataSource ds) throws UnsupportedFlavorException, IOException;

    public Object getContent(DataSource ds) throws IOException;

    public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException;
}