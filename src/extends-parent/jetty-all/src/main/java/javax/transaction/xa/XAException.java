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

//
// This source code implements specifications defined by the Java
// Community Process. In order to remain compliant with the specification
// DO NOT add / change / or delete method signatures!
//

package javax.transaction.xa;

/**
 * @version $Rev: 467742 $ $Date: 2010/04/07 16:52:45 $
 */
public class XAException extends Exception {
    public static final int XA_RBBASE = 100;
    public static final int XA_RBROLLBACK = 100;
    public static final int XA_RBCOMMFAIL = 101;
    public static final int XA_RBDEADLOCK = 102;
    public static final int XA_RBINTEGRITY = 103;
    public static final int XA_RBOTHER = 104;
    public static final int XA_RBPROTO = 105;
    public static final int XA_RBTIMEOUT = 106;
    public static final int XA_RBTRANSIENT = 107;
    public static final int XA_RBEND = 107;
    public static final int XA_NOMIGRATE = 9;
    public static final int XA_HEURHAZ = 8;
    public static final int XA_HEURCOM = 7;
    public static final int XA_HEURRB = 6;
    public static final int XA_HEURMIX = 5;
    public static final int XA_RETRY = 4;
    public static final int XA_RDONLY = 3;
    public static final int XAER_ASYNC = -2;
    public static final int XAER_RMERR = -3;
    public static final int XAER_NOTA = -4;
    public static final int XAER_INVAL = -5;
    public static final int XAER_PROTO = -6;
    public static final int XAER_RMFAIL = -7;
    public static final int XAER_DUPID = -8;
    public static final int XAER_OUTSIDE = -9;

    public int errorCode;

    public XAException() {
        super();
    }

    public XAException(String message) {
        super(message);
    }

    public XAException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }
}
