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
package org.test.more.util;
import java.beans.IntrospectionException;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import org.more.util.BeanUtil;
/**
 * 
 * @version : 2011-7-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class UtilTest {
    @Test
    public void test() throws Throwable {
        URI uri = new URI("ns://sss/a");
        uri.getAuthority();
        uri.getScheme();
        uri.getPath();
        //          (/([^/]+):)?/([^/]+)/(.*)
        String match = "/([^/]+)?:/([^?]*)(?:\\?(.*))?";
        Pattern p = Pattern.compile(match);
        Matcher m_1 = p.matcher("/");
        Matcher m_2 = p.matcher("http://www.google.com.hk/ns:/www.baidu.com");
        Matcher m_3 = p.matcher("/https:/www.google.com/accounts/ServiceLogin?service=mail&passive=true&rm=false&continue=http%3A%2F%2Fmail.google.com%2Fmail%2F%3Fhl%3Dzh-CN%26tab%3Dwm%26ui%3Dhtml%26zy%3Dl&bsv=llya694le36z&scc=1&ltmpl=default&ltmplcache=2&hl=zh-CN&from=login");
        Matcher m_4 = p.matcher("aaaaa/bbbbb/ccccc/cc.jsp?aa=123&bb=44/ns:/bbbbbbb/asfasdfa.jsp?afasfa=asfsd%dfsad");
        //    .*(/[^/]+:)?/([^/]+)/(.*)
        //
        //
        //  /[^/]+:
        //
        m_1.find();
        m_2.find();
        m_3.find();
        m_4.find();
        //
        Matcher m = m_3;
        for (int i = 0; i <= m.groupCount(); i++)
            System.out.println(m.group(i));
        //
        // 
    }
    public String getAa() {
        return null;
    }
    public void setAa(String aa) {}
    @Test
    public void test2() throws IntrospectionException {
        
        List<String> ps= BeanUtil.getPropertys( UtilTest.class);
        
         System.out.println(ps); 
    }
}