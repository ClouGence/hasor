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

hint MAX_DECIMAL_DIGITS = 4
import "net.hasor.dataql.sdk.CollectionUdfSource" as collect

var year2019 = [
    { "pt":2019, "item_code":"code_1", "sum_price":2234 },
    { "pt":2019, "item_code":"code_2", "sum_price":234 },
    { "pt":2019, "item_code":"code_3", "sum_price":12340 },
    { "pt":2019, "item_code":"code_4", "sum_price":2344 }
];

var year2018 = [
    { "pt":2018, "item_code":"code_1", "sum_price":1234.0 },
    { "pt":2018, "item_code":"code_2", "sum_price":1234.0 },
    { "pt":2018, "item_code":"code_3", "sum_price":1234.0 },
    { "pt":2018, "item_code":"code_4", "sum_price":1234.0 }
];

return collect.mapJoin(year2019,year2018, { "item_code":"item_code" }) => [
    {
        "商品Code": data1.item_code,
        "去年同期": data2.sum_price,
        "今年总额": data1.sum_price,
        "环比去年增长": ((data1.sum_price - data2.sum_price) / data2.sum_price * 100) + "%"
    }
]
