package net.hasor.rsf.address;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.utils.ZipUtils;
import org.more.util.MatchUtils;
import org.more.util.StringUtils;
import org.more.util.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 服务地址的辅助工具,负责读写本地地址本缓存。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClearPoolThread extends Thread {
    protected final      Logger logger      = LoggerFactory.getLogger(getClass());
    private static final String CharsetName = ZipUtils.CharsetName;
    private final File           rsfHome;
    private final File           indexFile;
    private final File           snapshotHome;
    private final RsfEnvironment rsfEnvironment;
    private final AddressPool    addressPool;
    private boolean exitThread = false;
    //
    ClearPoolThread(AddressPool addressPool, RsfEnvironment rsfEnvironment) {
        this.addressPool = addressPool;
        this.rsfEnvironment = rsfEnvironment;
        this.rsfHome = new File(rsfEnvironment.evalString("%" + RsfEnvironment.WORK_HOME + "%/rsf/"));
        this.snapshotHome = new File(rsfHome, RsfConstants.SnapshotPath);
        this.indexFile = new File(snapshotHome, RsfConstants.SnapshotIndex);
    }
    //
    public void stopTimer() {
        this.exitThread = true;
    }
    public void run() {
        this.exitThread = false;
        RsfSettings rsfSettings = rsfEnvironment.getSettings();
        long refreshCacheTime = rsfSettings.getRefreshCacheTime();
        long nextCheckSavePoint = 0;
        logger.info("AddressPool - Timer -> start, refreshCacheTime = {}.", refreshCacheTime);
        while (!this.exitThread) {
            //1.启动时做一次清理
            clearCacheData();
            try {
                Thread.sleep(refreshCacheTime);
            } catch (InterruptedException e) {
                    /**/
            }
            //2.将数据保存到缓存文件
            logger.info("AddressPool - refreshCache. at = {} , refreshCacheTime = {}.", nowTime(), refreshCacheTime);
            this.addressPool.refreshAddressCache();
            if (rsfSettings.islocalDiskCache() && nextCheckSavePoint < System.currentTimeMillis()) {
                nextCheckSavePoint = System.currentTimeMillis() + (1 * 60 * 60 * 1000);/*每小时保存一次地址本快照。*/
                try {
                    storeConfig();
                } catch (IOException e) {
                    logger.error("saveAddress error {} -> {}", e.getMessage(), e);
                }
            }
        }
        logger.info("AddressPool - Timer -> stop.");
    }
    /**清理缓存的地址数据*/
    protected void clearCacheData() {
        String[] fileNames = this.snapshotHome.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return MatchUtils.wildToRegex("address-[0-9]{8}-[0-9]{6}.zip", name, MatchUtils.MatchTypeEnum.Regex);
            }
        });
        List<String> sortList = (fileNames == null) ? new ArrayList<String>(0) : Arrays.asList(fileNames);
        Collections.sort(sortList);
        //
        long nowTime = System.currentTimeMillis() - (7 * 24 * 3600000);//数据写死自动清理 7 天之前的数据
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
        for (String itemName : sortList) {
            try {
                String dateTimeStr = itemName.substring("address-".length(), itemName.length() - ".zip".length());
                Date dateTime = format.parse(dateTimeStr);
                if (dateTime.getTime() < nowTime) {
                    new File(this.snapshotHome, itemName).delete();
                }
            } catch (Exception e) { /**/ }
        }
    }
    /**保存地址列表到zip流中(每小时保存一次)，当遇到保存的文件已存在时，会出现1秒的CPU漂高。*/
    protected synchronized void storeConfig() throws IOException {
        File writeFile = null;
        while (writeFile == null || writeFile.exists()) {/*会有1秒的CPU漂高*/
            writeFile = new File(this.snapshotHome, "address-" + nowTime() + ".zip");
        }
        logger.info("rsf - saveAddress to snapshot file({}) ->{}", writeFile);
        FileOutputStream fos = null;
        FileWriter fw = null;
        try {
            writeFile.getParentFile().mkdirs();
            fos = new FileOutputStream(writeFile, false);
            fos.getFD().sync();//独占文件
            //
            this.addressPool.storeConfig(fos);
            //
            fos.flush();
            fos.close();
            //
            fw = new FileWriter(this.indexFile, false);
            logger.info("rsf - update snapshot index -> " + this.indexFile.getAbsolutePath());
            fw.write(writeFile.getName());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            logger.error("rsf - saveAddress " + e.getClass().getSimpleName() + " :" + e.getMessage(), e);
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
    protected synchronized void restoreConfig() throws IOException {
        //1.校验
        if (!this.indexFile.exists()) {
            logger.info("address snapshot index file, undefined.");
            return;
        }
        if (!this.indexFile.canRead()) {
            logger.error("address snapshot index file, can not read.");
            return;
        }
        //2.确定要读取的文件。
        File readFile = null;
        try {
            String index = FileUtils.readFileToString(this.indexFile, CharsetName);
            readFile = new File(this.snapshotHome, index);
            if (StringUtils.equals(index, "") || !readFile.exists()) {
                logger.error("address snapshot file is not exist.", readFile);
                return;
            }
        } catch (Throwable e) {
            logger.error("read the snapshot file name error :" + e.getMessage(), e);
            return;
        }
        //
        //3.恢复数据数据
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(readFile);
            this.addressPool.restoreConfig(inStream);
            inStream.close();
        } catch (IOException e) {
            logger.error("read the snapshot file name error :" + e.getMessage(), e);
            if (inStream != null) {
                inStream.close();
            }
        }
    }
    //
    private static String nowTime() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    }
}