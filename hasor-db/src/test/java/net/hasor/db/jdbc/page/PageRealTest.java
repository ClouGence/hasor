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
package net.hasor.db.jdbc.page;
import org.junit.Test;

import java.sql.SQLException;

public class PageRealTest {
    @Test
    public void pageTest_1() throws SQLException {
        Page page = new PageObject();
        assert page.getPageSize() == 0;
        assert page.getCurrentPage() == 0;
        assert page.getPageNumberOffset() == 0;
        assert page.getFirstRecordPosition() == 0;
        assert page.getTotalPage() == 0;
        assert page.getTotalCount() == 0;
        //
        Page page2 = new PageObject();
        page2.setPageSize(-1000);
        assert page2.getPageSize() == 0;
        //
        Page page3 = new PageObject();
        page3.setCurrentPage(-1000);
        assert page3.getCurrentPage() == 0;
        page3.setPageNumberOffset(2);
        assert page3.getCurrentPage() == 2;
        page3.setPageNumberOffset(-1000);
        assert page3.getCurrentPage() == 0;
    }
}
