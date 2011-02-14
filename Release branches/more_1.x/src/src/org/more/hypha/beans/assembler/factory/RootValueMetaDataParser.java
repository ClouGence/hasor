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
package org.more.hypha.beans.assembler.factory;
import java.util.HashMap;
import java.util.Map;
import org.more.RepeateException;
import org.more.hypha.ApplicationContext;
import org.more.hypha.beans.ValueMetaData;
import org.more.hypha.beans.ValueMetaDataParser;
/**
 * 
 * @version 2011-1-21
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class RootValueMetaDataParser implements ValueMetaDataParser<ValueMetaData> {
    private Map<String, ValueMetaDataParser<ValueMetaData>> metaDataParserMap = new HashMap<String, ValueMetaDataParser<ValueMetaData>>();
    //
    public Object parser(ValueMetaData data, ValueMetaDataParser<?> rootParser, ApplicationContext context) {
        // TODO Auto-generated method stub
        return null;
    };
    public void addParser(String propertyType, ValueMetaDataParser<?> parser) throws RepeateException {
        // TODO Auto-generated method stub
    };
    public void removeParser(String metaDataType) {
        // TODO Auto-generated method stub
    };
};