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
package org.more.core.database.assembler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.more.core.database.QueryCallBack;
/**
 * ªÿµ˜
 * @version : 2011-12-27
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class DefaultQueryCallBack implements QueryCallBack {
    public DefaultQueryCallBack(AbstractQuery<?> abstractQuery) {
        // TODO Auto-generated constructor stub
    }
    public Object callBack(PreparedStatement preStatement, Object[] params) {
        return null;
    }
    @Override
    public Object callBack(Connection preStatement, String queryString, Object[] params) {
        // TODO Auto-generated method stub
        return null;
    };
};