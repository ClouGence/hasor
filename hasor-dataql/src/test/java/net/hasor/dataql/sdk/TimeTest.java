package net.hasor.dataql.sdk;
import net.hasor.core.Hasor;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.dataql.binder.DataQL;
import net.hasor.dataql.runtime.InstructRuntimeException;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTest extends AbstractTestResource {
    @Test
    public void now() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.sdk.DateTimeUdfSource' as time;";
        qlString = qlString + "return time.now()";
        //
        long t1 = System.currentTimeMillis();
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        long t2 = ((ValueModel) dataModel).asLong();
        long t3 = System.currentTimeMillis();
        assert t1 <= t2 && t2 <= t3;
    }

    @Test
    public void time() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.sdk.DateTimeUdfSource' as time;";
        qlString = qlString + "var now = time.now() ; return (time.year(now) + '-' + time.month(now) + '-' + time.day(now))";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        String format = new SimpleDateFormat("yyyy-M-d").format(new Date());
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asString().equalsIgnoreCase(format);
    }

    @Test
    public void format() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.sdk.DateTimeUdfSource' as time;";
        qlString = qlString + "return time.format(time.now(),'yyyy-MM-dd')";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asString().equalsIgnoreCase(format);
    }

    @Test
    public void parse() throws IOException, InstructRuntimeException, ParseException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.sdk.DateTimeUdfSource' as time;";
        qlString = qlString + "return time.parser('2017-03-15 12:44:56','yyyy-MM-dd hh:mm:ss')";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        long format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2017-03-15 12:44:56").getTime();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asLong() == format;
    }
}