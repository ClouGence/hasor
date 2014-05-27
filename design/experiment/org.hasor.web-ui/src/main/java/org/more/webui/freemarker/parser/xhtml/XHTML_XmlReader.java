//package org.more.webui.freemarker.parser.xhtml;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import javax.xml.stream.XMLStreamException;
//import org.more.core.xml.stream.XmlAccept;
//import org.more.core.xml.stream.XmlReader;
//import org.more.core.xml.stream.XmlStreamEvent;
//import org.more.util.ResourcesUtil;
///**
// * 用于解析xhtml的UI标签库。
// * @version : 2012-5-13
// * @author 赵永春 (zyc@byshell.org)
// */
//public class XHTML_XmlReader /* extends XmlReader */{
//    //    public XHTML_XmlReader(InputStream inStrema, OutputStream outStrema) {
//    //        super(inStrema);
//    //    }
//    //    @Override
//    //    protected void pushEvent(XmlAccept accept, XmlStreamEvent e) throws XMLStreamException, IOException {
//    //        // TODO Auto-generated method stub
//    //        super.pushEvent(accept, e);
//    //    }
//    public static void main(String[] args) throws IOException, XMLStreamException {
//        InputStream in = ResourcesUtil.getResourceAsStream("/org/more/webui/freemarker/xhtml/parser/template_1.xml");
//        new XmlReader(in).reader(new XmlAccept() {
//            @Override
//            public void sendEvent(XmlStreamEvent e) throws XMLStreamException, IOException {
//                // TODO Auto-generated method stub
//                System.out.println(e);
//            }
//            @Override
//            public void endAccept() throws XMLStreamException {
//                // TODO Auto-generated method stub
//            }
//            @Override
//            public void beginAccept() throws XMLStreamException {
//                // TODO Auto-generated method stub
//            }
//        }, null);
//    }
//}