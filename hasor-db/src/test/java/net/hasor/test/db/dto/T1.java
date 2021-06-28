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
package net.hasor.test.db.dto;
import net.hasor.db.mapping.Table;

/**
 *
 * @version : 2013-12-10
 * @author 赵永春 (zyc@hasor.net)
 */
@Table(value = "t1", mapUnderscoreToCamelCase = true)
public class T1 {
    private int t1S1;
    private int t1S2;
    private int t1S3;

    public int getT1S1() {
        return this.t1S1;
    }

    public void setT1S1(int t1S1) {
        this.t1S1 = t1S1;
    }

    public int getT1S2() {
        return this.t1S2;
    }

    public void setT1S2(int t1S2) {
        this.t1S2 = t1S2;
    }

    public int getT1S3() {
        return this.t1S3;
    }

    public void setT1S3(int t1S3) {
        this.t1S3 = t1S3;
    }
}
