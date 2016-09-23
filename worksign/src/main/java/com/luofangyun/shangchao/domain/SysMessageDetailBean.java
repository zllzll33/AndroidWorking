package com.luofangyun.shangchao.domain;

/**
 * Created by win7 on 2016/9/18.
 */
public class SysMessageDetailBean {
    public Result result;
    public String status;
    public String summary;

    public class Result {
        public String empphone;
        public String empname;
        public String notifytitle;
        public String notifycontent;
        public String notifydate;
        public String empphoto;
    }
}
