package net.hasor.web.invoker.params;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.invoker.beans.SelectEnum;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
//
@MappingTo("/query_param.do")
public class QueryCallAction {
    //
    @Post
    public Map<String, Object> execute(//
            @QueryParameter("byteParam") byte byteParam, @QueryParameter("shortParam") short shortParam,  //
            @QueryParameter("intParam") int intParam, @QueryParameter("longParam") long longParam,    //
            @QueryParameter("floatParam") float floatParam, @QueryParameter("doubleParam") double doubleParam,//
            @QueryParameter("charParam") char charParam, @QueryParameter("strParam") String strParam, //
            @QueryParameter("enumParam") SelectEnum enumParam,//
            @QueryParameter("bigInteger") BigInteger bigInteger, @QueryParameter("bigDecimal") BigDecimal bigDecimal,//
            //
            @QueryParameter("urlParam") URL urlParam, @QueryParameter("uriParam") URI uriParam, @QueryParameter("fileParam") File fileParam,
            //
            //
            @QueryParameter("utilData") java.util.Date utilData, @QueryParameter("utilCalendar") java.util.Calendar utilCalendar, //
            @QueryParameter("sqlData") java.sql.Date sqlData, @QueryParameter("sqlTime") java.sql.Time sqlTime, @QueryParameter("sqlTimestamp") java.sql.Timestamp sqlTimestamp //
    ) {
        Map<String, Object> dataMap = new HashMap<>();
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
