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
package net.hasor.db.dal.repository.parser;
import net.hasor.db.dal.*;
import net.hasor.db.dal.dynamic.DynamicParser;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.repository.RepositoryItemParser;
import net.hasor.db.dal.repository.config.QueryType;
import net.hasor.db.dal.repository.config.StatementType;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 解析动态 SQL 配置（注解形式）
 * @version : 2021-06-05
 * @author 赵永春 (zyc@byshell.org)
 */
public class MethodRepositoryItemParser extends DynamicParser implements RepositoryItemParser<Method> {
    private final XmlStringRepositoryItemParser xmlStringParser = new XmlStringRepositoryItemParser();

    public static boolean matchType(Class<?> dalType) {
        if (!dalType.isInterface()) {
            return false;
        }
        Method[] dalTypeMethods = dalType.getMethods();
        for (Method method : dalTypeMethods) {
            if (matchMethod(method)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchMethod(Method dalMethod) {
        if (dalMethod.getDeclaringClass() == Object.class) {
            return false;
        }
        for (Annotation anno : dalMethod.getAnnotations()) {
            if (matchAnnotation(anno)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchAnnotation(Annotation annotation) {
        return annotation instanceof Insert     //
                || annotation instanceof Delete //
                || annotation instanceof Update //
                || annotation instanceof Query  //
                || annotation instanceof Callable;
    }

    protected QueryType getQueryType(QueryType queryType, StatementType statementType) {
        if (statementType == StatementType.Callable) {
            return QueryType.Callable;
        } else {
            return queryType;
        }
    }

    protected DynamicSql createDynamicSql(Annotation annotation, Class<?> resultType, String parameterType) throws ParserConfigurationException, IOException, SAXException {
        String dynamicSqlBody = "";
        QueryType queryType = null;
        Map<String, String> dynamicSqlAttribute = new HashMap<>();
        dynamicSqlAttribute.put("parameterType", parameterType);
        //
        if (annotation instanceof Insert) {
            queryType = getQueryType(QueryType.Insert, ((Insert) annotation).statementType());
            dynamicSqlBody = ((Insert) annotation).value();
            dynamicSqlAttribute.put("statementType", ((Insert) annotation).statementType().getTypeName());
            dynamicSqlAttribute.put("timeout", String.valueOf(((Insert) annotation).timeout()));
        } else if (annotation instanceof Delete) {
            queryType = getQueryType(QueryType.Delete, ((Delete) annotation).statementType());
            dynamicSqlBody = ((Delete) annotation).value();
            dynamicSqlAttribute.put("statementType", ((Delete) annotation).statementType().getTypeName());
            dynamicSqlAttribute.put("timeout", String.valueOf(((Delete) annotation).timeout()));
        } else if (annotation instanceof Update) {
            queryType = getQueryType(QueryType.Update, ((Update) annotation).statementType());
            dynamicSqlBody = ((Update) annotation).value();
            dynamicSqlAttribute.put("statementType", ((Update) annotation).statementType().getTypeName());
            dynamicSqlAttribute.put("timeout", String.valueOf(((Update) annotation).timeout()));
        } else if (annotation instanceof Query) {
            queryType = getQueryType(QueryType.Query, ((Query) annotation).statementType());
            dynamicSqlBody = ((Query) annotation).value();
            dynamicSqlAttribute.put("statementType", ((Query) annotation).statementType().getTypeName());
            dynamicSqlAttribute.put("timeout", String.valueOf(((Query) annotation).timeout()));
            dynamicSqlAttribute.put("fetchSize", String.valueOf(((Query) annotation).fetchSize()));
            dynamicSqlAttribute.put("resultSetType", ((Query) annotation).resultSetType().getTypeName());
            dynamicSqlAttribute.put("multipleResult", ((Query) annotation).multipleResult().getTypeName());
            dynamicSqlAttribute.put("resultDataQL", ((Query) annotation).resultDataQL());
            dynamicSqlAttribute.put("resultType", resultType.getName());
        } else if (annotation instanceof Callable) {
            queryType = getQueryType(QueryType.Callable, ((Callable) annotation).statementType());
            dynamicSqlBody = ((Callable) annotation).value();
            dynamicSqlAttribute.put("statementType", ((Callable) annotation).statementType().getTypeName());
            dynamicSqlAttribute.put("timeout", String.valueOf(((Callable) annotation).timeout()));
            dynamicSqlAttribute.put("fetchSize", String.valueOf(((Callable) annotation).fetchSize()));
            dynamicSqlAttribute.put("resultSetType", ((Callable) annotation).resultSetType().getTypeName());
            dynamicSqlAttribute.put("multipleResult", ((Callable) annotation).multipleResult().getTypeName());
            dynamicSqlAttribute.put("resultDataQL", ((Callable) annotation).resultDataQL());
            dynamicSqlAttribute.put("resultType", resultType.getName());
        } else {
            return null;
        }
        //
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<" + queryType.getXmlTag());
        dynamicSqlAttribute.forEach((key, value) -> {
            String xmlValue = StringUtils.isNotBlank(value) ? value.replace("\"", "&quot;") : "";
            xmlBuilder.append(" " + key + " =\"" + xmlValue + "\"");
        });
        xmlBuilder.append(">");
        xmlBuilder.append(dynamicSqlBody);
        xmlBuilder.append("</" + queryType.getXmlTag() + ">");
        //
        return this.xmlStringParser.parseSqlConfig(xmlBuilder.toString());
    }

    public DynamicSql parseSqlConfig(Method dalMethod) {
        Objects.requireNonNull(dalMethod, "dalMethod is null.");
        Class<?> returnType = dalMethod.getReturnType();
        //
        String parameterType = null;
        if (dalMethod.getParameterCount() == 1) {
            parameterType = dalMethod.getParameterTypes()[0].getName();
        }
        //
        for (Annotation anno : dalMethod.getAnnotations()) {
            if (matchAnnotation(anno)) {
                try {
                    return createDynamicSql(anno, returnType, parameterType);
                } catch (ParserConfigurationException | IOException | SAXException e) {
                    throw ExceptionUtils.toRuntime(e);
                }
            }
        }
        return null;
    }
}
