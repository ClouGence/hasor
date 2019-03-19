package net.hasor.web.invoker.params;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.RequestParameter;
import net.hasor.web.invoker.beans.SelectEnum;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
//
@MappingTo("/req_param.do")
public class RequestCallAction {
    //
    @Post
    public Map<String, Object> execute(//
            @RequestParameter("byteParam") byte byteParam, @RequestParameter("shortParam") short shortParam,  //
            @RequestParameter("intParam") int intParam, @RequestParameter("longParam") long longParam,    //
            @RequestParameter("floatParam") float floatParam, @RequestParameter("doubleParam") double doubleParam,//
            @RequestParameter("charParam") char charParam, @RequestParameter("strParam") String strParam, //
            @RequestParameter("enumParam") SelectEnum enumParam,//
            @RequestParameter("bigInteger") BigInteger bigInteger, @RequestParameter("bigDecimal") BigDecimal bigDecimal,//
            //
            @RequestParameter("urlParam") URL urlParam, @RequestParameter("uriParam") URI uriParam, @RequestParameter("fileParam") File fileParam,
            //
            //
            @RequestParameter("utilData") java.util.Date utilData, @RequestParameter("utilCalendar") java.util.Calendar utilCalendar, //
            @RequestParameter("sqlData") java.sql.Date sqlData, @RequestParameter("sqlTime") java.sql.Time sqlTime, @RequestParameter("sqlTimestamp") java.sql.Timestamp sqlTimestamp //
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
