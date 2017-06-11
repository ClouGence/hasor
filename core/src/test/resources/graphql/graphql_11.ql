
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

var evalChagre = lambda : (dat) -> {

    var rule = filter(costRuleDs , lambda : (obj) -> {
        return obj.minVal <= dat.cost && dat.cost <= obj.maxVal
    })~;
    if (rule != null)
        return rule.chagre;
    end

    var rule = filter(weightRuleDs , lambda : (obj) -> {
        return obj.minVal <= dat.weight && dat.weight <= obj.maxVal
    })~;
    if (rule != null)
        return rule.cost * dat.weight
    end

    return error("no rule.")~;
}

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