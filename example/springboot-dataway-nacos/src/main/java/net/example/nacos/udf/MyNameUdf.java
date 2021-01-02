package net.example.nacos.udf;
import net.example.nacos.service.MyService;
import net.hasor.dataql.DimUdf;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@DimUdf("myName")
@Service
public class MyNameUdf implements Udf {
    @Resource
    private MyService myService;

    @Override
    public Object call(Hints readOnly, Object... params) throws Throwable {
        return myService.myName();
    }
}
