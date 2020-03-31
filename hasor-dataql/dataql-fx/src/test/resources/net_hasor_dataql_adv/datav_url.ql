import "net.hasor.dataql.fx.basic.CollectionUdfSource" as collec;
import "net.hasor.dataql.fx.basic.DateTimeUdfSource" as time2;
import "net.hasor.dataql.fx.basic.StringUdfSource" as string;
import 'net.hasor.dataql.fx.basic.CompareUdfSource' as compare;
import "net.hasor.dataql.fx.encryt.HmacUdfSource" as hmac;
import "net.hasor.dataql.fx.encryt.DigestUdfSource" as digest;
import 'net.hasor.dataql.fx.encryt.CodecUdfSource' as codec;

//
var screenID = "1c006f1b16ebaa77f455d8550a88"
var branch_id = 20200101

// ----------------------------------------------------------------------------------------
// 大屏的 Token 信息 和对应的参数，每块大屏一个
var tokenMap = {
    "bf5e5b6caf02a23cd6548df10123" : {
        "token" : "kVh2VBAJ40WQ1VgRs4",
        "paramMap" : { }
    },
    "1c006f1b16ebaa77f455d8550a88" : {
        "token" : "xF6fmmStlOMIuB-3yz",
        "paramMap" : {
            "branch_id": branch_id,
            "datav_sign_no" :123,
            "datav_sign_aaa" :123
         }
    }
}
// ----------------------------------------------------------------------------------------
// 不存在的大屏
if (tokenMap[screenID] == null) {
    throw 404 ,"not found.";
}

// 自定义参数
var customeParams = tokenMap[screenID].paramMap;

// 计算签名串：先过滤出要签名的参数 -> 参数排序 -> 通过map2string转成字符串 -> 在交给下一步进行签名
var tmpParam = collec.filterMap(customeParams,(key) -> {
    return string.startsWith(key,"datav_sign_");
});
var tmpParam = collec.mapSort(tmpParam,(k1,k2) -> {
    return compare.compareString(k1,k2);//对 key 比大小进行排序
});
var signParamStr = collec.map2string(tmpParam,"&",(key,value) -> {
    return key + "=" + value;
});

//
// 签名
var timeNow = 1585664817053;//time2.now();
var token = tokenMap[screenID].token;
var stringToSign = string.join([screenID, timeNow,signParamStr],"|");
var signature = hmac.hmacSHA256_string(token,stringToSign)

// 最终的 URL
var queryParams = {
  "_datav_time": timeNow,
  "_datav_signature": signature
};
var queryParams = collec.mergeMap(queryParams,customeParams);
var url = "http://datav.aliyun.com/share/"+ screenID +"?" + collec.map2string(queryParams,"&",(key,value) -> {
    return key + "=" + codec.urlEncode(value);// 要做一个编码，因为signature 是 Base64 的其中有 / 字符出现。 
});

return url;