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
//package net.hasor.rsf.plugins.hasor;
//import net.hasor.core.ApiBinder;
//import net.hasor.core.BindInfo;
//import net.hasor.core.Provider;
//import net.hasor.core.binder.ApiBinderWrap;
//import net.hasor.rsf.runtime.RsfContext;
//import net.hasor.rsf.runtime.RsfFilter;
//import net.hasor.rsf.runtime.register.AbstractRegisterCenter;
//import net.hasor.rsf.runtime.register.RsfBinderBuilder;
//import net.hasor.rsf.runtime.register.RsfBinderBuilder.LinkedBuilderImpl;
///**
// * 服务注册器
// * @version : 2014年11月12日
// * @author 赵永春(zyc@hasor.net)
// */
//public class AbstractRsfApiBinder extends ApiBinderWrap implements RsfApiBinder {
//    private RsfContext       rsfContext = null;
//    private RsfBinderBuilder rsfBinder  = null;
//    protected AbstractRsfApiBinder(ApiBinder apiBinder, RsfContext rsfContext) {
//        super(apiBinder);
//        this.rsfContext = rsfContext;
//    }
//    public RsfContext getContext() {
//        return this.rsfContext;
//    }
//    public void bindFilter(RsfFilter instance) {
//        this.rsfBinder.bindFilter(instance);
//    }
//    public void bindFilter(Provider<RsfFilter> provider) {
//        this.rsfBinder.bindFilter(provider);
//    }
//    public <T> HasorLinkedBuilder<T> rsfService(Class<T> type) {
//        return new HasorLinkedBuilderImpl<T>(type, (AbstractRegisterCenter) this.getContext(), this);
//    }
//    public <T> NamedBuilder<T> rsfService(Class<T> type, T instance) {
//        return this.rsfService(type).toInstance(instance);
//    }
//    public <T> NamedBuilder<T> rsfService(Class<T> type, Class<? extends T> implementation) {
//        return this.rsfService(type).to(implementation);
//    }
//    public <T> NamedBuilder<T> rsfService(Class<T> type, Provider<T> provider) {
//        return this.rsfService(type).toProvider(provider);
//    }
//    //
//    public class HasorLinkedBuilderImpl<T> extends LinkedBuilderImpl<T> implements HasorLinkedBuilder<T> {
//        private ApiBinder apiBinder;
//        protected HasorLinkedBuilderImpl(Class<T> serviceType, AbstractRegisterCenter registerCenter, ApiBinder apiBinder) {
//            super(serviceType, registerCenter);
//            this.apiBinder = apiBinder;
//        }
//        public NamedBuilder<T> to(Class<? extends T> implementation) {
//            BindInfo<T> bindInfo = this.apiBinder.bindType(this.serviceType).uniqueName().to(implementation).toInfo();
//            return this.toBindInfo(bindInfo);
//        }
//        public NamedBuilder<T> toInstance(T instance) {
//            BindInfo<T> bindInfo = this.apiBinder.bindType(this.serviceType).uniqueName().toInstance(instance).toInfo();
//            return this.toBindInfo(bindInfo);
//        }
//        //        public NamedBuilder<T> toBindInfo(final BindInfo<T> bindInfo) {
//        //            return apiBinder.bindType(type)， this.toProvider(new Provider<T>() {
//        //                public T get() {
//        //                    return appContext.getInstance(bindInfo);
//        //                }
//        //            });
//        //        }
//        public NamedBuilder<T> toBindInfo(BindInfo<T> bindInfo) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//    }
//}