package net.hasor.dataql.domain;
import java.util.Date;

public class DataBean {
    private String       name  = "马三";
    private int          age   = 32;
    private Date         time  = new Date();
    private Thread.State state = Thread.State.TIMED_WAITING;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Thread.State getState() {
        return state;
    }

    public void setState(Thread.State state) {
        this.state = state;
    }
}