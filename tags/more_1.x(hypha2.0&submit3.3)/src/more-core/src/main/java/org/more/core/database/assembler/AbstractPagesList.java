/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"){};
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
import java.util.List;
import org.more.core.database.PagesList;
/**
 * 通用查询接口实现类.
 * Date : 2010-6-21
 * @author 赵永春
 */
public abstract class AbstractPagesList implements PagesList {
    public int getPageSize() {
        // TODO Auto-generated method stub
        return 0;
    }
    public int getIndex() {
        // TODO Auto-generated method stub
        return 0;
    }
    public int getPageCount() {
        // TODO Auto-generated method stub
        return 0;
    }
    public int getCurrentPageNumber() {
        // TODO Auto-generated method stub
        return 0;
    }
    public <T> List<T> query(Class<T> dataType) {
        // TODO Auto-generated method stub
        return null;
    }
    public PagesList firstPage() {
        // TODO Auto-generated method stub
        return null;
    }
    public PagesList previousPage() {
        // TODO Auto-generated method stub
        return null;
    }
    public PagesList nextPage() {
        // TODO Auto-generated method stub
        return null;
    }
    public PagesList lastPage() {
        // TODO Auto-generated method stub
        return null;
    }
};