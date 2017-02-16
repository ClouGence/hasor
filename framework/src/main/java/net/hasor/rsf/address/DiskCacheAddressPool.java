package net.hasor.rsf.address;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.utils.IOUtils;
import net.hasor.rsf.utils.MatchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 服务地址的辅助工具,负责读写本地地址本缓存。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class DiskCacheAddressPool extends AddressPool {
    protected final Logger        logger = LoggerFactory.getLogger(getClass());
    private final   AtomicBoolean inited = new AtomicBoolean(false);
    private final Thread timer;
    private final File   snapshotHome;
    private final File   indexFile;
    private boolean exitThread = false;
    //
    public DiskCacheAddressPool(final RsfEnvironment rsfEnvironment) {
        super(rsfEnvironment);
        File rsfHome = new File(rsfEnvironment.evalString("%" + RsfEnvironment.WORK_HOME + "%/rsf/"));
        this.snapshotHome = new File(rsfHome, RsfConstants.SnapshotPath);
        this.indexFile = new File(snapshotHome, RsfConstants.SnapshotIndex);
        //
        this.timer = new Thread(new Runnable() {
            public void run() {
                if (rsfEnvironment.getSettings().islocalDiskCache()) {
                    doWork();
                }
            }
        });
        this.timer.setContextClassLoader(rsfEnvironment.getClassLoader());
        this.timer.setName("RSF-DiskCacheAddressPool-Timer");
        this.timer.setDaemon(true);
    }
    //
    //
    /** 启动定时器,定时进行地址本的磁盘缓存。*/
    public void startTimer() {
        if (this.inited.compareAndSet(false, true)) {
            this.logger.info("startTimer address snapshot Thread[{}].", timer.getName());
            this.exitThread = false;
            this.timer.start();
        }
    }
    /** 停止定时器,停止定时进行地址本的磁盘缓存。*/
    public void shutdownTimer() {
        if (this.inited.compareAndSet(true, false)) {
            this.logger.info("shutdownTimer address snapshot Thread[{}].", timer.getName());
            this.exitThread = true;
        }
    }
    //
    private void doWork() {
        this.exitThread = false;
        RsfSettings rsfSettings = this.getRsfEnvironment().getSettings();
        long refreshCacheTime = rsfSettings.getRefreshCacheTime();
        long diskCacheTimeInterval = rsfSettings.getDiskCacheTimeInterval();
        long nextCheckSavePoint = 0;
        //
        if (diskCacheTimeInterval <= 0) {
            diskCacheTimeInterval = RsfConstants.OneHourTime;
        }
        //
        this.logger.info("AddressPool - Timer -> start, refreshCacheTime = {}.", refreshCacheTime);
        while (!this.exitThread) {
            //1.启动时做一次清理
            clearCacheData();
            try {
                Thread.sleep(refreshCacheTime);
            } catch (InterruptedException e) {
                /**/
            }
            //2.将数据保存到缓存文件
            this.logger.info("AddressPool - refreshCache. at = {} , refreshCacheTime = {}.", nowTime(), refreshCacheTime);
            this.refreshAddressCache();
            if (rsfSettings.islocalDiskCache() && nextCheckSavePoint < System.currentTimeMillis()) {
                nextCheckSavePoint = System.currentTimeMillis() + diskCacheTimeInterval;/*每小时保存一次地址本快照。*/
                try {
                    storeConfig();
                } catch (IOException e) {
                    this.logger.error("saveAddress error {} -> {}", e.getMessage(), e);
                }
            }
        }
        this.logger.info("AddressPool - Timer -> stop.");
    }
    //
    /**清理缓存的地址数据*/
    public void clearCacheData() {
        String[] fileNames = this.snapshotHome.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return MatchUtils.wildToRegex("address-[0-9]{8}-[0-9]{6}.zip", name, MatchUtils.MatchTypeEnum.Regex);
            }
        });
        List<String> sortList = (fileNames == null) ? new ArrayList<String>(0) : Arrays.asList(fileNames);
        Collections.sort(sortList);
        //
        long nowTime = System.currentTimeMillis() - RsfConstants.SevenDaysTime;//数据自动清理 7 天之前的数据
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
        for (String itemName : sortList) {
            try {
                String dateTimeStr = itemName.substring(RsfConstants.AddrPoolStoreName.length(), itemName.length() - ".zip".length());
                Date dateTime = format.parse(dateTimeStr);
                if (dateTime.getTime() < nowTime) {
                    new File(this.snapshotHome, itemName).delete();
                }
            } catch (Exception e) { /**/ }
        }
    }
    //
    /**保存地址列表到zip流中(每小时保存一次)，当遇到保存的文件已存在时会重新生成新的文件名。*/
    public synchronized void storeConfig() throws IOException {
        File writeFile = null;
        while (writeFile == null || writeFile.exists()) {
            writeFile = new File(this.snapshotHome, RsfConstants.AddrPoolStoreName + nowTime() + ".zip");
        }
        this.logger.info("rsf - saveAddress to snapshot file({}) ->{}", writeFile);
        FileOutputStream fos = null;
        FileWriter fw = null;
        try {
            boolean mkdirResult = writeFile.getParentFile().mkdirs();
            if (mkdirResult || writeFile.getParentFile().exists()) {
                fos = new FileOutputStream(writeFile, false);
                fos.getFD().sync();//独占文件
                //
                this.storeConfig(fos);
                //
                fos.flush();
                fos.close();
                //
                fw = new FileWriter(this.indexFile, false);
                this.logger.info("rsf - update snapshot index -> " + this.indexFile.getAbsolutePath());
                fw.write(writeFile.getName());
                fw.flush();
                fw.close();
            }
        } catch (IOException e) {
            this.logger.error("rsf - saveAddress " + e.getClass().getSimpleName() + " :" + e.getMessage(), e);
            throw e;
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (fw != null) {
                fw.close();
            }
        }
    }
    /**从保存的地址本中恢复数据。*/
    public synchronized void restoreConfig() {
        //1.校验
        if (!this.indexFile.exists()) {
            this.logger.info("address snapshot index file, undefined.");
            return;
        }
        if (!this.indexFile.canRead()) {
            this.logger.error("address snapshot index file, can not read.");
            return;
        }
        //2.确定要读取的文件。
        File readFile = null;
        try {
            FileReader reader = new FileReader(this.indexFile);
            List<String> bodyList = IOUtils.readLines(reader);
            String index = bodyList.isEmpty() ? "" : bodyList.get(0);
            readFile = new File(this.snapshotHome, index);
            if ("".equals(index) || !readFile.exists()) {
                this.logger.error("address snapshot file is not exist.", readFile);
                return;
            }
        } catch (Throwable e) {
            this.logger.error("read the snapshot file name error :" + e.getMessage(), e);
            return;
        }
        //
        //3.恢复数据数据
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(readFile);
            this.restoreConfig(inStream);
            inStream.close();
        } catch (IOException e) {
            this.logger.error("read the snapshot file name error :" + e.getMessage(), e);
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e1) {
                    this.logger.error(e1.getMessage(), e1);
                }
            }
        }
    }
    //
    private static String nowTime() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    }
}