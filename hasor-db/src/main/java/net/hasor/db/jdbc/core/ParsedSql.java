/*
 * Copyright 2002-2008 the original author or authors.
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
package net.hasor.db.jdbc.core;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.SqlParameterSource;

import java.sql.SQLException;
import java.util.*;
/**
 * 编译的SQL
 * @version : 2013-10-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class ParsedSql {
    private String       originalSql;
    private int          namedParameterCount;
    private int          unnamedParameterCount;
    private int          totalParameterCount;
    private List<String> parameterNames;
    private List<int[]>  parameterIndexes;
    //
    private ParsedSql(final String originalSql) {
        this.originalSql = originalSql;
    }
    public String getOriginalSql() {
        return this.originalSql;
    }
    public int getNamedParameterCount() {
        return this.namedParameterCount;
    }
    public int getUnnamedParameterCount() {
        return this.unnamedParameterCount;
    }
    public int getTotalParameterCount() {
        return this.totalParameterCount;
    }
    public List<String> getParameterNames() {
        return this.parameterNames;
    }
    public List<int[]> getParameterIndexes() {
        return this.parameterIndexes;
    }
    //
    //
    //
    //
    /**Set of characters that qualify as parameter separators, indicating that a parameter name in a SQL String has ended. */
    private static final char[]   PARAMETER_SEPARATORS = new char[] { '"', '\'', ':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/', '\\', '<', '>', '^' };
    /** Set of characters that qualify as comment or quotes starting characters.*/
    private static final String[] START_SKIP           = new String[] { "'", "\"", "--", "/*" };
    /**Set of characters that at are the corresponding comment or quotes ending characters. */
    private static final String[] STOP_SKIP            = new String[] { "'", "\"", "\n", "*/" };
    //-------------------------------------------------------------------------
    // Core methods used by NamedParameterJdbcTemplate and SqlQuery/SqlUpdate
    //-------------------------------------------------------------------------
    public static ParsedSql getParsedSql(final String originalSql) {
        ParsedSql parSQL = new ParsedSql(originalSql);
        //
        //1.关键参数定义
        List<String> parameterNames = new ArrayList<String>();
        List<int[]> parameterIndexes = new ArrayList<int[]>();
        int namedParameterCount = 0;//带有名字参数的总数
        int unnamedParameterCount = 0;//无名字参数总数
        int totalParameterCount = 0;//参数总数
        //
        //2.分析SQL，提取出SQL中参数信息
        Hasor.assertIsNotNull(originalSql, "SQL must not be null");
        Set<String> namedParameters = new HashSet<String>();
        char[] statement = originalSql.toCharArray();
        int i = 0;
        while (i < statement.length) {
            int skipToPosition = ParsedSql.skipCommentsAndQuotes(statement, i);//从当前为止掠过的长度
            if (i != skipToPosition) {
                if (skipToPosition >= statement.length) {
                    break;
                }
                i = skipToPosition;
            }
            char c = statement[i];
            if (c == ':' || c == '&') {
                int j = i + 1;
                if (j < statement.length && statement[j] == ':' && c == ':') {
                    i = i + 2;// Postgres-style "::" casting operator - to be skipped.
                    continue;
                }
                while (j < statement.length && !ParsedSql.isParameterSeparator(statement[j])) {
                    j++;
                }
                if (j - i > 1) {
                    String parameter = originalSql.substring(i + 1, j);
                    if (!namedParameters.contains(parameter)) {
                        namedParameters.add(parameter);
                        namedParameterCount++;
                    }
                    parameterNames.add(parameter);
                    parameterIndexes.add(new int[] { i, j });//startIndex, endIndex
                    totalParameterCount++;
                }
                i = j - 1;
            } else if (c == '?') {
                unnamedParameterCount++;
                totalParameterCount++;
            }
            i++;
        }
        parSQL.namedParameterCount = namedParameterCount;/*带有名字参数的总数*/
        parSQL.unnamedParameterCount = unnamedParameterCount;/*匿名参数的总数*/
        parSQL.totalParameterCount = totalParameterCount;/*总共参数个数*/
        parSQL.parameterIndexes = parameterIndexes;
        parSQL.parameterNames = parameterNames;
        return parSQL;
    }
    /** Skip over comments and quoted names present in an SQL statement */
    private static int skipCommentsAndQuotes(final char[] statement, final int position) {
        for (int i = 0; i < ParsedSql.START_SKIP.length; i++) {
            if (statement[position] == ParsedSql.START_SKIP[i].charAt(0)) {
                boolean match = true;
                for (int j = 1; j < ParsedSql.START_SKIP[i].length(); j++) {
                    if (!(statement[position + j] == ParsedSql.START_SKIP[i].charAt(j))) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    int offset = ParsedSql.START_SKIP[i].length();
                    for (int m = position + offset; m < statement.length; m++) {
                        if (statement[m] == ParsedSql.STOP_SKIP[i].charAt(0)) {
                            boolean endMatch = true;
                            int endPos = m;
                            for (int n = 1; n < ParsedSql.STOP_SKIP[i].length(); n++) {
                                if (m + n >= statement.length)
                                    return statement.length;// last comment not closed properly
                                if (!(statement[m + n] == ParsedSql.STOP_SKIP[i].charAt(n))) {
                                    endMatch = false;
                                    break;
                                }
                                endPos = m + n;
                            }
                            if (endMatch) {
                                return endPos + 1;// found character sequence ending comment or quote
                            }
                        }
                    }
                    // character sequence ending comment or quote not found
                    return statement.length;
                }
            }
        }
        return position;
    }
    /** Determine whether a parameter name ends at the current position, that is, whether the given character qualifies as a separator. */
    private static boolean isParameterSeparator(final char c) {
        if (Character.isWhitespace(c)) {
            return true;
        }
        for (char separator : ParsedSql.PARAMETER_SEPARATORS) {
            if (c == separator) {
                return true;
            }
        }
        return false;
    }
    //-------------------------------------------------------------------------
    // Core methods used by NamedParameterJdbcTemplate and SqlQuery/SqlUpdate
    //-------------------------------------------------------------------------
    /**生成SQL*/
    public static String buildSql(final ParsedSql parsedSql, final SqlParameterSource paramSource) {
        String originalSql = parsedSql.getOriginalSql();
        List<String> parameterNames = parsedSql.getParameterNames();
        List<int[]> parameterIndexes = parsedSql.getParameterIndexes();
        //
        StringBuilder sqlToUse = new StringBuilder();
        int lastIndex = 0;
        for (int i = 0; i < parameterNames.size(); i++) {
            String paramName = parameterNames.get(i);
            int[] indexes = parameterIndexes.get(i);
            int startIndex = indexes[0];
            int endIndex = indexes[1];
            sqlToUse.append(originalSql.substring(lastIndex, startIndex));
            if (paramSource != null && paramSource.hasValue(paramName)) {
                Object value = paramSource.getValue(paramName);
                if (value instanceof Collection) {
                    Iterator<?> entryIter = ((Collection<?>) value).iterator();
                    int k = 0;
                    while (entryIter.hasNext()) {
                        if (k > 0) {
                            sqlToUse.append(", ");
                        }
                        k++;
                        Object entryItem = entryIter.next();
                        if (entryItem instanceof Object[]) {
                            Object[] expressionList = (Object[]) entryItem;
                            sqlToUse.append("(");
                            for (int m = 0; m < expressionList.length; m++) {
                                if (m > 0) {
                                    sqlToUse.append(", ");
                                }
                                sqlToUse.append("?");
                            }
                            sqlToUse.append(")");
                        } else {
                            sqlToUse.append("?");
                        }
                    }
                } else {
                    sqlToUse.append("?");
                }
            } else {
                sqlToUse.append("?");
            }
            lastIndex = endIndex;
        }
        sqlToUse.append(originalSql.substring(lastIndex, originalSql.length()));
        return sqlToUse.toString();
    }
    /**生成Values*/
    public static Object[] buildSqlValues(final ParsedSql parsedSql, final SqlParameterSource paramSource) throws SQLException {
        String originalSql = parsedSql.getOriginalSql();
        List<String> parameterNames = parsedSql.getParameterNames();
        int namedParameterCount = parsedSql.getNamedParameterCount();//带有名字参数的总数
        int unnamedParameterCount = parsedSql.getUnnamedParameterCount();//无名字参数总数
        int totalParameterCount = parsedSql.getTotalParameterCount();//参数总数
        //
        Object[] paramArray = new Object[totalParameterCount];
        if (namedParameterCount > 0 && unnamedParameterCount > 0) {
            throw new SQLException("You can't mix named and traditional ? placeholders. You have " + namedParameterCount + " named parameter(s) and " + unnamedParameterCount + " traditonal placeholder(s) in [" + originalSql + "]");
        }
        for (int i = 0; i < parameterNames.size(); i++) {
            String paramName = parameterNames.get(i);
            paramArray[i] = paramSource.getValue(paramName);
        }
        return paramArray;
    }
}