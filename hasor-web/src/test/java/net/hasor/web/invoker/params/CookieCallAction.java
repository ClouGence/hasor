package net.hasor.web.invoker.params;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.CookieParameter;
import net.hasor.web.invoker.beans.SelectEnum;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
//
@MappingTo("/cookie_param.do")
public class CookieCallAction {
    //
    @Post
    public Map<String, Object> execute(//
            @CookieParameter("byteParam") byte byteParam, @CookieParameter("shortParam") short shortParam,  //
            @CookieParameter("intParam") int intParam, @CookieParameter("longParam") long longParam,    //
            @CookieParameter("floatParam") float floatParam, @CookieParameter("doubleParam") double doubleParam,//
            @CookieParameter("charParam") char charParam, @CookieParameter("strParam") String strParam, //
            @CookieParameter("enumParam") SelectEnum enumParam,//
            @CookieParameter("bigInteger") BigInteger bigInteger, @CookieParameter("bigDecimal") BigDecimal bigDecimal,//
            //
            @CookieParameter("urlParam") URL urlParam, @CookieParameter("uriParam") URI uriParam, @CookieParameter("fileParam") File fileParam,
            //
            //
            @CookieParameter("utilData") java.util.Date utilData, @CookieParameter("utilCalendar") java.util.Calendar utilCalendar, //
            @CookieParameter("sqlData") java.sql.Date sqlData, @CookieParameter("sqlTime") java.sql.Time sqlTime, @CookieParameter("sqlTimestamp") java.sql.Timestamp sqlTimestamp //
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
