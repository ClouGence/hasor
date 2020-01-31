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

// 求同比
return collect.mapjoin(year2019,year2018, { "item_code":"item_code" }) => [
    {
        "商品Code": data1.item_code,
        "去年同期": data2.sum_price,
        "今年总额": data1.sum_price,
        "环比去年增长": ((data1.sum_price - data2.sum_price) / data2.sum_price * 100) + "%"
    }
]
