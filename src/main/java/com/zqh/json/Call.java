package com.zqh.json;

/**
 * Created by zqhxuyuan on 15-3-24.
 */
public class Call {

    private String callId;
    private String title;
    private String createTime;
    private String isUrgent;
    private String catalogId;
    private String comeFrom;
    private String kindId;
    private String eventTime;
    private String eventAddr;
    private String eventZoneId;
    private String callerName;
    private String callerGender;
    private String callerCellphone;
    private String callerEmail;
    private String callerProf;
    private String callerCompany;
    private String callerFax;
    private String callerAddr;
    private String callerPostcode;
    private String callerIp;
    private String isDel;
    private String repeatFlagId;
    private String isDispatch;
    private String isChildDispatch;
    private String isPublic;
    private String privateId;
    private String handlerStatus;
    private String editorId;
    private String runtimeId;
    private String isDisplay;
    private String validateCode;
    private String replySiterId;
    private String callerId;
    private String hastenNum;
    private String hits;

    public static void main(String[] args) {
        String str = "callId: \"FZ15030900004\",\n" +
                "title: \"申请核实社会主义改造经租房自留部分产权面积\",\n" +
                //"createTime: \"Feb 7, 2015 10:45:07 AM\",\n" +
                "isUrgent: 0,\n" +
                "catalogId: 237,\n" +
                "comeFrom: 0,\n" +
                "kindId: 3,\n" +
                "eventTime: \"2015-02-07\",\n" +
                "eventAddr: \"福州市仓山区窑花井弄46号\",\n" +
                "eventZoneId: 762,\n" +
                "callerName: \"陈名世\",\n" +
                "callerGender: 1,\n" +
                "callerCellphone: \"13860669070\",\n" +
                "callerEmail: \"418472446@qq.com\",\n" +
                "callerProf: \"企业管理人员\",\n" +
                "callerCompany: \"福建省毅诺鑫融资担保有限公司\",\n" +
                "callerFax: \"059188563368\",\n" +
                "callerAddr: \"福建省毅诺鑫融资担保有限公司\",\n" +
                "callerPostcode: \"350001\",\n" +
                "callerIp: \"NkrGhlkHY/Pg1Z4XiMfzLQ==\",\n" +
                "isDel: 0,\n" +
                "repeatFlagId: 0,\n" +
                "isDispatch: 1239106,\n" +
                "isChildDispatch: 1239107,\n" +
                "isPublic: 1,\n" +
                "privateId: 0,\n" +
                "handlerStatus: 4,\n" +
                "editorId: 0,\n" +
                "runtimeId: \"2905277\",\n" +
                "isDisplay: 1,\n" +
                "validateCode: \"143829\",\n" +
                "replySiterId: 0,\n" +
                "callerId: 764349,\n" +
                "hastenNum: 0,\n" +
                "hits: 0";

        String[] cols = str.split(",");
        for(String s : cols){
            //System.out.print("private String "+s.split(":")[0] + ";");
        }

        System.out.println((42-8) ==(79-45));
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(String isUrgent) {
        this.isUrgent = isUrgent;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    public String getKindId() {
        return kindId;
    }

    public void setKindId(String kindId) {
        this.kindId = kindId;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventAddr() {
        return eventAddr;
    }

    public void setEventAddr(String eventAddr) {
        this.eventAddr = eventAddr;
    }

    public String getEventZoneId() {
        return eventZoneId;
    }

    public void setEventZoneId(String eventZoneId) {
        this.eventZoneId = eventZoneId;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCallerGender() {
        return callerGender;
    }

    public void setCallerGender(String callerGender) {
        this.callerGender = callerGender;
    }

    public String getCallerCellphone() {
        return callerCellphone;
    }

    public void setCallerCellphone(String callerCellphone) {
        this.callerCellphone = callerCellphone;
    }

    public String getCallerEmail() {
        return callerEmail;
    }

    public void setCallerEmail(String callerEmail) {
        this.callerEmail = callerEmail;
    }

    public String getCallerProf() {
        return callerProf;
    }

    public void setCallerProf(String callerProf) {
        this.callerProf = callerProf;
    }

    public String getCallerCompany() {
        return callerCompany;
    }

    public void setCallerCompany(String callerCompany) {
        this.callerCompany = callerCompany;
    }

    public String getCallerFax() {
        return callerFax;
    }

    public void setCallerFax(String callerFax) {
        this.callerFax = callerFax;
    }

    public String getCallerAddr() {
        return callerAddr;
    }

    public void setCallerAddr(String callerAddr) {
        this.callerAddr = callerAddr;
    }

    public String getCallerPostcode() {
        return callerPostcode;
    }

    public void setCallerPostcode(String callerPostcode) {
        this.callerPostcode = callerPostcode;
    }

    public String getCallerIp() {
        return callerIp;
    }

    public void setCallerIp(String callerIp) {
        this.callerIp = callerIp;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getRepeatFlagId() {
        return repeatFlagId;
    }

    public void setRepeatFlagId(String repeatFlagId) {
        this.repeatFlagId = repeatFlagId;
    }

    public String getIsDispatch() {
        return isDispatch;
    }

    public void setIsDispatch(String isDispatch) {
        this.isDispatch = isDispatch;
    }

    public String getIsChildDispatch() {
        return isChildDispatch;
    }

    public void setIsChildDispatch(String isChildDispatch) {
        this.isChildDispatch = isChildDispatch;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getPrivateId() {
        return privateId;
    }

    public void setPrivateId(String privateId) {
        this.privateId = privateId;
    }

    public String getHandlerStatus() {
        return handlerStatus;
    }

    public void setHandlerStatus(String handlerStatus) {
        this.handlerStatus = handlerStatus;
    }

    public String getEditorId() {
        return editorId;
    }

    public void setEditorId(String editorId) {
        this.editorId = editorId;
    }

    public String getRuntimeId() {
        return runtimeId;
    }

    public void setRuntimeId(String runtimeId) {
        this.runtimeId = runtimeId;
    }

    public String getIsDisplay() {
        return isDisplay;
    }

    public void setIsDisplay(String isDisplay) {
        this.isDisplay = isDisplay;
    }

    public String getValidateCode() {
        return validateCode;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public String getReplySiterId() {
        return replySiterId;
    }

    public void setReplySiterId(String replySiterId) {
        this.replySiterId = replySiterId;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getHastenNum() {
        return hastenNum;
    }

    public void setHastenNum(String hastenNum) {
        this.hastenNum = hastenNum;
    }

    public String getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }

    @Override
    public String toString() {
        return "" +

                (null == callId || callId.equals("null") ? "" : callId) + '\t' +

                (null == title || title.equals("null") ? "" : title) + '\t' +

                (null == createTime || createTime.equals("null") ? "" : createTime) + '\t' +

                (null == isUrgent || isUrgent.equals("null") ? "" : isUrgent) + '\t' +

                (null == catalogId || catalogId.equals("null") ? "" : catalogId) + '\t' +

                (null == comeFrom || comeFrom.equals("null") ? "" : comeFrom) + '\t' +

                (null == kindId || kindId.equals("null") ? "" : kindId) + '\t' +

                (null == eventTime || eventTime.equals("null") ? "" : eventTime) + '\t' +

                (null == eventAddr || eventAddr.equals("null") ? "" : eventAddr) + '\t' +

                (null == eventZoneId || eventZoneId.equals("null") ? "" : eventZoneId) + '\t' +

                (null == callerName || callerName.equals("null") ? "" : callerName) + '\t' +

                (null == callerGender || callerGender.equals("null") ? "" : callerGender) + '\t' +

                (null == callerCellphone || callerCellphone.equals("null") ? "" : callerCellphone) + '\t' +

                (null == callerEmail || callerEmail.equals("null") ? "" : callerEmail) + '\t' +

                (null == callerProf || callerProf.equals("null") ? "" : callerProf) + '\t' +

                (null == callerCompany || callerCompany.equals("null") ? "" : callerCompany) + '\t' +

                (null == callerFax || callerFax.equals("null") ? "" : callerFax) + '\t' +

                (null == callerAddr || callerAddr.equals("null") ? "" : callerAddr) + '\t' +

                (null == callerPostcode || callerPostcode.equals("null") ? "" : callerPostcode) + '\t' +

                (null == callerIp || callerIp.equals("null") ? "" : callerIp) + '\t' +

                (null == isDel || isDel.equals("null") ? "" : isDel) + '\t' +

                (null == repeatFlagId || repeatFlagId.equals("null") ? "" : repeatFlagId) + '\t' +

                (null == isDispatch || isDispatch.equals("null") ? "" : isDispatch) + '\t' +

                (null == isChildDispatch || isChildDispatch.equals("null") ? "" : isChildDispatch) + '\t' +

                (null == isPublic || isPublic.equals("null") ? "" : isPublic) + '\t' +

                (null == privateId || privateId.equals("null") ? "" : privateId) + '\t' +

                (null == handlerStatus || handlerStatus.equals("null") ? "" : handlerStatus) + '\t' +

                (null == editorId || editorId.equals("null") ? "" : editorId) + '\t' +

                (null == runtimeId || runtimeId.equals("null") ? "" : runtimeId) + '\t' +

                (null == isDisplay || isDisplay.equals("null") ? "" : isDisplay) + '\t' +

                (null == validateCode || validateCode.equals("null") ? "" : validateCode) + '\t' +

                (null == replySiterId || replySiterId.equals("null") ? "" : replySiterId) + '\t' +

                (null == callerId || callerId.equals("null") ? "" : callerId) + '\t' +

                (null == hastenNum || hastenNum.equals("null") ? "" : hastenNum) + '\t' +

                (null == hits || hits.equals("null") ? "" : hits) + '\n';
    }
}
