///*
// * Copyright 2008-2009 the original author or authors.
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
//package test.net.hasor.land.voted;
//import net.hasor.core.Hasor;
//import net.hasor.land.election.ElectionService;
//import net.hasor.rsf.RsfApiBinder;
//import net.hasor.rsf.RsfModule;
//import test.net.hasor.land.election.ElectionServiceVoted_S1;
///**
// * 所有投票都投给 S1
// * @version : 2014年9月12日
// * @author 赵永春(zyc@hasor.net)
// */
//public class AllServerVoted_S1 {
//    public static void main(String[] args) throws Throwable {
//        Hasor.createAppContext("/none/server2-config.xml", new RsfModule() {
//            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
//                apiBinder.installModule(new ElectionServiceVoted_S1());
//                apiBinder.bindType(ElectionServiceVoted_S1.class).asEagerSingleton();
//                apiBinder.rsfService(ElectionService.class).to(ElectionServiceVoted_S1.class).register();
//            }
//        });
//        System.out.println("server B start.");
//        Hasor.createAppContext("/none/server3-config.xml", new RsfModule() {
//            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
//                apiBinder.installModule(new ElectionServiceVoted_S1());
//                apiBinder.bindType(ElectionServiceVoted_S1.class).asEagerSingleton();
//                apiBinder.rsfService(ElectionService.class).to(ElectionServiceVoted_S1.class).register();
//            }
//        });
//        System.out.println("server C start.");
//        System.in.read();
//        //
//    }
//}