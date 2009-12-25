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
package org.more.dbpool;
import java.sql.Connection;
import java.util.Date;
import org.more.dbpool.exception.AllotAndRepeatException;
import org.more.dbpool.exception.AllotException;
import org.more.dbpool.exception.RepeatIsZeroException;
import org.more.dbpool.exception.ResLostnException;
/**
 * 连接池中的一个连接类型
 * Date : 2009-5-17
 * @author 赵永春
 */
public class ConnStruct {
    private boolean    _enable    = true; //是否失效
    private boolean    _isUse     = false; //是否正在被使用中
    private boolean    _allot     = true; //表示该连接是否可以被分配
    private Date       _createTime;       //创建时间
    private int        _useDegree = 0;    //被使用次数
    private int        _repeatNow = 0;    //当前连接被重复引用多少
    private boolean    _isRepeat  = true; //连接是否可以被重复引用，当被分配出去的连接可能使用事务时，该属性被标识为true
    private int        _connType;         //连接类型
    private Connection _connect   = null; //连接对象
    /**
     * 连接池中的连接
     * @param dbc Connection 数据库连接
     */
    public ConnStruct(Connection dbc) {
        InitConnStruct(dbc, new Date());
    }
    /**
     * 连接池中的连接
     * @param dbc Connection  数据库连接
     * @param dt Date 连接创建时间
     */
    public ConnStruct(Connection dbc, Date dt) {
        InitConnStruct(dbc, dt);
    }
    /**
     * 连接池中的连接
     * @param dbc Connection  数据库连接
     * @param dt Date 连接创建时间
     */
    private void InitConnStruct(Connection dbc, Date dt) {
        _createTime = dt;
        _connect = dbc;
    }
    //--------------------------------------------------------------------
    /**
     * 得到一个值表示该连接是否可以被分配
     * @return boolean 该连接是否可以被分配
     */
    public boolean GetAllot() {
        return _allot;
    }
    /**
     * 设置一个值表示该连接是否可以被分配
     * @param value boolean true可以被分配，false不可以被分配
     */
    public void SetAllot(boolean value) {
        _allot = value;
    }
    /**
     * 得到当前连接是否失效
     * @return boolean 得到当前连接是否失效；false表示失效，只读
     */
    public boolean GetEnable() {
        return _enable;
    }
    /**
     * 得到当前连接是否正在被使用中，只读
     * @return boolean 当前连接是否正在被使用中
     */
    public boolean GetIsUse() {
        return _isUse;
    }
    /**
     * 得到连接创建时间，只读
     * @return Date 创建时间
     */
    public Date GetCreateTime() {
        return _createTime;
    }
    /**
     * 得到被使用次数，只读
     * @return int 得到被使用次数
     */
    public int GetUseDegree() {
        return _useDegree;
    }
    /**
     * 得到当前连接被重复引用多少，只读
     * @return int 当前连接被重复引用多少
     */
    public int GetRepeatNow() {
        return _repeatNow;
    }
    /**
     * 得到该连接，只读
     * @return Connection 连接
     */
    public Connection GetConnection() {
        return _connect;
    }
    /**
     * 得到连接是否可以被重复引用
     * @return boolean 是否可以被重复引用
     */
    public boolean GetIsRepeat() {
        return _isRepeat;
    }
    /**
     * 设置连接是否可以被重复引用
     * @param value boolean true可以被重复引用，false不可以被重复引用
     */
    public void SetIsRepeat(boolean value) {
        _isRepeat = value;
    }
    /**
     * 得到连接类型ConnectionPool.ConnType_*，只读
     * @return int 连接类型
     */
    public int GetConnType() {
        return _connType;
    }
    /**
     * 关闭数据库连接
     */
    public void Close() {
        try {
            _connect.close();
        } catch (Exception e) {}
    }
    /**
     * 无条件将连接设置为失效
     */
    public void SetConnectionLost() {
        _enable = false;
        _allot = false;
    }
    /**
     * 被分配出去，线程安全的
     * @throws ResLostnExecption
     * @throws AllotExecption
     * @throws AllotAndRepeatExecption
     */
    public synchronized void Repeat() throws ResLostnException, AllotException, AllotAndRepeatException {
        if (_enable == false) //连接可用
            throw new ResLostnException(); //连接资源已经失效
        if (_allot == false) //是否可以被分配
            throw new AllotException(); //连接资源不可以被分配
        if (_isUse == true && _isRepeat == false)
            throw new AllotAndRepeatException(); //连接资源已经被分配并且不允许重复引用
        _repeatNow++; //引用记数+1
        _useDegree++; //被使用次数+1
        _isUse = true; //被使用
    }
    /**
     * 被释放回来，线程安全的
     * @throws ResLostnExecption 连接资源已经失效
     * @throws RepeatIsZeroExecption 引用记数已经为0
     */
    public synchronized void Remove() throws ResLostnException, RepeatIsZeroException {
        if (_enable == false) //连接可用
            throw new ResLostnException(); //连接资源已经失效
        if (_repeatNow == 0)
            throw new RepeatIsZeroException(); //引用记数已经为0
        _repeatNow--; //引用记数-1
        if (_repeatNow == 0)
            _isUse = false; //未使用
        else
            _isUse = true; //使用中
    }
    /// <summary>
    /// 释放资源
    /// </summary>
    public void Dispose() {
        _enable = false;
        try {
            _connect.close();
        } catch (Exception e) {}
        _connect = null;
    }
}
