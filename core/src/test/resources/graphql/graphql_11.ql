// 规则
var mailChargeSet = readTable("dataSourceID","mailCharge") [
    {
        "type"  : FIELD,
        "minVal": MINVAL,
        "maxVal": MAXVAL,
        "chagre": CHARGE
    }
]
var costRuleDs = filter(mailChargeSet , lambda : (obj) -> {
        return obj.type == "COST"
    })~;
var weightRuleDs = filter(mailChargeSet , lambda : (obj) -> {
        return obj.type == "WEIGHT"
    })~;

// 运费计算逻辑
var evalChagre = lambda : (dat) -> {

    // 依照订单金额
    var rule = filter(costRuleDs , lambda : (obj) -> {
        return obj.minVal <= dat.cost && dat.cost <= obj.maxVal
    })~;
    if (rule != null)
        return rule.chagre;
    end

    // 依照重量
    var rule = filter(weightRuleDs , lambda : (obj) -> {
        return obj.minVal <= dat.weight && dat.weight <= obj.maxVal
    })~;
    if (rule != null)
        return rule.cost * dat.weight
    end

    throw "没有匹配到邮费规则";
}

/*
    LAMBDA  1
    LOCAL   1,"dat"
    FRAME_S
    ...
    LDC_S   "没有匹配到邮费规则"
    ERR
    FRAME_E
    STORE   3
    ...
    LDC_D   1998
    LDC_D   1998
    LCALL   3,2
    ...
*/

// 循环订单并计算运费，新的运费结果在循环数据时输出
return readTxt("orderSet.txt") [
    {
        "id",
        "cost",
        "weight",
        "chagre" : evalChagre({
            "cost",
            "weight"
        })~
    }
]