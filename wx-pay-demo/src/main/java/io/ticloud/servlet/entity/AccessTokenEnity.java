package io.ticloud.servlet.entity;

public class AccessTokenEnity {
    private long createTime;
    private Boolean expireTime;
    private String accessToken;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Boolean getExpireTime() {
        return System.currentTimeMillis() - createTime < 7000;
    }

    public void setExpireTime(Boolean expireTime) {
        this.expireTime = expireTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
