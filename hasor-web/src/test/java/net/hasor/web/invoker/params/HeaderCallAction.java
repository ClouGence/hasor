package net.hasor.web.invoker.params;
import net.hasor.web.annotation.HeaderParameter;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;
import net.hasor.web.invoker.beans.SelectEnum;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
//
@MappingTo("/header_param.do")
public class HeaderCallAction {
    //
    @Post
    public Map<String, Object> execute(//
            @HeaderParameter("byteParam") byte byteParam, @HeaderParameter("shortParam") short shortParam,  //
            @HeaderParameter("intParam") int intParam, @HeaderParameter("longParam") long longParam,    //
            @HeaderParameter("floatParam") float floatParam, @HeaderParameter("doubleParam") double doubleParam,//
            @HeaderParameter("charParam") char charParam, @HeaderParameter("strParam") String strParam, //
            @HeaderParameter("enumParam") SelectEnum enumParam,//
            @HeaderParameter("bigInteger") BigInteger bigInteger, @HeaderParameter("bigDecimal") BigDecimal bigDecimal,//
            //
            @HeaderParameter("urlParam") URL urlParam, @HeaderParameter("uriParam") URI uriParam, @HeaderParameter("fileParam") File fileParam,
            //
            //
            @HeaderParameter("utilData") java.util.Date utilData, @HeaderParameter("utilCalendar") java.util.Calendar utilCalendar, //
            @HeaderParameter("sqlData") java.sql.Date sqlData, @HeaderParameter("sqlTime") java.sql.Time sqlTime, @HeaderParameter("sqlTimestamp") java.sql.Timestamp sqlTimestamp //
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
