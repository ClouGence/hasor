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
package net.hasor.dataway.web;
import net.hasor.core.Inject;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataway.daos.impl.ApiDataAccessLayer;
import net.hasor.dataway.daos.impl.FieldDef;
import net.hasor.dataway.daos.impl.QueryCondition;
import net.hasor.dataway.domain.ApiStatusEnum;
import net.hasor.dataway.domain.ApiTypeData;
import net.hasor.dataway.domain.HeaderData;
import net.hasor.web.WebController;

import java.util.*;
import java.util.function.Predicate;

/**
 * 基础
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
public abstract class BasicController extends WebController {
    public static final Map<FieldDef, String> STATUS_UPDATE_TO_EDITOR    = new HashMap<FieldDef, String>() {{
        put(FieldDef.STATUS, String.valueOf(ApiStatusEnum.Editor.typeNum()));
    }};
    public static final Map<FieldDef, String> STATUS_UPDATE_TO_PUBLISHED = new HashMap<FieldDef, String>() {{
        put(FieldDef.STATUS, String.valueOf(ApiStatusEnum.Published.typeNum()));
    }};
    public static final Map<FieldDef, String> STATUS_UPDATE_TO_CHANGES   = new HashMap<FieldDef, String>() {{
        put(FieldDef.STATUS, String.valueOf(ApiStatusEnum.Changes.typeNum()));
    }};
    public static final Map<FieldDef, String> STATUS_UPDATE_TO_DISABLE   = new HashMap<FieldDef, String>() {{
        put(FieldDef.STATUS, String.valueOf(ApiStatusEnum.Disable.typeNum()));
    }};
    public static final Map<FieldDef, String> STATUS_UPDATE_TO_DELETE    = new HashMap<FieldDef, String>() {{
        put(FieldDef.STATUS, String.valueOf(ApiStatusEnum.Delete.typeNum()));
    }};

    public static Map<QueryCondition, Object> conditionByApiId(String apiId) {
        return new HashMap<QueryCondition, Object>() {{
            put(QueryCondition.ApiId, apiId);
        }};
    }

    public static Map<QueryCondition, Object> conditionByOrderByTime() {
        return new HashMap<QueryCondition, Object>() {{
            put(QueryCondition.OrderByTime, true);
        }};
    }

    //
    //
    @Inject
    protected SpiTrigger         spiTrigger;
    @Inject
    protected ApiDataAccessLayer dataAccessLayer;

    protected static List<Map<String, Object>> headerToList(ApiTypeData requestInfo) {
        return headerToList(requestInfo, headerValue -> true);
    }

    protected static List<Map<String, Object>> headerToList(ApiTypeData requestInfo, Predicate<HeaderData> predicate) {
        if (requestInfo != null && requestInfo.getHeaderData() != null) {
            List<HeaderData> headerData = requestInfo.getHeaderData();
            List<Map<String, Object>> list = new ArrayList<>();
            for (HeaderData header : headerData) {
                if (!predicate.test(header)) {
                    continue;
                }
                list.add(new HashMap<String, Object>() {{
                    put("checked", header.isChecked());
                    put("name", header.getName());
                    put("value", header.getValue());
                }});
            }
            return list;
        }
        return Collections.emptyList();
    }
}