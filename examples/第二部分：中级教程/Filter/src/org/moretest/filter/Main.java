package org.moretest.filter;
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
import org.more.submit.SubmitBuild;
import org.more.submit.SubmitContext;
import org.more.submit.casing.more.MoreBuilder;
/**
 * 
 * Date : 2009-12-11
 * @author Administrator
 */
public class Main {
    /**
     * @param args
     * @throws Throwable 
     */
    public static void main(String[] args) throws Throwable {
        //第一步、获得SubmitContext环境
        SubmitBuild cd = new SubmitBuild();//创建SubmitContext生成器，该类负责生成SubmitContext类对象。
        SubmitContext submitContext = cd.build(new MoreBuilder());//调用build方法生成SubmitContext对象，ClientMoreBuilder对象是专注于桌面程序的生成器。
        System.out.println(submitContext.doAction("action.hello"));
        System.out.println("----");
        System.out.println(submitContext.doAction("action-filterLink.hello"));
    }
}