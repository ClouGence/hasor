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
package net.hasor.rsf.utils;
/**
 * 令热策略的,LRU
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ByteStringCachelUtils {
    private static final LRU<Integer, String> stringCache1;
    private static final LRU<String, byte[]>  stringCache2;

    static {
        stringCache1 = new LRU<Integer, String>();
        stringCache2 = new LRU<String, byte[]>();
    }

    public static String fromCache(byte[] stringByte) {
        //        if (stringByte == null) {
        //            return null;
        //        }
        //        int hashCode = Arrays.hashCode(stringByte);
        //        String oriData = stringCache1.get(hashCode);
        //        if (oriData == null) {
        //            oriData = new String(stringByte);
        //            stringCache1.put(hashCode, oriData);
        //        }
        //        return oriData;
        if (stringByte == null)
            return null;
        else
            return new String(stringByte);
    }
    public static byte[] fromCache(String string) {
        if (string == null) {
            return null;
        }
        //        byte[] stringByte = stringCache2.get(string);
        //        if (stringByte == null) {
        //            stringByte = string.getBytes();
        //            stringCache2.put(string, stringByte);
        //        }
        //        return stringByte;
        return string.getBytes();
    }
}
//
//
class LRU<K, V> {
    /**链表元素*/
    private class LRU_Entity {
        public LRU_Entity pre;   //链表前一个元素
        public LRU_Entity next;  //链表后一个元素
        public int        count; //总数
        public boolean    isNew;
        public K          key;   //Key
        public V          val;   //值
    }
    //
    private LRU_Entity root     = null; //链表的起始点
    private int        linkSize = 0;
    private int        maxSize  = 400;
    //
    //
    //
    private LRU_Entity entByKey(K key) {
        if (this.root == null) {
            return null;
        }
        //0.从热端开始搜索目标
        LRU_Entity searchRoot = this.root;
        LRU_Entity currentRoot = searchRoot;
        do {
            //1.判断当前节点是否为目标
            if (key.equals(currentRoot.key)) {
                return currentRoot;
            } else {
                currentRoot = currentRoot.next;
            }
            //2.循环了一圈
            if (currentRoot == searchRoot) {
                break;
            }
        } while (true);
        //
        return null;
    }
    //
    public V get(K key) {
        LRU_Entity valEnt = entByKey(key);
        if (valEnt != null) {
            valEnt.count++;
            return valEnt.val;
        }
        return null;
    }
    //
    public V put(K key, V val) {
        LRU_Entity valEnt = entByKey(key);
        if (valEnt != null) {
            if (valEnt.key.equals(key)) {
                valEnt.count++;
                return valEnt.val;
            } else {
                valEnt.count = 0;
                V oldVal = valEnt.val;
                valEnt.val = val;
                this.moveOrAdd(valEnt);
                return oldVal;
            }
        } else {
            valEnt = new LRU_Entity();
            valEnt.count = 0;
            valEnt.isNew = true;
            valEnt.key = key;
            valEnt.val = val;
            this.moveOrAdd(valEnt);
            return null;
        }
    }
    //
    /*将元素移动或添加到到冷端尾*/
    private void moveOrAdd(LRU_Entity valEnt) {
        weedOut();
        int realSize = linkSize;
        //剔除
        if (!valEnt.isNew) {
            valEnt.pre.next = valEnt.next;
            valEnt.next.pre = valEnt.pre;
            realSize--;
        }
        //新增
        if (this.root == null) {
            this.root = valEnt;
        }
        LRU_Entity root = this.root;
        valEnt.pre = root.pre;
        root.pre = valEnt;
        root.next = valEnt;
        valEnt.next = root;
        //
        this.root = valEnt;
        realSize++;
        linkSize = realSize;
    }
    //
    /*执行淘汰策略*/
    private void weedOut() {
        if (this.linkSize > this.maxSize) {
            //
            System.out.println("weedOut");
            //
        }
    }
}