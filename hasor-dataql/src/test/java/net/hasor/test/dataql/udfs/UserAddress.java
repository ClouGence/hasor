package net.hasor.test.dataql.udfs;
public class UserAddress {
    private String zip;
    private String code;
    private String address;

    public UserAddress(int i) {
        this.zip = "1234" + i;
        this.code = "c_" + i;
        this.address = "this is detail address info.";
    }
}