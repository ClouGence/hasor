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
import net.hasor.core.Hasor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
/**
 * 单机模式
 * @version : 2015年8月13日
 * @author 赵永春 (zyc@hasor.net)
 */
public class CenterServerAlone {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public static void main(String[] args) throws IOException {
        Hasor.createAppContext("rsf-config.xml");
        System.in.read();
    }
}