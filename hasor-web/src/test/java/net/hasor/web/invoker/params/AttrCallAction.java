package net.hasor.web.invoker.params;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.AttributeParameter;
import net.hasor.web.invoker.beans.SelectEnum;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
//
@MappingTo("/attr_param.do")
public class AttrCallAction {
    //
    @Post
    public Map<String, Object> execute(//
            @AttributeParameter("byteParam") byte byteParam, @AttributeParameter("shortParam") short shortParam,  //
            @AttributeParameter("intParam") int intParam, @AttributeParameter("longParam") long longParam,    //
            @AttributeParameter("floatParam") float floatParam, @AttributeParameter("doubleParam") double doubleParam,//
            @AttributeParameter("charParam") char charParam, @AttributeParameter("strParam") String strParam, //
            @AttributeParameter("enumParam") SelectEnum enumParam,//
            @AttributeParameter("bigInteger") BigInteger bigInteger, @AttributeParameter("bigDecimal") BigDecimal bigDecimal,//
            //
            @AttributeParameter("urlParam") URL urlParam, @AttributeParameter("uriParam") URI uriParam, @AttributeParameter("fileParam") File fileParam,
            //
            //
            @AttributeParameter("utilData") java.util.Date utilData, @AttributeParameter("utilCalendar") java.util.Calendar utilCalendar, //
            @AttributeParameter("sqlData") java.sql.Date sqlData, @AttributeParameter("sqlTime") java.sql.Time sqlTime, @AttributeParameter("sqlTimestamp") java.sql.Timestamp sqlTimestamp //
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
