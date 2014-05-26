package test;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
/**
 * 
 * @version : 2013-2-16
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class TTTTT {
    Display display = new Display();
    Shell   shell   = new Shell(display);
    public TTTTT() {
        shell.setLayout(new GridLayout());
        ToolBar toolBar = new ToolBar(shell, SWT.FLAT);
        ToolItem itemCopy = new ToolItem(toolBar, SWT.PUSH);
        ToolItem itemPaste = new ToolItem(toolBar, SWT.PUSH);
        itemCopy.setText("Copy");
        itemPaste.setText("Paste");
        itemCopy.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                Clipboard clipboard = new Clipboard(display);
                String plainText = "Hello World";
                String rtfText = "{\\rtf1\\b Hello World}";
                TextTransfer textTransfer = TextTransfer.getInstance();
                RTFTransfer rftTransfer = RTFTransfer.getInstance();
                clipboard.setContents(new String[] { plainText, rtfText }, new Transfer[] { textTransfer, rftTransfer });
                clipboard.dispose();
            }
        });
        itemPaste.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                Clipboard clipboard = new Clipboard(display);
                TransferData[] transferDatas = clipboard.getAvailableTypes();
                for (int i = 0; i < transferDatas.length; i++) {
                    if (RTFTransfer.getInstance().isSupportedType(transferDatas[i])) {
                        System.out.println("Data is available in RTF format");
                        break;
                    }
                }
                String plainText = (String) clipboard.getContents(TextTransfer.getInstance());
                String rtfText = (String) clipboard.getContents(RTFTransfer.getInstance());
                System.out.println("PLAIN: " + plainText + "\n" + "RTF: " + rtfText);
                clipboard.dispose();
            }
        });
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
    public static void main(String[] args) {
        new TTTTT();
    }
}