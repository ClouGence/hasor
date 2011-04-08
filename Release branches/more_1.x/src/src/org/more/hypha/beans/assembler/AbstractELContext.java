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
package org.more.hypha.beans.assembler;
import org.more.core.ognl.OgnlContext;
import org.more.hypha.ELContext;
import org.more.hypha.ValueExpression;
/**
 * 
 * Date : 2011-4-8
 * @author ’‘”¿¥∫
 */
public class AbstractELContext implements ELContext {
    private OgnlContext ognlContext = null;
    /* this°¢$context°¢$att°¢$beans */
    //this.elContext.put("context", this.getAttribute()); TODO
    //this.elContext.put("this", this);
    public void loadConfig() {
        // TODO Auto-generated method stub
    }
    public ValueExpression createExpression(String elString) {
        // TODO Auto-generated method stub
        return null;
    };
}