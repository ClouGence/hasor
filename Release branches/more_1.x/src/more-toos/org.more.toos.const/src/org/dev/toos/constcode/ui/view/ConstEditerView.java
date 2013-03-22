package org.dev.toos.constcode.ui.view;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
public class ConstEditerView extends Dialog {
    protected Object result;
    protected Shell  shell;
    /**
     * Create the dialog.
     * @param parent
     * @param style
     */
    public ConstEditerView(Shell parent, int style) {
        super(parent, style);
        setText("SWT Dialog");
    }
    /**
     * Open the dialog.
     * @return the result
     */
    public Object open() {
        createContents();
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return result;
    }
    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shell = new Shell(getParent(), getStyle());
        shell.setSize(403, 257);
        shell.setText(getText());
    }
}
