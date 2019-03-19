package net.hasor.web.invoker.params;
import net.hasor.web.annotation.RequestParameter;
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
public class ParamBean {
    @RequestParameter("byteParam")
    private byte               byteParam;
    @RequestParameter("shortParam")
    private short              shortParam;
    @RequestParameter("intParam")
    private int                intParam;
    @RequestParameter("longParam")
    private long               longParam;
    @RequestParameter("floatParam")
    private float              floatParam;
    @RequestParameter("doubleParam")
    private double             doubleParam;
    @RequestParameter("charParam")
    private char               charParam;
    @RequestParameter("strParam")
    private String             strParam;
    @RequestParameter("enumParam")
    private SelectEnum         enumParam;
    @RequestParameter("bigInteger")
    private BigInteger         bigInteger;
    @RequestParameter("bigDecimal")
    private BigDecimal         bigDecimal;
    @RequestParameter("urlParam")
    private URL                urlParam;
    @RequestParameter("uriParam")
    private URI                uriParam;
    @RequestParameter("fileParam")
    private File               fileParam;
    //
    @RequestParameter("utilData")
    private java.util.Date     utilData;
    @RequestParameter("utilCalendar")
    private java.util.Calendar utilCalendar;
    //
    @RequestParameter("sqlData")
    private java.sql.Date      sqlData;
    @RequestParameter("sqlTime")
    private java.sql.Time      sqlTime;
    @RequestParameter("sqlTimestamp")
    private java.sql.Timestamp sqlTimestamp;
    //
    public Map<String, Object> buildParams() {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        //
        dataMap.put("byteParam", this.byteParam);
        dataMap.put("shortParam", this.shortParam);
        dataMap.put("intParam", this.intParam);
        dataMap.put("longParam", this.longParam);
        dataMap.put("floatParam", this.floatParam);
        dataMap.put("doubleParam", this.doubleParam);
        dataMap.put("charParam", this.charParam);
        dataMap.put("strParam", this.strParam);
        //
        dataMap.put("enumParam", this.enumParam);
        dataMap.put("bigInteger", this.bigInteger);
        dataMap.put("bigDecimal", this.bigDecimal);
        dataMap.put("urlParam", this.urlParam);
        dataMap.put("uriParam", this.uriParam);
        dataMap.put("fileParam", this.fileParam);
        //
        dataMap.put("utilData", this.utilData);
        dataMap.put("utilCalendar", this.utilCalendar);
        dataMap.put("sqlData", this.sqlData);
        dataMap.put("sqlTime", this.sqlTime);
        dataMap.put("sqlTimestamp", this.sqlTimestamp);
        return dataMap;
    }
}
