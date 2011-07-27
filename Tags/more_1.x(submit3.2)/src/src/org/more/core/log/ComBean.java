/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.more.core.log;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
/**
 * ◊ÈΩ®Bean
 * @version 2009-5-13
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class ComBean {
    private Map<String, CommandAttribut> command = new Hashtable<String, CommandAttribut>(0);
    private String                       name;
    // ===============================================================
    public void addCommand(CommandAttribut ca) {
        this.command.put(ca.getName(), ca);
    }
    public void removeCommand(String name) {
        this.command.remove(name);
    }
    public CommandAttribut getCommand(String name) {
        return this.command.get(name);
    }
    public List<CommandAttribut> getCommand() {
        return new ArrayList<CommandAttribut>(this.command.values());
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}