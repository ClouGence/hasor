package net.hasor.db.dal.fxquery;
import org.junit.Test;

public class TokenTest {
    @Test
    public void test() {
        //
        GenericTokenParser tokenParser = new GenericTokenParser(new String[] { "#{", "${" }, "}", (builder, token, content) -> {
            System.out.println("ST-> " + builder.toString());
            if (token.equalsIgnoreCase("${")) {
                System.out.println("$L-> " + content);
            }
            if (token.equalsIgnoreCase("#{")) {
                System.out.println("#L-> " + content);
            }
            builder.delete(0, builder.length());
            return "";
        });
        //
        String abc = tokenParser.parse("insert into user_info (name,age,status,create_time) values (#{userInfo.name},${userInfo.age},#{userInfo.status},now())");
        System.out.println("ST-> " + abc);
    }
}
