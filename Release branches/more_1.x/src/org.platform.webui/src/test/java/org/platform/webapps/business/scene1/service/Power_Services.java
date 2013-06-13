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
package org.platform.webapps.business.scene1.service;
import org.platform.security.Power;
import org.platform.security.Power.Level;
/**
 * 
 * @version : 2013-5-2
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class Power_Services {
    @Power(level = Level.Free)
    public String callFree(String bizID, String param) {
        return "Scene1:" + bizID + " ,param=" + param;
    }
    @Power(level = Level.NeedLogin, errorMsg = "call this method need doLogin.")
    public String callLogin(String bizID, String param) {
        return "Scene1:" + bizID + " ,param=" + param;
    }
    @Power(level = Level.NeedAccess, errorMsg = "call this method need Access[BBSPower].", value = { "BBSPower" })
    public String callAccess1(String bizID, String param) {
        return "Scene1:" + bizID + " ,param=" + param;
    }
    @Power(level = Level.NeedAccess, errorMsg = "call this method need Access[AdminPower,BBSPower].", value = { "AdminPower", "BBSPower" })
    public String callAccess2(String bizID, String param) {
        return "Scene1:" + bizID + " ,param=" + param;
    }
}