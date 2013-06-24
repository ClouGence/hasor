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
package org.platform.event.support;
import org.platform.context.AppContext;
/**
 * 
 * @version : 2013-5-8
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
interface ManagerLife {
    /**≥ı ºªØManager*/
    public void initLife(AppContext appContext);
    /**œ˙ªŸManager*/
    public void destroyLife(AppContext appContext);
}