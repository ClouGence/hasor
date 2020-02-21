package net.hasor.test.dataql.beans;
public class OrderBean {
    private long   accountID = 123;
    private long   orderID   = 123456789;
    private long   itemID    = 987654321;
    private String itemName;

    public OrderBean(long accountID, int i) {
        this.itemName = "商品名称_" + i;
    }

    public long getAccountID() {
        return accountID;
    }

    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

    public long getOrderID() {
        return orderID;
    }

    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    public long getItemID() {
        return itemID;
    }

    public void setItemID(long itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
