package net.hasor.web.invoker.params;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParam;
import net.hasor.web.invoker.beans.SelectEnum;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
//
@MappingTo("/sync.do")
public class QueryCallAction {
    //
    @Post
    public Map<String, Object> execute(//
            @QueryParam("byteParam") byte byteParam, @QueryParam("shortParam") short shortParam,  //
            @QueryParam("intParam") int intParam, @QueryParam("longParam") long longParam,    //
            @QueryParam("floatParam") float floatParam, @QueryParam("doubleParam") double doubleParam,//
            @QueryParam("charParam") char charParam, @QueryParam("strParam") String strParam, //
            @QueryParam("enumParam") SelectEnum enumParam,//
            @QueryParam("bigInteger") BigInteger bigInteger, @QueryParam("bigDecimal") BigDecimal bigDecimal,//
            //
            @QueryParam("urlParam") URL urlParam, @QueryParam("uriParam") URI uriParam, @QueryParam("fileParam") File fileParam,
            //
            //
            @QueryParam("utilData") java.util.Date utilData, @QueryParam("utilCalendar") java.util.Calendar utilCalendar, //
            @QueryParam("sqlData") java.sql.Date sqlData, @QueryParam("sqlTime") java.sql.Time sqlTime, @QueryParam("sqlTimestamp") java.sql.Timestamp sqlTimestamp //
    ) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        //
        dataMap.put("byteParam", byteParam);
        dataMap.put("shortParam", shortParam);
        dataMap.put("intParam", intParam);
        dataMap.put("longParam", longParam);
        dataMap.put("floatParam", floatParam);
        dataMap.put("doubleParam", doubleParam);
        dataMap.put("charParam", charParam);
        dataMap.put("strParam", strParam);
        //
        dataMap.put("enumParam", enumParam);
        dataMap.put("bigInteger", bigInteger);
        dataMap.put("bigDecimal", bigDecimal);
        dataMap.put("urlParam", urlParam);
        dataMap.put("uriParam", uriParam);
        dataMap.put("fileParam", fileParam);
        //
        dataMap.put("utilData", utilData);
        dataMap.put("utilCalendar", utilCalendar);
        dataMap.put("sqlData", sqlData);
        dataMap.put("sqlTime", sqlTime);
        dataMap.put("sqlTimestamp", sqlTimestamp);
        return dataMap;
    }
}
