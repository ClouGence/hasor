package net.hasor.test.web.actions.valid;
import net.hasor.web.valid.ValidInvoker;

import java.util.HashMap;

public class ValidTestUtils {
    public static HashMap<String, Object> newHashMap(ValidInvoker validInvoker, ValidRequestFieldBean fieldBean) {
        return new HashMap<String, Object>() {{
            put("byteParam", fieldBean.getByteParam());
            put("intParam", fieldBean.getIntParam());
            put("strParam", fieldBean.getStrParam());
            put("eptParam", fieldBean.getEptParam());
            //
            put("doValid", validInvoker.isValid());
            put("validErrorsOfString", validInvoker.validErrorsOfString());
            put("validErrorsOfMessage", validInvoker.validErrorsOfMessage());
        }};
    }
}
