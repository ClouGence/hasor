package net.hasor.test.dataql.beans;
public class UserAddress {
    private String zip;
    private String code;
    private String address;

    public UserAddress(int i) {
        this.zip = "1234" + i;
        this.code = "c_" + i;
        this.address = "this is detail address info.";
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
