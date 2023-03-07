package cn.bywin.business.common.base;

import java.io.Serializable;

public class UserDo implements Serializable {

    /**
     * 用户名
     */
    String userId;
    String userName;
    String chnName;

    String topOrgNo; //用户第一级部门编码
    String topOrgName; //用户第一级部门名称
    /**
     * 用户所属部门
     */
    String orgNo; //用户所在部门编码

    String orgName;//用户所在部门名称

    String tokenId;

    String sessionId;

    Long lastAct;

    Integer adminIf;

    String ip;


    long cachSecond = 0L;


    public Integer getAdminIf() {
        return adminIf;
    }

    public void setAdminIf(Integer adminIf) {
        this.adminIf = adminIf;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTopOrgNo() {
        return topOrgNo;
    }

    public void setTopOrgNo(String topOrgNo) {
        this.topOrgNo = topOrgNo;
    }

    public String getTopOrgName() {
        return topOrgName;
    }

    public void setTopOrgName(String topOrgName) {
        topOrgName = topOrgName;
    }

    public String getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getLastAct() {
        return lastAct;
    }

    public void setLastAct(Long lastAct) {
        this.lastAct = lastAct;
    }

    public void resetLastAct() {
        this.lastAct = System.currentTimeMillis();
    }

    public String getChnName() {
        return chnName;
    }

    public void setChnName(String chnName) {
        this.chnName = chnName;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getCachSecond() {
        return cachSecond;
    }

    public void setCachSecond(long cachSecond) {
        this.cachSecond = cachSecond;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("userId:").append(userId).append(",");
        sb.append("userName:").append(userName).append(",");
        sb.append("chnName:").append(chnName).append(",");
        sb.append("topOrgNo:").append(topOrgNo).append(",");
        sb.append("topOrgName:").append(topOrgName).append(",");
        sb.append("orgNo:").append(orgNo).append(",");
        sb.append("orgName:").append(orgName).append(",");
        sb.append("tokenId:").append(tokenId).append(",");
        sb.append("sessionId:").append(sessionId).append(",");
        sb.append("adminIf:").append(adminIf).append(",");
        sb.append("cachSecond:").append(cachSecond).append("");
        sb.append("lastAct:").append(lastAct).append("");
        return sb.toString();
    }
}
