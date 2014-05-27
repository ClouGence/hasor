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
public interface XAResource {
    int TMENDRSCAN = 8388608;
    int TMFAIL = 536870912;
    int TMJOIN = 2097152;
    int TMNOFLAGS = 0;
    int TMONEPHASE = 1073741824;
    int TMRESUME = 134217728;
    int TMSTARTRSCAN = 16777216;
    int TMSUCCESS = 67108864;
    int TMSUSPEND = 33554432;
    int XA_RDONLY = 3;
    int XA_OK = 0;

    void commit(Xid xid, boolean onePhase) throws XAException;

    void end(Xid xid, int flags) throws XAException;

    void forget(Xid xid) throws XAException;

    int getTransactionTimeout() throws XAException;

    boolean isSameRM(XAResource xaResource) throws XAException;

    int prepare(Xid xid) throws XAException;

    Xid[] recover(int flag) throws XAException;

    void rollback(Xid xid) throws XAException;

    boolean setTransactionTimeout(int seconds) throws XAException;

    void start(Xid xid, int flags) throws XAException;
}
