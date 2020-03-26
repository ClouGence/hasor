package net.hasor.test.dataql.beans;
import java.util.ArrayList;
import java.util.List;

public class UserBean {
    private long              userID     = 1234567890;
    private int               age        = 31;
    private String            name       = "this is name.";
    private String            name2      = "this is name2.";
    private String            nick       = "my name is nick.";
    private SexEnum           sex        = SexEnum.F;
    private boolean           status     = true;
    private List<UserAddress> addressSet = new ArrayList<UserAddress>() {{
        add(new UserAddress(1));
        add(new UserAddress(2));
        add(new UserAddress(3));
        add(new UserAddress(4));
    }};

    public UserBean(int i) {
        this.userID = i;
        this.name = "this is name. -> " + i;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public SexEnum getSex() {
        return sex;
    }

    public void setSex(SexEnum sex) {
        this.sex = sex;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<UserAddress> getAddressSet() {
        return addressSet;
    }

    public void setAddressSet(List<UserAddress> addressSet) {
        this.addressSet = addressSet;
    }
}
