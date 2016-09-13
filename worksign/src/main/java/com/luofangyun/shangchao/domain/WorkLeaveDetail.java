package com.luofangyun.shangchao.domain;

public class WorkLeaveDetail {
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
    }
}
