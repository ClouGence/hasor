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
package net.hasor.dataql;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.spi.ContextInitializeListener;
import net.hasor.dataql.binder.DimFragment;
import net.hasor.dataql.binder.DimUdf;
import net.hasor.dataql.binder.DimUdfSource;
import net.hasor.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 全面提供 “数据库 + 服务” 整合查询，并为查询结果提供全面的数据整合能力。您甚至可以通过一条 QL 混合 RPC 和数据库的查询结果。
 * 除此之外，DataQL 软件包，还为您提供全面的数据库事务控制能力，在底层上 DataQL 还为您提供了 DataSource、JDBC操作接口、等常见的功能。
 * 无论您是简单使用数据库查询，还是要整合数据库 + 服务。 DataQL 都将会您最有力的帮手。
 *
 * Data 提供的 DataQL
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface DataApiBinder extends ApiBinder, Hints {
    /** 加载所有 Dim 注解，@DimFragment、@DimUdf、@DimUdfSource 注解。 */
    public default DataApiBinder loadAllDims(String... packages) {
        loadUdfDims(packages);
        loadUdfSourceDims(packages);
        loadFragmentDims(packages);
        return this;
    }

    /** 扫描包，加载所有 @DimFragment 注解。 */
    public default DataApiBinder loadFragmentDims(String... packages) {
        return loadFragmentDims(o -> true, packages);
    }

    /** 扫描包，加载所有 @DimFragment 注解。 */
    public default DataApiBinder loadFragmentDims(Predicate<Class<?>> predicate, String... packages) {
        packages = (packages == null || packages.length == 0) ? getEnvironment().getSpanPackage() : packages;
        Set<Class<?>> fragmentSet = getEnvironment().findClass(DimFragment.class, packages);
        for (Class<?> fragmentType : fragmentSet) {
            DimFragment[] annotationsByType = fragmentType.getAnnotationsByType(DimFragment.class);
            if (annotationsByType == null || annotationsByType.length == 0) {
                continue;
            }
            if (!predicate.test(fragmentType)) {
                continue;
            }
            if (FragmentProcess.class.isAssignableFrom(fragmentType)) {
                throw new ClassCastException(fragmentType.getName() + " type is not FragmentProcess.");
            }
            Class<FragmentProcess> fragmentProcessClass = (Class<FragmentProcess>) fragmentType;
            Arrays.stream(annotationsByType).map(DimFragment::value).forEach(typeName -> {
                bindFragment(typeName, bindType(FragmentProcess.class).uniqueName().to(fragmentProcessClass).toInfo());
            });
        }
        return this;
    }

    /** 扫描包，加载所有 @DimUdf 注解。 */
    public default DataApiBinder loadUdfDims(String... packages) {
        return loadUdfDims(o -> true, packages);
    }

    /** 扫描包，加载所有 @DimUdf 注解。 */
    public default DataApiBinder loadUdfDims(Predicate<Class<?>> predicate, String... packages) {
        // .搜寻
        packages = (packages == null || packages.length == 0) ? getEnvironment().getSpanPackage() : packages;
        Set<Class<?>> udfSet = getEnvironment().findClass(DimUdf.class, packages);
        List<BindInfo<Udf>> collect = udfSet.stream().filter(predicate).filter(udfSources -> {
            if (Udf.class.isAssignableFrom(udfSources)) {
                throw new ClassCastException(udfSources.getName() + " type is not Udf.");
            }
            return true;
        }).map(aClass -> {
            Class<? extends Udf> dimUdfType = (Class<? extends Udf>) aClass;
            DimUdf dimUdf = dimUdfType.getAnnotation(DimUdf.class);
            if (StringUtils.isBlank(dimUdf.value())) {
                throw new NullPointerException(dimUdfType.getName() + " type udf name Undefined.");
            }
            return bindType(Udf.class).nameWith(dimUdf.value()).to(dimUdfType).toInfo();
        }).collect(Collectors.toList());
        //
        // .装载
        bindSpiListener(ContextInitializeListener.class, appContext -> {
            DataQL dataQL = appContext.getInstance(DataQL.class);
            for (BindInfo<Udf> udfType : collect) {
                Udf udfObj = Objects.requireNonNull(appContext.getInstance(udfType), "load Udf is null.");
                dataQL.addShareVar(udfType.getBindName(), () -> udfObj);
            }
        });
        return this;
    }

    /** 扫描包，加载所有 @DimUdfSource 注解。 */
    public default DataApiBinder loadUdfSourceDims(String... packages) {
        return loadUdfSourceDims(o -> true, packages);
    }

    /** 扫描包，加载所有 @DimUdfSource 注解。 */
    public default DataApiBinder loadUdfSourceDims(Predicate<Class<?>> predicate, String... packages) {
        // .搜寻
        packages = (packages == null || packages.length == 0) ? getEnvironment().getSpanPackage() : packages;
        Set<Class<?>> udfSourcesSet = getEnvironment().findClass(DimUdfSource.class, packages);
        List<BindInfo<UdfSource>> collect = udfSourcesSet.stream().filter(predicate).filter(udfSources -> {
            if (UdfSource.class.isAssignableFrom(udfSources)) {
                throw new ClassCastException(udfSources.getName() + " type is not UdfSource.");
            }
            return true;
        }).map(aClass -> {
            Class<? extends UdfSource> udfSourceType = (Class<? extends UdfSource>) aClass;
            DimUdfSource dimUdfSource = udfSourceType.getAnnotation(DimUdfSource.class);
            if (StringUtils.isBlank(dimUdfSource.value())) {
                throw new NullPointerException(udfSourceType.getName() + " type udfSource name Undefined.");
            }
            return bindType(UdfSource.class).nameWith(dimUdfSource.value()).to(udfSourceType).toInfo();
        }).collect(Collectors.toList());
        //
        // .装载
        bindSpiListener(ContextInitializeListener.class, appContext -> {
            DataQL dataQL = appContext.getInstance(DataQL.class);
            for (BindInfo<UdfSource> udfSourceType : collect) {
                UdfSource udfObj = Objects.requireNonNull(appContext.getInstance(udfSourceType), "load udfSource is null.");
                dataQL.addShareVar(udfSourceType.getBindName(), () -> udfObj);
            }
        });
        return this;
    }

    /** 添加全局变量（等同于 compilerVar） */
    public default DataApiBinder addShareVarInstance(String name, Object instance) {
        return this.addShareVar(name, () -> instance);
    }

    /** 添加全局变量（等同于 compilerVar） */
    public default <T> DataApiBinder addShareVar(String name, Class<? extends T> implementation) {
        return this.addShareVar(name, getProvider(implementation));
    }

    /** 添加全局变量（等同于 compilerVar） */
    public default <T> DataApiBinder addShareVar(String name, BindInfo<T> bindInfo) {
        return this.addShareVar(name, getProvider(bindInfo));
    }

    /** 添加全局变量（等同于 compilerVar） */
    public <T> DataApiBinder addShareVar(String name, Supplier<T> provider);

    public DataApiBinder bindFinder(Finder finder);

    /** 注册 FragmentProcess */
    public default DataApiBinder bindFragment(String fragmentType, FragmentProcess instance) {
        return this.bindFragment(fragmentType, () -> instance);
    }

    /** 注册 FragmentProcess */
    public default <T extends FragmentProcess> DataApiBinder bindFragment(String fragmentType, Class<? extends T> implementation) {
        return this.bindFragment(fragmentType, getProvider(implementation));
    }

    /** 注册 FragmentProcess */
    public default <T extends FragmentProcess> DataApiBinder bindFragment(String fragmentType, BindInfo<T> bindInfo) {
        return this.bindFragment(fragmentType, getProvider(bindInfo));
    }

    /** 注册 FragmentProcess */
    public default <T extends FragmentProcess> DataApiBinder bindFragment(String fragmentType, Supplier<T> provider) {
        bindType(FragmentProcess.class).nameWith(fragmentType).toProvider(provider);
        return this;
    }
}