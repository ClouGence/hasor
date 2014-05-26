package test;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.dev.toos.constcode.data.xml.XmlConstDao;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
/**
 * 
 * @version : 2013-2-17
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class TestXmlConstDao {
    public static void main(String[] args) throws IOException, JAXBException {
        String filePaht = "E:\\workspace\\noe-dev-toos\\core-codes.xml";
        XmlConstDao xmlConstDao = new XmlConstDao(new FileInputStream(filePaht));
        //
        //        System.out.println(xmlConstDao.getALLConst());
    }
}