package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * 请假记录展示
 */
public class LeaveExamine {
    public Result result;
    public String status;
    public String summary;
    public class Result{
        public int pindex;
        public int totalpage;
        public ArrayList<Json> data;
        public class Json{
            public String reason;
            public String leavereason;
            public int statu;
            public String leavename;
            public String stattime;
            public String leavedays;
            public String leavecode;
            public String endtime;
            public String leavetype;
            public String images;
            public String leaders;
        }
    }
}