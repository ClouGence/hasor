/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.tconsole.launcher.hosts;
import net.hasor.core.AppContext;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;

/**
 * 继承自 HostTelService 通过 PipedWriter 机制来简化 HostTelService 的操作。
 * @version : 2019年10月23日
 * @author 赵永春 (zyc@hasor.net)
 */
public class PipedHostTelService extends HostTelService {
    private final PipedWriter inDataWriter = new PipedWriter();

    public PipedHostTelService(Writer outDataWriter) throws IOException {
        this(null, outDataWriter);
    }

    public PipedHostTelService(AppContext appContext, Writer outDataWriter) throws IOException {
        super(appContext);
        super.initConstructor(new PipedReader(this.inDataWriter), outDataWriter);
    }

    public Writer getInDataWriter() {
        return this.inDataWriter;
    }

    public void sendCommand(String message) throws IOException {
        if (!this.isInit()) {
            throw new IllegalStateException("the container is not started yet.");
        }
        this.getInDataWriter().write(message + "\n");
    }
}