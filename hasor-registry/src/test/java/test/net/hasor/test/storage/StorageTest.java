package test.net.hasor.test.storage;
import com.alibaba.fastjson.JSON;
import net.hasor.registry.access.adapter.ObjectData;
import net.hasor.registry.access.adapter.StorageDao;
import net.hasor.registry.storage.mem.MemStorageDao;
import org.junit.Test;
public class StorageTest {
    private ObjectData newData(String dataBody) {
        ObjectData data = new ObjectData(dataBody);
        data.setServiceID("serviceID");
        data.setInstanceID("instanceID");
        data.setTimestamp(System.currentTimeMillis());
        return data;
    }
    @Test
    public void storageDaoTest() {
        StorageDao storageDao = new MemStorageDao();
        //
        storageDao.saveData("/", newData("sss"));
        storageDao.saveData("/1", newData("sss"));
        storageDao.saveData("/1/a", newData("sss"));
        storageDao.saveData("/2", newData("sss"));
        storageDao.saveData("/3", newData("sss"));
        //
        storageDao.deleteData("/1");
        //
        System.out.println(JSON.toJSON(storageDao.querySubList("/", 0, 100)));
    }
}
