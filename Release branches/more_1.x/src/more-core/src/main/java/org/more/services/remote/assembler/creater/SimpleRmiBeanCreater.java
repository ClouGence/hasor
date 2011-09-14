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
package org.more.services.remote.assembler.creater;
import java.rmi.Remote;
import org.more.services.remote.assembler.AbstractRmiBeanCreater;
/**
 * Ò»¸ö¼òµ¥ÊµÏÖ¡£
 * @version : 2011-8-17
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public class SimpleRmiBeanCreater extends AbstractRmiBeanCreater {
    private Class<?> objectType = null;
    //
    public SimpleRmiBeanCreater(Class<?> objectType) throws Throwable {
        this.objectType = objectType;
    };
    public Class<?>[] getFaces() throws Throwable {
        return this.getRemoteFaces(this.objectType.getInterfaces());
    };
    public Remote create() throws Throwable {
        //1.ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
        Object obj = this.createObject(this.objectType);
        //2.ï¿½ï¿½ï¿½Remoteï¿½ï¿½ï¿½ï¿½
        return super.getRemoteByFaces(obj, this.getFaces());
    };
    /**´´½¨¶ÔÏó*/
    protected Object createObject(Class<?> objectType) throws Throwable {
        return objectType.newInstance();
    };
};