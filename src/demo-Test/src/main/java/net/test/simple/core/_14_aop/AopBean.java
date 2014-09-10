/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.simple.core._14_aop;
import java.io.IOException;
import java.sql.SQLException;
import org.more.classcode.InnerChainPropertyDelegate;
/**
 * 
 * @version : 2014年9月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class AopBean extends Bean {
    public AopBean() {
        super();
    }
    public String print(final int i, final int c) {
        return super.print(i, c);
    }
    public int doCall(int abc, Object abcc, Object abcc2, Object abcc3) throws SQLException, IOException {
        try {
            System.out.println();
            return 0;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    //
    public int getName() {
        try {
            ClassLoader loader = this.getClass().getClassLoader();
            Object target = new InnerChainPropertyDelegate("", "name", loader).get();
            return ((Integer) target).intValue();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    public void setName(int name) {
        try {
            ClassLoader loader = this.getClass().getClassLoader();
            new InnerChainPropertyDelegate("sd", "name", loader).set(name);
            return;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}