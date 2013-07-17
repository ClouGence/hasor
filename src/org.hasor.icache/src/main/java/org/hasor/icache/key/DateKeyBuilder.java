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
package org.hasor.icache.key;
import java.util.Date;
import org.hasor.icache.KeyBuilderDefine;
/**
 * 
 * @version : 2013-4-27
 * @author 赵永春 (zyc@byshell.org)
 */
@KeyBuilderDefine(value = Date.class)
public class DateKeyBuilder extends AbstractKeyBuilder {
    /**获取参数的序列化标识码。*/
    public String serializeKey(Object arg) {
        Date date = (Date) arg;
        return String.valueOf(date.getTime());
    }
}