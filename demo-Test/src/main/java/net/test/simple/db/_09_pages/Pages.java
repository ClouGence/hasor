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
package net.test.simple.db._09_pages;
import net.hasor.db.ar.Paginator;
/**
 * 
 * @version : 2014年10月30日
 * @author 赵永春(zyc@hasor.net)
 */
public class Pages {
    public static void main(String[] args) {
        Paginator pag = new Paginator();
        pag.setTotalCount(20);
        pag.setCurrentPage(100);
        //
        System.out.println("    pageSize: " + pag.getPageSize());
        System.out.println("  totalCount: " + pag.getTotalCount());
        System.out.println(" isFirstPage: " + pag.isFirstPage());
        System.out.println(" hasPrevious: " + pag.hasPreviousPage());
        System.out.println("PreviousPage: " + pag.getPreviousPage());
        System.out.println(" CurrentPage: " + pag.getCurrentPage());
        System.out.println("    NextPage: " + pag.getNextPage());
        System.out.println(" hasNextPage: " + pag.hasNextPage());
        System.out.println("  isLastPage: " + pag.isLastPage());
        System.out.println("   totalPage: " + pag.getTotalPage());
        System.out.println("   firstItem: " + pag.getFirstItem());
        System.out.println("   lastItem : " + pag.getLastItem());
    }
}