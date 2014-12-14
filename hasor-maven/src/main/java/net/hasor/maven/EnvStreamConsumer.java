/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.hasor.maven;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.StreamConsumer;
public class EnvStreamConsumer implements StreamConsumer {
    public static final String  START_PARSING_INDICATOR = "================================This is the beginning of env parsing================================";
    private Map<String, String> envs                    = new HashMap<String, String>();
    private boolean             startParsing            = false;
    public void consumeLine(String line) {
        if (line.startsWith(START_PARSING_INDICATOR)) {
            this.startParsing = true;
            return;
        }
        if (this.startParsing) {
            String[] tokens = StringUtils.split(line, "=");
            if (tokens.length == 2) {
                envs.put(tokens[0], tokens[1]);
            }
        } else {
            System.out.println(line);
        }
    }
    public Map<String, String> getParsedEnv() {
        return this.envs;
    }
}
