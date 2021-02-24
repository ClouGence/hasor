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
package net.hasor.dataql.parser;
import net.hasor.dataql.DataQueryException;
import net.hasor.dataql.parser.location.CodeLocation;
import net.hasor.dataql.parser.location.LocationUtils;

/**
 * DataQL 解析异常。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-01-22
 */
public class QueryParseException extends DataQueryException {
    public QueryParseException(int line, int column, String message) {
        super(LocationUtils.atLocation(new CodeLocation(line, column), null), message);
    }

    public int getLine() {
        return this.getLocation().getStartPosition().getLineNumber();
    }

    public int getColumn() {
        return this.getLocation().getStartPosition().getColumnNumber();
    }
}
