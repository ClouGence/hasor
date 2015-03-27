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
package net.hasor.rsf.manager;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
/**
 * 
 * @version : 2015年3月28日
 * @author 赵永春(zyc@hasor.net)
 */
public class ComfitManager<T> {
    private final String                              PUBLIC = getClass().getName();
    private final Provider<T>[]                       EMPTY_COMFIT;
    private StatusManager<StatusManager<Provider<T>>> statusManager;
    //
    public ComfitManager() {
        this.statusManager = new StatusManager<StatusManager<Provider<T>>>();
        this.statusManager.putIfAbsent(PUBLIC, new StatusManager<Provider<T>>());
        EMPTY_COMFIT = new Provider[0];
    }
    //
    protected StatusManager<StatusManager<Provider<T>>> statusManager() {
        return statusManager;
    }
    //
    //
    /** 根据ID查找comfit，该方法会从PUBLIC内匹配。
     * @see #PUBLIC
     * @see net.hasor.rsf.status.StatusManager */
    public Provider<T> findComfitByComfitID(String comfitID) {
        StatusManager<Provider<T>> managerForPublic = this.statusManager().getByKey(PUBLIC);
        if (managerForPublic != null) {
            Provider<T> define = managerForPublic.getByKey(comfitID);
            if (define != null) {
                return define;
            }
        }
        return null;
    }
    /** 根据ID查找对象，该方法会从PUBLIC内匹配，如果找不到会在PUBLIC内匹配。
     * @see #PUBLIC
     * @see #findComfitByComfitID(String)
     * @see net.hasor.rsf.status.StatusManager */
    public Provider<T> findComfitByComfitID(String objectID, String comfitID) {
        StatusManager<Provider<T>> managerForServices = this.statusManager().getByKey(objectID);
        if (managerForServices != null) {
            Provider<T> define = managerForServices.getByKey(comfitID);
            if (define != null) {
                return define;
            }
        }
        return this.findComfitByComfitID(comfitID);
    }
    //
    /**获取全局有效的Comfit*/
    public Provider<T>[] findAllComfitInPublic() {
        StatusManager<Provider<T>> managerForPublic = this.statusManager().getByKey(PUBLIC);
        if (managerForPublic != null) {
            return managerForPublic.getAll();
        }
        return EMPTY_COMFIT;
    }
    /**获取服务上配置有效的过滤器*/
    public Provider<T>[] findAllComfitByObjectID(String objectID) {
        Provider<T>[] pubDefines = EMPTY_COMFIT;//  Public
        Provider<T>[] subDefines = EMPTY_COMFIT;// Service
        //
        StatusManager<Provider<T>> managerForServices = this.statusManager().getByKey(objectID);
        if (managerForServices != null) {
            subDefines = managerForServices.getAll();
        }
        StatusManager<Provider<T>> managerForPublic = this.statusManager().getByKey(PUBLIC);
        if (managerForPublic != null) {
            pubDefines = managerForPublic.getAll();
        }
        //
        int mergeFilterLength = pubDefines.length + subDefines.length;
        Provider<T>[] mergeFilters = new Provider[mergeFilterLength];
        //
        System.arraycopy(subDefines, 0, mergeFilters, 0, subDefines.length);
        System.arraycopy(subDefines, 0, mergeFilters, pubDefines.length, subDefines.length);
        return mergeFilters;
    }
    //
    /** 添加Comfit */
    public void addComfit(String comfitID, T instance) {
        this.addComfit(PUBLIC, comfitID, new InstanceProvider<T>(instance));
    }
    /** 添加Comfit */
    public void addComfit(String comfitID, Provider<T> provider) {
        this.addComfit(PUBLIC, comfitID, provider);
    }
    /** 添加Comfit */
    public void addComfit(String objectID, String comfitID, T instance) {
        this.addComfit(objectID, comfitID, new InstanceProvider<T>(instance));
    }
    /** 添加Comfit */
    public void addComfit(String objectID, String comfitID, Provider<T> provider) {
        StatusManager<Provider<T>> targetManager = this.statusManager().putIfAbsent(objectID, new StatusManager<Provider<T>>());
        targetManager.putIfAbsent(comfitID, provider);
    }
}