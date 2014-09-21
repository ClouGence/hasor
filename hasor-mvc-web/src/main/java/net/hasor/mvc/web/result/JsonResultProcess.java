///*
// * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package net.hasor.mvc.web.result;
//import java.io.IOException;
//import java.lang.annotation.Annotation;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import net.hasor.core.Hasor;
//import net.hasor.mvc.result.ResultDefine;
//import net.hasor.mvc.result.ResultProcess;
//import org.more.json.JSON;
//import org.more.json.JSONPojoConvertor;
///**
// * 
// * @version : 2013-6-5
// * @author 赵永春 (zyc@hasor.net)
// */
//@ResultDefine(Json.class)
//public class JsonResultProcess implements ResultProcess {
//    private JSON jsonToos = null;
//    public JsonResultProcess() {
//        this.jsonToos = new JSON() {
//            protected Convertor getConvertor(Class forClass) {
//                Convertor con = super.getConvertor(forClass);
//                return con != null ? con : new JSONPojoConvertor(forClass, true);
//            }
//        };
//    }
//    public void process(HttpServletRequest request, HttpServletResponse response, Annotation annoData, Object result) throws ServletException, IOException {
//        String jsonData = this.jsonToos.toJSON(result);
//        Hasor.logDebug("write json %s.", jsonData.length() > 300 ? jsonData.substring(0, 300) : jsonData);
//        if (response.isCommitted() == false)
//            response.getWriter().write("(" + jsonData + ")");
//    }
//}