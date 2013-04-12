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
package org.platform.api.binder;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.platform.api.context.AppContext;
import org.platform.api.context.ViewContext;
import com.google.inject.Binding;
import com.google.inject.TypeLiteral;
/**
 *  
 * @version : 2013-4-12
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class ManagedFilterPipeline implements FilterPipeline {
    private List<Binding<FilterDefinition>> filterDefine = null;
    //
    @Override
    public void initPipeline(AppContext appContext) throws ServletException {
        this.filterDefine = appContext.getGuice().findBindingsByType(TypeLiteral.get(FilterDefinition.class));
        //filterDefine.get(0).getProvider().get().
    }
    @Override
    public void dispatch(ViewContext viewContext, ServletRequest request, ServletResponse response, FilterChain defaultFilterChain) throws IOException, ServletException {
        // TODO Auto-generated method stub
    }
    @Override
    public void destroyPipeline(AppContext appContext) {
        // TODO Auto-generated method stub
    }
}a