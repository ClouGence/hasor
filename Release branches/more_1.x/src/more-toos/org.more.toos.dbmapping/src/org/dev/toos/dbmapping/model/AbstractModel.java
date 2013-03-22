package org.dev.toos.dbmapping.model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
/**
 * 
 * @version : 2013-3-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class AbstractModel implements Cloneable, Serializable {
    private static final long     serialVersionUID = 758614355552296549L;
    private PropertyChangeSupport listeners        = new PropertyChangeSupport(this);
    //----------------------------------------------------------------------
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.removePropertyChangeListener(listener);
    }
    /**属性值变化*/
    protected void firePropertyChange(String prop, Object old, Object newValue) {
        this.listeners.firePropertyChange(prop, old, newValue);
    }
    /**属性结构发生变化*/
    protected void fireStructureChange(String prop, Object child) {
        listeners.firePropertyChange(prop, null, child);
    }
    //    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    //        in.defaultReadObject();
    //        listeners = new PropertyChangeSupport(this);
    //    }
}
