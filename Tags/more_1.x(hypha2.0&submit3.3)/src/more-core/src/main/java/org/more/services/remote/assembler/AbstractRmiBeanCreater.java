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
package org.more.services.remote.assembler;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import org.more.core.classcode.BuilderMode;
import org.more.core.classcode.ClassEngine;
import org.more.core.classcode.RootClassLoader;
import org.more.core.error.FormatException;
import org.more.services.remote.RmiBeanCreater;
import org.more.services.remote.client.FacesMachining;
/**
* 该类是{@link RmiBeanCreater}接口的基实现，主要作用是将一个任意对象和接口绑定，生称他们的一个RMI代理对象。
* @version : 2011-8-16
* @author 赵永春 (zyc@byshell.org)
*/
public abstract class AbstractRmiBeanCreater implements RmiBeanCreater {
    private FacesMachining  factory    = null;
    private RootClassLoader rootLoader = null;
    public AbstractRmiBeanCreater() {
        /*RootClassLoader在生成代理类的时候会读取生成的接口，因此确保生称的代理类可以读取到接口采用下列Loader继承关系。*/
        this.factory = new FacesMachining(Thread.currentThread().getContextClassLoader());
        this.rootLoader = new RootClassLoader(this.factory);
    }
    /*
     * RMI的实现类必须继承自UnicastRemoteObject，但无法保证实现类也是来自于该类的子类。
     * 因此代理原型只负责生成一个继承自UnicastRemoteObject并且实现了若干接口的代理类。
     * 再由这个代理类去调用目标对象。这个对接过程不能保证代理对象可以强制转换成代理对象的类型。（特此说明）
     */
    /**根据对象和要预期实现的接口创建一个接口代理对象，该对象不能被强制转换为obj所表示的类型，但可以转换成为faces中的任意一个类型。*/
    protected Remote getRemoteByFaces(Object obj, Class<?>[] faces) throws ClassNotFoundException, RemoteException, IOException {
        ClassEngine ce = new ClassEngine();
        ce.setRootClassLoader(this.rootLoader);
        RmiFaceDelegate rfd = new RmiFaceDelegate();
        ce.setSuperClass(RmiObjectPropxy.class);
        ce.setBuilderMode(BuilderMode.Propxy);
        //逐一添加要实现的接口。
        if (faces != null)
            for (Class<?> face : faces)
                if (face.isInterface() == false)
                    throw new FormatException(face + "，接口并非一个接口类型。");
                else
                    ce.addDelegate(face, rfd);
        RmiObjectPropxy propxy = new RmiObjectPropxy();
        propxy.setTarget(obj);
        return (Remote) ce.newInstance(propxy);
    };
    /**处理faces，将faces转换为可以曝露的Remote远程接口。*/
    protected Class<?>[] getRemoteFaces(Class<?>[] faces) throws Throwable {
        Class<?>[] newfaces = new Class<?>[faces.length];
        for (int i = 0; i < faces.length; i++) {
            Class<?> cls = faces[i];
            if (Remote.class.isAssignableFrom(cls) == false)
                newfaces[i] = this.factory.createFaces(cls);
            else
                newfaces[i] = cls;
        }
        return newfaces;/*TODO 接口到接口的转换*/
    };
};
