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
package org.more.util;
import java.util.Map;
import org.more.core.ognl.Node;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlContext;
import org.more.core.ognl.OgnlException;
/**
 * 
 * @version : 2012-5-18
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ELUtil {
    public static Map<String, Node> cacheNode = new java.util.Hashtable<String, Node>();
    public static Object evalOGNL(String expressionString, Object root) throws OgnlException {
        Node expressionNode = cacheNode.get(expressionString);
        if (expressionNode == null) {
            expressionNode = (Node) Ognl.parseExpression(expressionString);
            cacheNode.put(expressionString, expressionNode);
        }
        OgnlContext oc = new OgnlContext();
        oc.setCurrentObject(root);
        return expressionNode.getValue(oc, root);
    };
    public static Node parserNode(String expressionString) throws OgnlException {
        Node expressionNode = cacheNode.get(expressionString);
        if (expressionNode == null) {
            expressionNode = (Node) Ognl.parseExpression(expressionString);
            cacheNode.put(expressionString, expressionNode);
        }
        return expressionNode;
    };
}