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
package org.platform.test.service;
import org.platform.api.event.InitEvent;
import org.platform.api.safety.Power;
import org.platform.api.safety.Power.Level;
import org.platform.api.services.IService;
import org.platform.api.services.Service;
/**
 * 
 * @version : 2013-3-26
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
import org.platform.api.services.Service.Access;
@Power(level = Level.PassPolicy)
@Service(value = { "JobService" }, access = Access.Protected)
public class JobService implements IService {
    @Override
    public void start(InitEvent event) {
        // TODO Auto-generated method stub
    }
    @Override
    public void destroyed() {
        // TODO Auto-generated method stub
    }
}
