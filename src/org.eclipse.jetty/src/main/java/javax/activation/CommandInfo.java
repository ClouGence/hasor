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

import java.beans.Beans;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @version $Rev: 467742 $ $Date: 2011/05/07 04:32:39 $
 */
public class CommandInfo {
    private final String commandName;
    private final String commandClass;

    /**
     * Constructor for a CommandInfo
     *
     * @param commandName  the command name
     * @param commandClass the name of the command's implementation class
     */
    public CommandInfo(String commandName, String commandClass) {
        this.commandName = commandName;
        this.commandClass = commandClass;
    }

    /**
     * Return the command name.
     *
     * @return the command name
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Return the implementation class name.
     *
     * @return the name of the command's implementation class; may be null
     */
    public String getCommandClass() {
        return commandClass;
    }

    /**
     * Instantiate and return a command JavaBean.
     * The bean is created using Beans.instantiate(loader, commandClass).
     * If the new bean implements CommandObject then its setCommandContext(String, DataHandler)
     * method is called.
     * Otherwise if it implements Externalizable and the supplied DataHandler is not null
     * then its readExternal(ObjectInputStream) method is called with a stream obtained from
     * DataHandler.getInputStream().
     *
     * @param dh a DataHandler that provides the data to be passed to the command
     * @param loader the ClassLoader to be used to instantiate the command
     * @return a new command instance
     * @throws IOException if there was a problem initializing the command
     * @throws ClassNotFoundException if the command class could not be found
     */
    public Object getCommandObject(DataHandler dh, ClassLoader loader) throws IOException, ClassNotFoundException {
        Object bean = Beans.instantiate(loader, commandClass);
        if (bean instanceof CommandObject) {
            ((CommandObject) bean).setCommandContext(commandName, dh);
        } else if (bean instanceof Externalizable && dh != null) {
            ((Externalizable) bean).readExternal(new ObjectInputStream(dh.getInputStream()));
        }
        return bean;
    }
}