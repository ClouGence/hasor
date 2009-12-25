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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import org.more.dbpool.exception.ConnLevelException;
import org.more.dbpool.exception.CreateConnectionException;
import org.more.dbpool.exception.KeyException;
import org.more.dbpool.exception.NotKeyException;
import org.more.dbpool.exception.OccasionException;
import org.more.dbpool.exception.ParameterBoundException;
import org.more.dbpool.exception.PoolFullException;
import org.more.dbpool.exception.PoolNotRunException;
import org.more.dbpool.exception.PoolNotStopException;
import org.more.dbpool.exception.ResCallBackException;
import org.more.dbpool.exception.StateException;
/**
 * 
 * Date : 2009-5-17
 * @author Administrator
 */
public class ConnectionPool {
    /**
     * <b>独占方式</b><br>
     * 使用空闲的实际连接分配连接资源，并且在该资源释放回之前，该资源在连接池中将不能将其引用分配给其他申请者。<br>
     * 如果连接池中所有实际连接资源都已经分配出去，那么即使连接池可以在分配引用资源在该模式下连接迟将不会分配连接资源，
     * 连接池会产生一个异常，标志连接池资源耗尽。<br><br>
     * <font color='#006600'>例：假如一个实际连接可以被分配5次，那么使用该模式申请连接的话您将损失4个可分配的连接，只将得到一个连接资源。
     * 直至该资源被释放回连接池，连接池才继续分配它剩余的4次机会。</font>
     * <br><br>
     * 当您在使用连接时可能应用到事务时，可以使用该模式的连接，以确定在事务进行期间您可以对该连接具有独享权限，以避免各个数据库操作访问的干扰。
     */
    public static final int               ConnLevel_ReadOnly     = 10;
    /**
     * <b>优先级-高</b><br>
     * 使用空闲的实际连接分配连接资源，并且在该资源释放回之前，该资源在连接池中将可能将其引用分配给其他申请者。<br>
     * <font color='#FF0000'>*注意：此级别不保证在分配该资源后，仍然保持独立占有连接资源，若想独立占有资源请使用ReadOnely，
     * 因为当连接池达到某一时机时该资源将被重复分配（引用记数）然而这个时机是不可预测的。</font><br>
     * 如果您申请的连接会用于事务处理您可以使用<font color='#0033CC'>ConnLevel_ReadOnly</font>级别
     */
    public static final int               ConnLevel_High         = 11;
    /**
     * <b>优先级-中</b><br>
     * 适当应用引用记数技术分配连接资源。<br>
     * 在该模式下，连接池内部会按照实际连接已经使用次数排序(多->少)，然后在结果中选取 1/3 位置的连接资源返回。<br>
     * 与优先级-高相同该模式也不具备保持独立占有连接资源的特性。<br>
     * 如果您申请的连接会用于事务处理您可以使用<font color='#0033CC'>ConnLevel_ReadOnly</font>级别
     */
    public static final int               ConnLevel_None         = 12;
    /**
     * <b>优先级-底</b><br>
     * 尽可能使用引用记数技术分配连接。<br>
     * 在该模式下，连接池内部会按照实际连接已经使用次数排序(多->少)，然后在结果中选取被使用最多的返回。<br>
     * 该模式适合处理较为不重要的连接资源请求。<br>
     * 与优先级-高相同该模式也不具备保持独立占有连接资源的特性。<br>
     * 如果您申请的连接会用于事务处理您可以使用<font color='#0033CC'>ConnLevel_ReadOnly</font>级别
     */
    public static final int               ConnLevel_Bottom       = 13;
    /**
     * 表示未对连接池未被调用过StartSeivice方法。
     */
    public static final int               PoolState_UnInitialize = 20;
    /**
     * 连接池初始化中，该状态下服务正在按照参数初始化连接池。StopServices之后将首先跳转到该状态
     */
    public static final int               PoolState_Initialize   = 21;
    /**
     * 当连接池开始运做时则表示为该状态
     */
    public static final int               PoolState_Run          = 22;
    /**
     * 连接池被调用StopServices停止状态
     */
    public static final int               PoolState_Stop         = 23;
    //属性
    private int                           _RealFormPool;                                               //连接池中存在的实际连接数(包含失效的连接)
    private int                           _PotentRealFormPool;                                         //连接池中存在的实际连接数(有效的实际连接)
    private int                           _SpareRealFormPool;                                          //空闲的实际连接
    private int                           _UseRealFormPool;                                            //已分配的实际连接
    private int                           _ReadOnlyFormPool;                                           //连接池已经分配多少只读连接
    private int                           _UseFormPool;                                                //已经分配出去的连接数
    private int                           _SpareFormPool;                                              //目前可以提供的连接数
    private int                           _MaxConnection;                                              //最大连接数，最大可以创建的连接数目
    private int                           _MinConnection;                                              //最小连接数
    private int                           _SeepConnection;                                             //每次创建连接的连接数
    private int                           _KeepRealConnection;                                         //保留的实际空闲连接，以攻可能出现的ReadOnly使用，当空闲连接不足该数值时，连接池将创建seepConnection个连接
    private int                           _Exist                 = 20;                                 //每个连接生存期限 20分钟
    private String                        _userID                = "";
    private String                        _password              = "";
    //可以被重复使用次数（引用记数），当连接被重复分配该值所表示的次数时，该连接将不能被分配出去
    //当连接池的连接被分配尽时，连接池会在已经分配出去的连接中，重复分配连接（引用记数）。来缓解连接池压力
    private int                           _MaxRepeatDegree       = 5;
    private Date                          _StartTime;                                                  //服务启动时间
    private String                        _ConnString            = null;                               //连接字符串
    private String                        _DriveString           = null;                               //驱动字符串
    private int                           _PoolState;                                                  //连接池状态
    //内部对象
    private ArrayList<ConnStruct>         al_All                 = new ArrayList<ConnStruct>(0);       //实际连接
    private Hashtable<Object, ConnStruct> hs_UseConn             = new Hashtable<Object, ConnStruct>(); //正在使用的连接
    private CreateThreadProcess           threadCreate;
    private CheckThreadProcess            threadCheck;
    //-------------------------------------------------------------------------------------------
    /**
     * 初始化连接池
     */
    public ConnectionPool() {
        InitConnectionPool("", "", 200, 30, 10, 5);
    }
    /**
     * 初始化连接池
     * @param connectionString String 数据库连接字符串。
     * @param driveString String 数据库驱动字符串。
     */
    public ConnectionPool(String connectionString, String driveString) {
        InitConnectionPool(connectionString, driveString, 200, 30, 10, 5);
    }
    /**
     * 初始化连接池
     * @param connectionString String 数据库连接字符串。
     * @param driveString String 数据库驱动字符串。
     * @param maxConnection int 最大实际连接数，最大可以创建的连接数目。
     * @param minConnection int 最小实际连接数。
     */
    public ConnectionPool(String connectionString, String driveString, int maxConnection, int minConnection) {
        InitConnectionPool(connectionString, driveString, maxConnection, minConnection, 10, 5);
    }
    /**
     * 初始化连接池
     * @param connectionString String 数据库连接字符串。
     * @param driveString String 数据库驱动字符串。
     * @param maxConnection int 最大实际连接数，最大可以创建的连接数目。
     * @param minConnection int 最小实际连接数。
     * @param seepConnection int 每次创建连接的连接数。当空闲的实际连接不足该值时创建连接，直到达到最大连接数
     * @param keepRealConnection int 保留连接数，当空闲连接不足该数值时，连接池将创建seepConnection个连接。
     */
    public ConnectionPool(String connectionString, String driveString, int maxConnection, int minConnection, int seepConnection, int keepRealConnection) {
        InitConnectionPool(connectionString, driveString, maxConnection, minConnection, seepConnection, keepRealConnection);
    }
    /**
     * 初始化连接池
     * @param connectionString String 数据库连接字符串。
     * @param driveString String 数据库驱动字符串。
     * @param maxConnection int 最大实际连接数，最大可以创建的连接数目。
     * @param minConnection int 最小实际连接数。
     * @param seepConnection int 每次创建连接的连接数。当空闲的实际连接不足该值时创建连接，直到达到最大连接数
     * @param keepRealConnection int 保留连接数，当空闲连接不足该数值时，连接池将创建seepConnection个连接。
     */
    private void InitConnectionPool(String connectionString, String driveString, int maxConnection, int minConnection, int seepConnection, int keepRealConnection) {
        this._PoolState = PoolState_UnInitialize;
        this._ConnString = connectionString;
        this._DriveString = driveString;
        this._MinConnection = minConnection;
        this._SeepConnection = seepConnection;
        this._KeepRealConnection = keepRealConnection;
        this._MaxConnection = maxConnection;
        this.threadCheck = new CheckThreadProcess();
        this.threadCheck.setDaemon(true);
        this.threadCreate = new CreateThreadProcess();
        this.threadCreate.setDaemon(true);
        this.threadCheck.start();
        while (threadCheck.getState() != Thread.State.WAITING) {}
    }
    //-------------------------------------------------------------------------------------------
    /**
     * <p>创建线程类</p>
     */
    private class CreateThreadProcess extends Thread {
        public int     createThreadMode        = 0;    //创建线程工作模式
        public int     createThreadProcessTemp = 0;    //需要创建的连接数
        public boolean createThreadProcessRun  = false; //是否决定创建线程将继续工作，如果不继续工作则线程会将自己处于阻止状态
        public void run() {
            boolean join = false;
            int createThreadProcessTemp_inside = createThreadProcessTemp;
            _PoolState = PoolState_Initialize;
            while (true) {
                join = false;
                _PoolState = PoolState_Run;
                if (createThreadProcessRun == false) {//遇到终止命令
                    try {
                        this.join();//中断自己
                    } catch (Exception e) {/* */}
                } else {
                    if (createThreadMode == 0) {
                        //------------------------begin mode  创建模式
                        synchronized (al_All) {
                            if (al_All.size() < createThreadProcessTemp_inside) {
                                ConnStruct cs = CreateConnectionTemp(_ConnString, _DriveString, _userID, _password);
                                if (cs != null)
                                    al_All.add(cs);
                            } else
                                join = true;
                        }
                        //------------------------end mode
                    } else if (createThreadMode == 1) {
                        //------------------------begin mode  增加模式
                        synchronized (al_All) {
                            if (createThreadProcessTemp_inside != 0) {
                                createThreadProcessTemp_inside--;
                                ConnStruct cs = CreateConnectionTemp(_ConnString, _DriveString, _userID, _password);
                                if (cs != null)
                                    al_All.add(cs);
                            } else
                                join = true;
                        }
                        //------------------------end mode
                    } else
                        join = true;
                    //-------------------------------------------------------------------------
                    if (join == true) {
                        UpdateAttribute();//更新属性
                        try {
                            createThreadProcessTemp = 0;
                            this.join(); //中断自己
                        } catch (Exception e) {
                            createThreadProcessTemp_inside = createThreadProcessTemp;
                        } //得到传入的变量
                    }
                }
            }
        }
    }
    /**
     * <p>检测事件</p>
     */
    private class CheckThreadProcess extends Thread {
        private long    _Interval = 100;  //执行间隔
        private boolean timeSize  = false;
        /**
         * 启动记时器
         */
        public void StartTimer() {
            timeSize = true;
            if (this.getState() == Thread.State.NEW)
                this.start();
            else if (this.getState() == Thread.State.WAITING)
                this.interrupt();
        }
        /**
         * 停止记时器
         */
        public void StopTimer() {
            timeSize = false;
            while (this.getState() != Thread.State.WAITING) {/**/}
        }
        public void aRun() {
            ConnStruct cs = null;
            //如果正在执行创建连接则退出
            if (threadCreate.getState() != Thread.State.WAITING)
                return;
            //------------------------------------------------------
            synchronized (al_All) {
                for (int i = 0; i < al_All.size(); i++) {
                    cs = al_All.get(i);
                    TestConnStruct(cs);//测试
                    if (cs.GetEnable() == false && cs.GetRepeatNow() == 0)//没有引用的失效连接
                    {
                        cs.Close();//关闭它
                        al_All.remove(cs);//删除
                    }
                }
            }
            //------------------------------------------------------
            UpdateAttribute();//更新属性
            if (_SpareRealFormPool < _KeepRealConnection)//保留空闲实际连接数不足
                threadCreate.createThreadProcessTemp = GetNumOf(_RealFormPool, _SeepConnection, _MaxConnection);
            else
                threadCreate.createThreadProcessTemp = 0;
            //if (threadCreate.createThreadProcessTemp != 0)
            //{
            //    System.out.println("创建" + threadCreate.createThreadProcessTemp);
            //    System.out.println(threadCreate.getState()+ " this " + this.getState());
            //}
            if (threadCreate.createThreadProcessTemp != 0) {
                //启动创建线程，工作模式1
                threadCreate.createThreadMode = 1;
                threadCreate.interrupt();
            }
        }
        public void run() {
            while (true) {
                try {
                    this.join(_Interval);
                    if (timeSize == true)
                        aRun();
                    else
                        this.join();
                } catch (InterruptedException ex1) {/**/}
            }
        }
        /**
         * 设置执行时间间隔
         * @param value double 时间间隔
         */
        public void setInterval(long value) {
            _Interval = value;
        }
        /**
         * 获得执行时间间隔
         * @return double 时间间隔
         */
        public long getInterval() {
            return _Interval;
        }
        /**
         * 得到当前要增加的量
         * @param nowNum int 当前值
         * @param seepNum int 步长
         * @param maxNum int 最大值
         * @return int 当前要增加的量
         */
        private int GetNumOf(int nowNum, int seepNum, int maxNum) {
            if (maxNum >= nowNum + seepNum)
                return seepNum;
            else
                return maxNum - nowNum;
        }
    }
    //-------------------------------------------------------------------------------------------
    /**
     * 设置连接池字符串
     * @param _ConnString String 连接字符串
     * @param _DriveString String 驱动字符串
     * @throws StateException 服务状态错误
     */
    public void set_String(String _ConnString, String _DriveString) throws StateException {
        if (_ConnString != null && _DriveString != null && (_PoolState == PoolState_UnInitialize || _PoolState == PoolState_Stop)) {
            this._ConnString = _ConnString;
            this._DriveString = _DriveString;
        } else {
            throw new StateException(); //服务状态错误
        }
    }
    /**
     * 设置每个连接生存期限(单位分钟)，默认20分钟
     * @param _Exist int 连接生存期限
     * @throws StateException 服务状态错误
     */
    public void set_Exist(int _Exist) throws StateException {
        if (_PoolState == PoolState_Stop) {
            this._Exist = _Exist;
        } else {
            throw new StateException(); //服务状态错误
        }
    }
    /**
     * 设置最小实际连接数
     * @param _MinConnection int 最小实际连接数
     * @throws ParameterBoundExecption 参数范围应该在 0~MaxConnection之间，并且应该大于KeepConnection
     */
    public void set_MinConnection(int _MinConnection) throws ParameterBoundException {
        if (_MinConnection < _MaxConnection && _MinConnection > 0 && _MinConnection >= _KeepRealConnection)
            this._MinConnection = _MinConnection;
        else
            throw new ParameterBoundException(); //参数范围应该在 0~MaxConnection之间，并且应该大于KeepConnection
    }
    /**
     * 可以被重复使用次数（引用记数）当连接被重复分配该值所表示的次数时，该连接将不能被分配出去。
     * 当连接池的连接被分配尽时，连接池会在已经分配出去的连接中，重复分配连接（引用记数）。来缓解连接池压力
     * @param _MaxRepeatDegree int 引用记数
     * @throws ParameterBoundExecption 重复引用次数应大于0
     */
    public void set_MaxRepeatDegree(int _MaxRepeatDegree) throws ParameterBoundException {
        if (_MaxRepeatDegree > 0)
            this._MaxRepeatDegree = _MaxRepeatDegree;
        else
            throw new ParameterBoundException(); //重复引用次数应大于0
    }
    /**
     * 设置最大可以创建的实际连接数目
     * @param _MaxConnection int 最大可以创建的实际连接数目
     * @throws ParameterBoundExecption 参数范围错误，参数应该大于MinConnection
     */
    public void set_MaxConnection(int _MaxConnection) throws ParameterBoundException {
        if (_MaxConnection > _MinConnection && _MaxConnection > 0)
            this._MaxConnection = _MaxConnection;
        else
            throw new ParameterBoundException(); //参数范围错误，参数应该大于MinConnection
    }
    /**
     * 设置保留的实际空闲连接，以供可能出现的ReadOnly使用
     * @throws ParameterBoundExecption 保留连接数应大于等于0，同时小于MaxConnection
     */
    public void set_KeepRealConnection(int _KeepRealConnection) throws ParameterBoundException {
        if (_KeepRealConnection >= 0 && _KeepRealConnection < _MaxConnection)
            this._KeepRealConnection = _KeepRealConnection;
        else
            throw new ParameterBoundException(); //保留连接数应大于等于0，同时小于MaxConnection
    }
    /**
     * 设置每次创建连接的连接数
     * @param _SeepConnection int 每次创建连接的连接数
     * @throws ParameterBoundExecption 保留连接数应大于等于0，同时小于MaxConnection
     */
    public void set_SeepConnection(int _SeepConnection) throws ParameterBoundException {
        if (_SeepConnection > 0 && _SeepConnection < _MaxConnection)
            this._SeepConnection = _SeepConnection;
        else
            throw new ParameterBoundException(); //保留连接数应大于等于0，同时小于MaxConnection
    }
    /**
     * 设置登陆数据库的密码
     * @param userID String 数据库登陆帐户
     * @param password String 数据库登陆密码
     * @throws PoolNotStopException 服务尚在运行中
     */
    public void set_DataBaseUser(String userID, String password) throws PoolNotStopException {
        if (_PoolState == PoolState_UnInitialize || _PoolState == PoolState_Stop) {
            this._userID = userID;
            this._password = password;
        } else
            throw new PoolNotStopException();
    }
    /**
     * 得到自动清理连接池的时间间隔
     * @return double 自动清理连接池的时间间隔
     */
    public long get_Interval() {
        return threadCheck.getInterval();
    }
    /**
     * 设置自动清理连接池的时间间隔
     * @param value int 保留的实际空闲连接数
     */
    public void set_Interval(long value) {
        threadCheck.setInterval(value);
    }
    /**
     * 得到连接池使用的连接字符串
     * @return String 连接字符串
     */
    public String get_ConnString() {
        return _ConnString;
    }
    /**
     * 得到连接池使用的驱动字符串
     * @return String 连接池使用的驱动字符串
     */
    public String get_DriveString() {
        return _DriveString;
    }
    /**
     * 得到每个连接生存期限(单位分钟)，默认20分钟
     * @return int 每个连接生存期限(单位分钟)
     */
    public int get_Exist() {
        return _Exist;
    }
    /**
     * 获得连接池已经分配多少只读连接
     * @return int 已经分配多少只读连接
     */
    public int get_ReadOnlyFormPool() {
        return _ReadOnlyFormPool;
    }
    /**
     * 连接池中存在的实际连接数(包含失效的连接)
     * @return int 实际连接数
     */
    public int get_RealFormPool() {
        return _RealFormPool;
    }
    /**
     * 得到每次创建连接的连接数
     * @return int 每次创建连接的数目
     */
    public int get_SeepConnection() {
        return _SeepConnection;
    }
    /**
     * 得到目前可以提供的连接数
     * @return int 得到目前可以提供的连接数
     */
    public int get_SpareFormPool() {
        return _SpareFormPool;
    }
    /**
     * 获得空闲的实际连接
     * @return int 空闲的实际连接
     */
    public int get_SpareRealFormPool() {
        return _SpareRealFormPool;
    }
    /**
     * 得到服务器运行时间
     * @return Date 返回服务器运行时间
     */
    public Date get_StartTime() {
        return _StartTime;
    }
    /**
     * 获得已经分配的连接数
     * @return int 已经分配的连接数
     */
    public int get_UseFormPool() {
        return _UseFormPool;
    }
    /**
     * 获得已分配的实际连接
     * @return int 已分配的实际连接
     */
    public int get_UseRealFormPool() {
        return _UseRealFormPool;
    }
    /**
     * 得到当前连接池的状态
     * @return int 连接池的状态
     */
    public int get_PoolState() {
        return _PoolState;
    }
    /**
     * 得到连接池中存在的实际连接数(有效的实际连接)
     * @return int 实际连接数
     */
    public int get_PotentRealFormPool() {
        return _PotentRealFormPool;
    }
    /**
     * 得到最小实际连接数
     * @return int 最小实际连接数
     */
    public int get_MinConnection() {
        return _MinConnection;
    }
    /**
     * 得到每个实际连接可以被重复使用次数（引用记数）
     * @return int 每个实际连接可以被重复使用次数（引用记数）
     */
    public int get_MaxRepeatDegree() {
        return _MaxRepeatDegree;
    }
    /**
     * 得到最大实际连接数目
     * @return int 最大可以创建的实际连接数目
     */
    public int get_MaxConnection() {
        return _MaxConnection;
    }
    /**
     * 得到保留的实际空闲连接
     * @return int 保留的实际空闲连接
     */
    public int get_KeepRealConnection() {
        return _KeepRealConnection;
    }
    /**
     * 得到数据库登陆密码
     * @return String 数据库登陆密码
     */
    public String get_password() {
        return _password;
    }
    /**
     * 得到数据库登陆帐号
     * @return String 数据库登陆帐号
     */
    public String get_userID() {
        return _userID;
    }
    /**
     * 得到连接池最多可以提供多少个连接，该值是最大实际连接数与每个连接可以被重复使用次数的乘积
     * @return int 连接池最多可以提供多少个连接
     */
    public int get_MaxConnectionFormPool() {
        return _MaxConnection * _MaxRepeatDegree;
    }
    //-------------------------------------------------------------------------------------------
    public Connection GetConnectionFormPool(Object key) throws StateException, PoolFullException, KeyException, OccasionException, ConnLevelException {
        return GetConnectionFormPool(key, ConnLevel_None);
    }
    /**
     * 在连接池中申请一个连接，线程安全
     * @param key Object 发起者
     * @return Connection 申请到的连接
     * @throws StateException 服务状态错误
     * @throws PoolFullException 连接池已经饱和，不能提供连接
     * @throws KeyExecption 一个key对象只能申请一个连接
     * @throws OccasionExecption 连接资源耗尽，或错误的访问时机。
     * @throws ConnLevelExecption 无效的错误的级别参数
     */
    public Connection GetConnectionFormPool(Object key, int connLevel) throws StateException, PoolFullException, KeyException, OccasionException, ConnLevelException {
        synchronized (this) {
            if (_PoolState != PoolState_Run)
                throw new StateException(); //服务状态错误
            if (hs_UseConn.size() == get_MaxConnectionFormPool())
                throw new PoolFullException(); //连接池已经饱和，不能提供连接
            if (hs_UseConn.containsKey(key))
                throw new KeyException(); //一个key对象只能申请一个连接
            if (connLevel == ConnLevel_ReadOnly)
                return GetConnectionFormPool_ReadOnly(key); //ReadOnly级别
            else if (connLevel == ConnLevel_High)
                return GetConnectionFormPool_High(key); //High级别
            else if (connLevel == ConnLevel_None)
                return GetConnectionFormPool_None(key); //None级别
            else if (connLevel == ConnLevel_Bottom)
                return GetConnectionFormPool_Bottom(key); //Bottom级别
            else
                throw new ConnLevelException(); //无效的错误的级别参数
        }
    }
    /**
     * 申请一个连接资源ReadOnly级别，只读方式，线程安全
     * @param key Object 申请者
     * @return Connection 申请到的连接对象
     * @throws OccasionExecption 连接资源耗尽，或错误的访问时机。
     */
    private Connection GetConnectionFormPool_ReadOnly(Object key) throws OccasionException {
        ConnStruct cs = null;
        for (int i = 0; i < al_All.size(); i++) {
            cs = al_All.get(i);
            if (cs.GetEnable() == false || cs.GetAllot() == false || cs.GetUseDegree() == _MaxRepeatDegree || cs.GetIsUse() == true)
                continue;
            return GetConnectionFormPool_Return(key, cs, ConnLevel_ReadOnly); //返回得到的连接
        }
        return GetConnectionFormPool_Return(key, null, ConnLevel_ReadOnly);
    }
    /**
     * 申请一个连接资源High级别，只读方式，线程安全
     * @param key Object 申请者
     * @return Connection 申请到的连接对象
     * @throws OccasionExecption 连接资源耗尽，或错误的访问时机。
     */
    private Connection GetConnectionFormPool_High(Object key) throws OccasionException {
        ConnStruct cs = null;
        ConnStruct csTemp = null;
        for (int i = 0; i < al_All.size(); i++) {
            csTemp = al_All.get(i);
            if (csTemp.GetEnable() == false || csTemp.GetAllot() == false || csTemp.GetUseDegree() == _MaxRepeatDegree) { //不可以分配跳出本次循环。
                csTemp = null;
                continue;
            }
            if (csTemp.GetUseDegree() == 0) { //得到最合适的
                cs = csTemp;
                break;
            } else { //不是最合适的放置到最佳选择中
                if (cs != null) {
                    if (csTemp.GetUseDegree() < cs.GetUseDegree())
                        //与上一个最佳选择选出一个最佳的放置到cs中
                        cs = csTemp;
                } else
                    cs = csTemp;
            }
        }
        return GetConnectionFormPool_Return(key, cs, ConnLevel_High); //返回最合适的连接
    }
    /**
     * 申请一个连接资源，优先级-None，线程安全
     * @param key Object 申请者
     * @return Connection 申请到的连接对象
     * @throws OccasionExecption 连接资源耗尽，或错误的访问时机。
     */
    private Connection GetConnectionFormPool_None(Object key) throws OccasionException {
        ArrayList<ConnStruct> al = new ArrayList<ConnStruct>();
        ConnStruct cs = null;
        for (int i = 0; i < al_All.size(); i++) {
            cs = al_All.get(i);
            if (cs.GetEnable() == false || cs.GetAllot() == false || cs.GetUseDegree() == _MaxRepeatDegree) //不可以分配跳出本次循环。
                continue;
            if (cs.GetAllot() == true)
                al.add(cs);
        }
        if (al.size() == 0)
            return GetConnectionFormPool_Return(key, null, ConnLevel_None); //发出异常
        else
            return GetConnectionFormPool_Return(key, ((ConnStruct) al.get(al.size() / 2)), ConnLevel_None); //返回连接
    }
    /**
     * 申请一个连接资源，优先级-低，线程安全
     * @param key Object 申请者
     * @return Connection 申请到的连接对象
     * @throws OccasionExecption
     */
    private Connection GetConnectionFormPool_Bottom(Object key) throws OccasionException {
        ConnStruct cs = null;
        ConnStruct csTemp = null;
        for (int i = 0; i < al_All.size(); i++) {
            csTemp = al_All.get(i);
            if (csTemp.GetEnable() == false || csTemp.GetAllot() == false || csTemp.GetUseDegree() == _MaxRepeatDegree)//不可以分配跳出本次循环。
            {
                csTemp = null;
                continue;
            } else//不是最合适的放置到最佳选择中
            {
                if (cs != null) {
                    if (csTemp.GetUseDegree() > cs.GetUseDegree())
                        //与上一个最佳选择选出一个最佳的放置到cs中
                        cs = csTemp;
                } else
                    cs = csTemp;
            }
        }
        return GetConnectionFormPool_Return(key, cs, ConnLevel_Bottom);//返回最合适的连接
    }
    /**
     * 返回Connection对象，同时做获得连接时的必要操作
     * @param key Object 申请者
     * @param cs ConnStruct ConnStruct对象
     * @param connLevel int 级别
     * @return Connection 是否为只读属性
     * @throws OccasionExecption
     */
    private Connection GetConnectionFormPool_Return(Object key, ConnStruct cs, int connLevel) throws OccasionException {
        try {
            if (cs == null)
                throw new Exception();
            cs.Repeat();
            hs_UseConn.put(key, cs);
            if (connLevel == ConnLevel_ReadOnly) {
                cs.SetAllot(false);
                cs.SetIsRepeat(false);
            }
        } catch (Exception e) {
            throw new OccasionException(); //连接资源耗尽，或错误的访问时机。
        } finally {
            UpdateAttribute(); //更新属性
        }
        return cs.GetConnection();
    }
    /**
     * 释放申请的数据库连接对象，线程安全
     * @param key Object 表示数据库连接申请者
     * @throws NotKeyExecption 无法释放，不存在的key
     * @throws PoolNotRunException 服务未启动
     */
    public void DisposeConnection(Object key) throws NotKeyException, PoolNotRunException {
        synchronized (hs_UseConn) {
            ConnStruct cs = null;
            if (_PoolState == PoolState_Run) {
                if (!hs_UseConn.containsKey(key))
                    throw new NotKeyException(); //无法释放，不存在的key
                cs = hs_UseConn.get(key);
                cs.SetIsRepeat(true);
                if (cs.GetAllot() == false)
                    if (cs.GetEnable() == true)
                        cs.SetAllot(true);
                try {
                    cs.Remove();
                } catch (Exception e) { //如果在设置连接期间出现问题就立刻将该连接设置失效
                    cs.Close();
                    cs.SetConnectionLost();
                }
                hs_UseConn.remove(key);
            } else
                throw new PoolNotRunException(); //服务未启动
        }
        UpdateAttribute(); //更新属性
    }
    //--------------------------------------------------------------------
    /**
     * 返回包装后的新的连接对象
     * @param connString String 连接字符串
     * @param driveString String 驱动字符串
     * @return ConnStruct 装后的新的连接对象
     * @throws RuntimeException  
     */
    private ConnStruct CreateConnectionTemp(String connString, String driveString, String userID, String password) throws RuntimeException {
        try {
            Connection con = CreateConnection(connString, driveString, userID, password);
            return new ConnStruct(con, Calendar.getInstance().getTime());
        } catch (CreateConnectionException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * <p>用指定类型创建连接</p>
     *
     * 内部实现方法：<br>
     *    protected Connection CreateConnection(String connString, String driveString,String userID,String password) throws CreateConnectionException {<br>
     *        try {<br>
     *            Class.forName(driveString);<br>
     *            Connection con = java.sql.DriverManager.getConnection(connString, userID, password);<br>
     *            return con;<br>
     *        } catch (Exception e) {<br>
     *            throw new CreateConnectionException();<br>
     *        }<br>
     *    }<br>
     *
     * <p>使用示例：</p>
     *   ConnectionPool c = new ConnectionPool() {<br>
     *       protected Connection CreateConnection(String connString, String driveString, String userID, String password) throws CreateConnectionException {<br>
     *           try {<br>
     *               Class.forName(driveString);<br>
     *               Connection con = java.sql.DriverManager.getConnection(connString, userID, password);<br>
     *               System.out.println("new db connection");<br>
     *               return con;<br>
     *   } catch (Exception e) {throw new CreateConnectionException();}<br>
     *       }<br>
     *   };<br>
     * 在示例中使用匿名类来确定如何连接到数据库以及在每次创建数据库连接时要执行的方法，在实际使用当中您可以继承该类以重写该方法达到该目的
     *
     * @param connString String 连接字符串，该值可以由set_String方法设置
     * @param driveString String 驱动字符串，该值可以由set_String方法设置
     * @param userID String 登陆数据库使用的帐号，该值可以由set_DataBaseUser方法设置
     * @param password String 登陆数据库使用的帐号密码，该值可以由set_DataBaseUser方法设置
     * @return Connection 返回创建的连接
     * @throws CreateConnectionExecption 如果在创建中出的现异
     */
    protected Connection CreateConnection(String connString, String driveString, String userID, String password) throws CreateConnectionException {
        try {
            Class.forName(driveString);
            Connection con = java.sql.DriverManager.getConnection(connString, userID, password);
            return con;
        } catch (Exception e) {
            throw new CreateConnectionException();
        }
    }
    //-------------------------------------------------------------------------------------------
    /**
     * 启动连接池服务，同步调用
     * @throws PoolNotStopException 
     */
    public void StartServices() throws PoolNotStopException {
        StartServices(false);
    }
    /** 
     * 启动连接池服务
     * @param ansy 指定调用方式是否为异步调用，True为使用异步调用，异步调用指，用户调用该方法后，无须等待创建结束就可继续做其他操作
     * @throws PoolNotStopException 服务已经运行或者未完全结束
     */
    public void StartServices(boolean ansy) throws PoolNotStopException {
        synchronized (this) {
            threadCreate.createThreadMode = 0; //工作模式0
            threadCreate.createThreadProcessRun = true;
            threadCreate.createThreadProcessTemp = _MinConnection;
            if (_PoolState == PoolState_UnInitialize)
                threadCreate.start();
            else if (_PoolState == PoolState_Stop)
                threadCreate.interrupt();
            else
                throw new PoolNotStopException(); //服务已经运行或者未完全结束
            threadCheck.StartTimer();
            threadCheck.interrupt();//开始运行
        }
        if (!ansy)
            while (threadCreate.getState() != Thread.State.WAITING) {
                //等待可能存在的创建线程结束
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }
    /**
     * 停止服务同步的方法，线程安全
     * @throws ResCallBackException 资源未全部回首
     * @throws PoolNotRunException 服务没有运行
     */
    public void StopServices() throws ResCallBackException, PoolNotRunException {
        StopServices(false);
    }
    /**
     * 停止服务，线程安全
     * @param needs boolean 是否必须退出；如果指定为false与StartServices()功能相同，如果指定为true。将未收回的连接资源关闭，这将是危险的。认为可能你的程序正在使用此资源。
     * @throws ResCallBackException 服务未启动
     * @throws PoolNotRunException 服务没有运行
     */
    public void StopServices(boolean needs) throws PoolNotRunException, ResCallBackException {
        synchronized (this) {
            if (_PoolState == PoolState_Run) {
                synchronized (hs_UseConn) {
                    if (needs == true) //必须退出
                        hs_UseConn.clear();
                    else if (hs_UseConn.size() != 0)
                        throw new ResCallBackException(); //连接池资源未全部回收
                }
                threadCheck.StopTimer();
                while (threadCreate.getState() != Thread.State.WAITING) {
                    //等待threadCreate事件结束
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                threadCreate.createThreadProcessRun = false;
                while (threadCreate.getState() != Thread.State.WAITING) {
                    //等待可能存在的创建线程结束
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                synchronized (al_All) {
                    for (int i = 0; i < al_All.size(); i++)
                        al_All.get(i).Dispose();
                    al_All.clear();
                }
                _PoolState = PoolState_Stop;
            } else
                throw new PoolNotRunException(); //服务未启动
        }
        UpdateAttribute(); //更新属性
    }
    /**
     * 如果想销毁线程池请务必调用该方法，该将终止线程池内部处于等待的线程。
     */
    @SuppressWarnings("deprecation")
    public synchronized void Dispose() {
        try {
            this.StopServices();
            threadCreate.stop();
            threadCheck.stop();
            al_All = null;
            hs_UseConn = null;
        } catch (Exception e) {}
    }
    //-------------------------------------------------------------------------------------------
    /**
     * 测试ConnStruct是否过期
     * @param cs ConnStruct 被测试的ConnStruct
     */
    private void TestConnStruct(ConnStruct cs) {
        //此次被分配出去的连接是否在此次之后失效
        if (cs.GetUseDegree() == _MaxRepeatDegree)
            cs.SetConnectionLost(); //超过使用次数
        Calendar c = Calendar.getInstance();
        c.setTime(cs.GetCreateTime());
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + _Exist);
        if (c.getTime().after(new Date()))
            cs.SetConnectionLost(); //连接超时
    }
    /**
     * 更新属性
     */
    private synchronized void UpdateAttribute() {
        int temp_readOnlyFormPool = 0;//连接池已经分配多少只读连接
        int temp_potentRealFormPool = 0;//连接池中存在的实际连接数(有效的实际连接)
        int temp_spareRealFormPool = 0;//空闲的实际连接
        int temp_useRealFormPool = 0;//已分配的实际连接
        int temp_spareFormPool = get_MaxConnectionFormPool();//目前可以提供的连接数
        //---------------------------------
        synchronized (hs_UseConn) {
            _UseFormPool = hs_UseConn.size();
        }
        //---------------------------------
        ConnStruct cs = null;
        synchronized (al_All) {
            _RealFormPool = al_All.size();
            for (int i = 0; i < al_All.size(); i++) {
                cs = al_All.get(i);
                //只读
                if (cs.GetAllot() == false && cs.GetIsUse() == true && cs.GetIsRepeat() == false)
                    temp_readOnlyFormPool++;
                //有效的实际连接
                if (cs.GetEnable() == true)
                    temp_potentRealFormPool++;
                //空闲的实际连接
                if (cs.GetEnable() == true && cs.GetIsUse() == false)
                    temp_spareRealFormPool++;
                //已分配的实际连接
                if (cs.GetIsUse() == true)
                    temp_useRealFormPool++;
                //目前可以提供的连接数
                if (cs.GetAllot() == true)
                    temp_spareFormPool = temp_spareFormPool - cs.GetRepeatNow();
                else
                    temp_spareFormPool = temp_spareFormPool - _MaxRepeatDegree;
            }
        }
        _ReadOnlyFormPool = temp_readOnlyFormPool;
        _PotentRealFormPool = temp_potentRealFormPool;
        _SpareRealFormPool = temp_spareRealFormPool;
        _UseRealFormPool = temp_useRealFormPool;
        _SpareFormPool = temp_spareFormPool;
    }
    /**
     * 获得该连接池的版本
     * @return 返回版本信息
     */
    public String getVerstion() {
        return "1.2";
    }
}
