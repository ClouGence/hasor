package net.example.springcloud.provider.config;
import net.hasor.core.ApiBinder;
import net.hasor.core.DimModule;
import net.hasor.dataql.Finder;
import net.hasor.dataql.QueryApiBinder;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.spring.SpringModule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Objects;

@DimModule
@Component
public class DatawayModule implements SpringModule {
    @Resource(name = "dataDs1")
    private DataSource dataDs1 = null;
    @Resource(name = "dataDs2")
    private DataSource dataDs2 = null;

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        QueryApiBinder queryBinder = apiBinder.tryCast(QueryApiBinder.class);
        // .check
        Objects.requireNonNull(this.dataDs1, "dataDs1 is null");
        Objects.requireNonNull(this.dataDs2, "dataDs2 is null");
        // .DataSource form Spring boot into Hasor
        queryBinder.installModule(new JdbcModule(Level.Full, "ds1", this.dataDs1));
        queryBinder.installModule(new JdbcModule(Level.Full, "ds2", this.dataDs2));
        // udf/udfSource/import 指令 的类型创建委托给 spring
        queryBinder.bindFinder(Finder.TYPE_SUPPLIER.apply(springTypeSupplier(apiBinder)));
    }
}