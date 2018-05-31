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
package net.hasor.registry.storage.btree;
import net.hasor.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
/**
 * B-Tree 索引操作类
 * @version : 2018年5月28日
 * @author 赵永春 (zyc@hasor.net)
 */
public class SliceAdapter {
    private final int splitSize; // 最好是单数，这样新数据加入之后再分裂左右是平衡的
    private long               enteryKey = 0;
    //
    private Map<String, Slice> sliceMap  = new HashMap<String, Slice>();
    private int[]              slicePool = new int[10240];
    public SliceAdapter(int splitSize) {
        if (splitSize < 3) {
            throw new IndexOutOfBoundsException("splitSize < 3");
        }
        this.splitSize = splitSize;
        for (int i = 0; i < slicePool.length; i++) {
            slicePool[i] = -1;
        }
    }
    public Slice getEntryPoint() {
        //
        Slice root = getSlice(this.enteryKey);
        if (root != null) {
            return root;
        }
        root = new Slice(-1);
        root.setSliceID(requestNewSliceID());
        root.setChildrensKeys(new Node[] {});
        //
        enteryKey = root.getSliceID();
        sliceMap.put(String.valueOf(enteryKey), root);
        //
        return root;
    }
    private void updataEntryPoint(int sliceID) {
        this.enteryKey = sliceID;
    }
    //
    protected int requestNewSliceID() {
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < slicePool.length; i++) {
            if (slicePool[i] < 0) {
                slicePool[i] = random.nextInt();
                return i;
            }
        }
        throw new RuntimeException("no rich Slice.");
    }
    protected void releaseSliceID(int sliceID) {
        System.out.println("release-" + sliceID);
        slicePool[sliceID] = -1;
        sliceMap.remove(String.valueOf(sliceID));
    }
    protected Slice getSlice(long sliceID) {
        return sliceMap.get(String.valueOf(sliceID));
    }
    protected long storeSlice(Slice sliceData) {
        long sliceID = sliceData.getSliceID();
        sliceMap.put(String.valueOf(sliceID), sliceData);
        return sliceID;
    }
    //
    //
    //
    //
    public int getDepth(long sliceID) {
        Slice slice = this.getSlice(sliceID);
        int depth = 1;
        while (slice.getParent() > 0) {
            depth++;
            slice = this.getSlice(slice.getParent());
        }
        return depth;
    }
    //
    // -最近的（可操作）
    public ResultSlice nearSlice(long hashKey) {
        Slice slice = this.getEntryPoint();
        return nearSlice(hashKey, slice, 0);
    }
    private ResultSlice nearSlice(long hashKey, Slice atSlice, int positionOfatSlice) {
        //
        // .已经是叶子节点了，不可能在遍历了
        Node[] childrensNodes = atSlice.getChildrensKeys();
        if (childrensNodes.length == 0) {
            return new ResultSlice(atSlice, positionOfatSlice);
        }
        //
        for (int i = 0; i < childrensNodes.length; i++) {
            Node itemNode = childrensNodes[i];
            // 小于itemKey，证明 hashKey 应该放在这个分支下
            if (hashKey < itemNode.getDataKey()) {
                if (!itemNode.isData()) {
                    Slice itemSlice = this.getSlice(itemNode.getPosition());
                    return nearSlice(hashKey, itemSlice, i); // itemSlice 没有子节点
                }
                return new ResultSlice(atSlice, i);
            }
            // 等于，命中 atSlice 就是要找的
            if (itemNode.getDataKey() == hashKey) {
                return new ResultSlice(atSlice, i);
            }
        }
        //
        // .最右侧的数据都比 hashKey 小，那说明只能添加到 atSlice 上
        return new ResultSlice(atSlice, childrensNodes.length);
    }
    //
    public ResultSlice insertData(DataNode dataSlice) {
        // .找到最近的 Slice
        ResultSlice resultSlice = this.nearSlice(dataSlice.getDataKey());
        Slice atSlice = resultSlice.getAtSlice();
        int atPosition = resultSlice.getAtPosition();
        //
        Node[] childrensNodes = atSlice.getChildrensKeys();
        Node[] new_childrensNodes = new Node[childrensNodes.length + 1];
        //
        new_childrensNodes[atPosition] = dataSlice;
        //
        System.arraycopy(childrensNodes, 0, new_childrensNodes, 0, atPosition);
        System.arraycopy(childrensNodes, atPosition, new_childrensNodes, atPosition + 1, childrensNodes.length - atPosition);
        //
        atSlice.setChildrensKeys(new_childrensNodes);
        //
        return splitSlice(resultSlice, this.splitSize);
    }
    private void insertToParent(TreeNode treeNode, Slice atSlice) {
        Node[] childrensNodes = atSlice.getChildrensKeys();
        int atPosition = -1;
        for (int i = 0; i < childrensNodes.length; i++) {
            atPosition = i;
            Node itemNode = childrensNodes[i];
            if (treeNode.getDataKey() < itemNode.getDataKey()) {
                break;
            }
        }
        //
        Node[] new_childrensNodes = new Node[childrensNodes.length + 1];
        new_childrensNodes[atPosition] = treeNode;
        //
        System.arraycopy(childrensNodes, 0, new_childrensNodes, 0, atPosition);
        System.arraycopy(childrensNodes, atPosition, new_childrensNodes, atPosition + 1, childrensNodes.length - atPosition);
        //
        atSlice.setChildrensKeys(new_childrensNodes);
        //
        splitSlice(new ResultSlice(atSlice, atPosition), this.splitSize);
    }
    //
    // -根据分裂因子进行分裂，分裂后返回依据的父节点
    public ResultSlice splitSlice(ResultSlice sliceResult, int maxRecord) {
        //
        // .容量还够的情况下不进行分裂
        Slice slice = sliceResult.getAtSlice();
        Node[] childrens = slice.getChildrensKeys();
        if (childrens.length <= maxRecord || maxRecord < 1) {
            return sliceResult;
        }
        //
        // split Data
        int splitPoint = childrens.length / 2;
        Node[] leftKeys = new Node[splitPoint];
        System.arraycopy(childrens, 0, leftKeys, 0, leftKeys.length);
        Slice leftSlice = new Slice(slice.getParent());
        leftSlice.setChildrensKeys(leftKeys);
        leftSlice.setSliceID(this.requestNewSliceID());
        //
        Node[] rightKeys = new Node[childrens.length - splitPoint];
        System.arraycopy(childrens, rightKeys.length, rightKeys, 0, childrens.length - rightKeys.length);
        Slice rightSlice = new Slice(slice.getParent());
        rightSlice.setChildrensKeys(rightKeys);
        rightSlice.setSliceID(this.requestNewSliceID());
        //
        // .重新计算 sliceResult
        if (sliceResult.getAtPosition() < splitPoint) {
            sliceResult = new ResultSlice(leftSlice, sliceResult.getAtPosition());
        } else {
            sliceResult = new ResultSlice(rightSlice, sliceResult.getAtPosition() - splitPoint);
        }
        //
        // .决定是否增加树的高度
        if (slice.getParent() < 0) {
            Slice newParent = new Slice(-1);
            newParent.setSliceID(this.requestNewSliceID());
            newParent.setChildrensKeys(new Node[] { //
                    new TreeNode(leftKeys[leftKeys.length - 1].getDataKey(), leftSlice.getSliceID()),//
                    new TreeNode(rightKeys[rightKeys.length - 1].getDataKey(), rightSlice.getSliceID()),//
            });
            leftSlice.setParent(newParent.getSliceID());
            rightSlice.setParent(newParent.getSliceID());
            //
            this.storeSlice(leftSlice);
            this.storeSlice(rightSlice);
            this.storeSlice(newParent);
            this.updataEntryPoint(newParent.getSliceID());
            this.releaseSliceID(slice.getSliceID());
            //
            return sliceResult;
        }
        //
        // .不需要增加树的高度，那么向父 Slice 插入数据
        rightSlice.setSliceID(slice.getSliceID());
        this.storeSlice(leftSlice);
        this.storeSlice(rightSlice);
        this.insertToParent(//
                new TreeNode(leftKeys[leftKeys.length - 1].getDataKey(), leftSlice.getSliceID()),//
                this.getSlice(slice.getParent())//
        );
        //
        return sliceResult;
    }
    //
    //
    //
    //
    //
    //
    //
    //
    //
    protected void printNodeTree(int sliceID, int depth) {
        depth = (depth < 1) ? 1 : depth;
        Slice slice = getSlice(sliceID);
        if (slice == null) {
            System.out.println(sliceID);
        }
        for (Node node : slice.getChildrensKeys()) {
            if (node.isData()) {
                System.out.println("Data" + StringUtils.fixedString(' ', depth * 3) + node.getDataKey() + "\tslice-(" + slice.getSliceID() + ")");
            } else {
                System.out.println("Index" + StringUtils.fixedString(' ', depth * 3) + node.getDataKey() + "\tslice-(" + slice.getSliceID() + ")");
                printNodeTree((int) node.getPosition(), depth + 1);
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        SliceAdapter sliceAdapter = new SliceAdapter(5);
        //
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 30; i++) {
            long randomLong = random.nextLong();
            DataNode data = new DataNode(randomLong);
            sliceAdapter.insertData(data);
        }
        //
        System.out.println("enteryKey = " + sliceAdapter.enteryKey + " ,depth = " + sliceAdapter.getDepth(sliceAdapter.enteryKey));
        sliceAdapter.printNodeTree((int) sliceAdapter.enteryKey, 1);
        Thread.sleep(1000);
    }
}