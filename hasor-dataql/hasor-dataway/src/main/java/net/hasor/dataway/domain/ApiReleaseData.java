package net.hasor.dataway.domain;
import java.util.Date;

public class ApiReleaseData extends ApiInfoData {
    private String releaseId;       // releaseId
    private Date   releaseTime;     // 发布时间

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }
}