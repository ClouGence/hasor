/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.example.db.udf;
import net.example.db.service.MyService;
import net.hasor.dataql.DimUdf;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-01-02
 */
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
