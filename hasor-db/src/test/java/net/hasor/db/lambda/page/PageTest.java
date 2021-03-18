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
package net.hasor.db.lambda.page;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Map;

public class PageTest {
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

    @Test
    public void pageTest_2() throws SQLException {
        Page page = new PageObject();
        page.setPageSize(10);
        assert page.getPageSize() == 10;
        assert page.getCurrentPage() == 0;
        assert page.getPageNumberOffset() == 0;
        assert page.getFirstRecordPosition() == 0;
        assert page.getTotalPage() == 0;
        assert page.getTotalCount() == 0;
    }

    @Test
    public void pageTest_3() throws SQLException {
        Page page1 = new PageObject(10, () -> 10);
        assert page1.getPageSize() == 10;
        assert page1.getCurrentPage() == 0;
        assert page1.getPageNumberOffset() == 0;
        assert page1.getFirstRecordPosition() == 0;
        assert page1.getTotalPage() == 1;
        assert page1.getTotalCount() == 10;
        //
        Page page2 = new PageObject(10, () -> 7);
        assert page2.getPageSize() == 10;
        assert page2.getCurrentPage() == 0;
        assert page2.getPageNumberOffset() == 0;
        assert page2.getFirstRecordPosition() == 0;
        assert page2.getTotalPage() == 1;
        assert page2.getTotalCount() == 7;
        //
        Page page3 = new PageObject(0, () -> 7);
        assert page3.getPageSize() == 0;
        assert page3.getCurrentPage() == 0;
        assert page3.getPageNumberOffset() == 0;
        assert page3.getFirstRecordPosition() == 0;
        assert page3.getTotalPage() == 1;
        assert page3.getTotalCount() == 7;
    }

    @Test
    public void pageTest_4() throws SQLException {
        Page page1 = new PageObject(10, () -> 10);
        page1.setPageNumberOffset(2);
        assert page1.getPageSize() == 10;
        assert page1.getCurrentPage() == 2;     // offset +2
        assert page1.getPageNumberOffset() == 2;// offset
        assert page1.getFirstRecordPosition() == 0;
        assert page1.getTotalPage() == 3;       // offset +2
        assert page1.getTotalCount() == 10;
    }

    @Test
    public void pageTest_5() throws SQLException {
        Page page1 = new PageObject(4, () -> 15);
        assert page1.getTotalPage() == 4;
        //
        Page page2 = new PageObject(4, () -> 16);
        assert page2.getTotalPage() == 4;
        //
        Page page3 = new PageObject(4, () -> 17);
        assert page3.getTotalPage() == 5;
    }

    @Test
    public void pageTest_6() {
        Page page1 = new PageObject(4, () -> 15);
        page1.setCurrentPage(1);
        assert page1.getFirstRecordPosition() == 4;
        page1.setCurrentPage(4);
        assert page1.getFirstRecordPosition() == 16;
        //
        Page page2 = new PageObject(4, () -> 16);
        page2.setCurrentPage(1);
        assert page2.getFirstRecordPosition() == 4;
        page2.setCurrentPage(4);
        assert page2.getFirstRecordPosition() == 16;
        //
        Page page3 = new PageObject(4, () -> 17);
        page3.setCurrentPage(1);
        assert page3.getFirstRecordPosition() == 4;
        page3.setCurrentPage(4);
        assert page3.getFirstRecordPosition() == 16;
    }

    @Test
    public void pageTest_7() {
        Page page = new PageObject(4, () -> 15);
        assert page.getFirstRecordPosition() == 0;
        //
        page.nextPage();
        assert page.getFirstRecordPosition() == 4;
        page.nextPage();
        assert page.getFirstRecordPosition() == 8;
        page.nextPage();
        assert page.getFirstRecordPosition() == 12;
        page.nextPage();
        assert page.getFirstRecordPosition() == 16;
        page.nextPage();
        assert page.getFirstRecordPosition() == 20;
    }

    @Test
    public void pageTest_8() {
        Page page = new PageObject(4, () -> 15);
        page.setCurrentPage(6);
        assert page.getFirstRecordPosition() == 24;
        //
        page.previousPage();
        assert page.getFirstRecordPosition() == 20;
        page.previousPage();
        assert page.getFirstRecordPosition() == 16;
        page.previousPage();
        assert page.getFirstRecordPosition() == 12;
        page.previousPage();
        assert page.getFirstRecordPosition() == 8;
        page.previousPage();
        assert page.getFirstRecordPosition() == 4;
        page.previousPage();
        assert page.getFirstRecordPosition() == 0;
        page.previousPage();
        assert page.getFirstRecordPosition() == 0;
    }

    @Test
    public void pageTest_9() throws SQLException {
        Page page = new PageObject(4, () -> 15);
        page.setCurrentPage(6);
        assert page.getFirstRecordPosition() == 24;
        //
        page.firstPage();
        assert page.getFirstRecordPosition() == 0;
        page.lastPage();
        assert page.getFirstRecordPosition() == 12;
    }

    @Test
    public void pageTest_10() throws SQLException {
        Page page = new PageObject(4, () -> 15);
        page.setCurrentPage(6);
        Map<String, Object> pageInfo = page.toPageInfo();
        //
        assert pageInfo.get("enable").equals(true);
        assert pageInfo.get("pageSize").equals(4);
        assert pageInfo.get("totalCount").equals(15);
        assert pageInfo.get("totalPage").equals(4);
        assert pageInfo.get("currentPage").equals(6);
        assert pageInfo.get("recordPosition").equals(24);
        //
        page.setPageNumberOffset(2);
        pageInfo = page.toPageInfo();
        assert pageInfo.get("enable").equals(true);
        assert pageInfo.get("pageSize").equals(4);
        assert pageInfo.get("totalCount").equals(15);
        assert pageInfo.get("totalPage").equals(6);// 4+2
        assert pageInfo.get("currentPage").equals(8);// 6(CurrentPage) +2
        assert pageInfo.get("recordPosition").equals(24);
    }

    @Test
    public void pageTest_11() throws SQLException {
        Page page = new PageObject(0, () -> 15);
        page.setCurrentPage(6);
        Map<String, Object> pageInfo = page.toPageInfo();
        //
        assert pageInfo.get("enable").equals(false);
        assert pageInfo.get("pageSize").equals(0);
        assert pageInfo.get("totalCount").equals(15);
        assert pageInfo.get("totalPage").equals(1);
        assert pageInfo.get("currentPage").equals(0);
        assert pageInfo.get("recordPosition").equals(0);
        //
        page.setPageNumberOffset(2);
        pageInfo = page.toPageInfo();
        assert pageInfo.get("enable").equals(false);
        assert pageInfo.get("pageSize").equals(0);
        assert pageInfo.get("totalCount").equals(15);
        assert pageInfo.get("totalPage").equals(3);
        assert pageInfo.get("currentPage").equals(2);
        assert pageInfo.get("recordPosition").equals(0);
    }
}
