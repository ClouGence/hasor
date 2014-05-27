package org.dev.toos.constcode.ui.provider;
import java.util.Arrays;
import java.util.Comparator;
import org.dev.toos.constcode.model.bridge.ConstBeanBridge;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
/**
 * 
 * @version : 2013-2-4
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConstSortProviderProxy implements ITreeContentProvider {
    public static enum SortType {
        Asc, Desc, None
    }
    private ConstTreeProvider target   = null;
    private SortType          sortType = SortType.None;
    //
    public ConstSortProviderProxy(ConstTreeProvider target) {
        this.target = target;
    }
    public void sortBy(SortType sortType) {
        this.sortType = sortType;
    }
    /**获取排序方式*/
    public SortType getSortType() {
        return sortType;
    }
    //
    private ConstBeanBridge[] sortList(ConstBeanBridge[] dataList) {
        if (dataList == null)
            return null;
        Arrays.sort(dataList, new Comparator<ConstBeanBridge>() {
            @Override
            public int compare(ConstBeanBridge o1, ConstBeanBridge o2) {
                String str_1 = o1.getCode();
                String str_2 = o2.getCode();
                if (str_1 == null || str_2 == null)
                    return 0;
                int compareToRes = str_1.compareTo(str_2);
                if (compareToRes != 0)
                    if (sortType == SortType.Asc)
                        compareToRes = (compareToRes > 0) ? 1 : 0;
                    else if (sortType == SortType.Desc)
                        compareToRes = (compareToRes > 0) ? 0 : 1;
                    else if (sortType == SortType.None)
                        compareToRes = 0;
                return compareToRes;
            }
        });
        return dataList;
    }
    //
    //
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.target.inputChanged(viewer, oldInput, newInput);
    }
    @Override
    public ConstBeanBridge[] getElements(Object inputElement) {
        if (this.sortType == null || this.sortType == SortType.None)
            return this.target.getElements(inputElement);
        else
            return this.sortList(this.target.getElements(inputElement));
    }
    @Override
    public ConstBeanBridge[] getChildren(Object parentElement) {
        if (this.sortType == null || this.sortType == SortType.None)
            return this.target.getChildren(parentElement);
        else
            return this.sortList(this.target.getChildren(parentElement));
    }
    @Override
    public boolean hasChildren(Object element) {
        return this.target.hasChildren(element);
    }
    @Override
    public ConstBeanBridge getParent(Object element) {
        return this.target.getParent(element);
    }
    @Override
    public void dispose() {
        this.target.dispose();
    }
}