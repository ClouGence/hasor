package org.dev.toos.internal.util;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
/**
 * 
 * @version : 2013-2-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class ColorUtils {
    public static Color getColor4New() {
        return new Color(Display.getDefault(), 153, 255, 153);
    }
    public static Color getColor4Delete() {
        return new Color(Display.getDefault(), 255, 102, 102);
    }
    //
    public static Color getColor4DB() {
        return new Color(Display.getDefault(), 153, 204, 255);
    }
    public static Color getColor4Jar() {
        return new Color(Display.getDefault(), 255, 204, 153);
    }
    public static Color getColor4Source() {
        return new Color(Display.getDefault(), 255, 255, 255);
    }
    //
    public static Color getColor4Changed() {
        return new Color(Display.getDefault(), 255, 204, 204);
    }
    public static Color getColor4Changed2() {
        return new Color(Display.getDefault(), 255, 153, 153);
    }
}