package com.luofangyun.shangchao.domain;


/**
 * 工作通知详细信息
 */
public class MessageBean {
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
        public String images;
    }
}