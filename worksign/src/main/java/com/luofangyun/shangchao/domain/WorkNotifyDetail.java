package com.luofangyun.shangchao.domain;

public class WorkNotifyDetail {
    public String summary;
    public String status;
    public Result result;
    public class Result {
        /**
         * 请假
         */
        public String leavetype;
        public String leavename;
        public String stattime;
        public String endtime;
        public int    leavedays;
        public String leavereason;
        public String images;
        public int    statu;
        public String reason;
        /**
         * 外出
         */
        public int    outdays;
        public String outreason;
        /**
         * 出差
         */
        public int    traveldays;
        public String travelreason;
        /**
         * 加班
         */
        public int    overdays;
        public String overreason;
    }
}
