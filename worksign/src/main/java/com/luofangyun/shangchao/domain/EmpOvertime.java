package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * 加班信息展示
 */
public class EmpOvertime {
    public Result result;
    public String status;
    public String summary;
    public class Result {
        public int pindex;
        public int totalpage;
        public ArrayList<Json> data;
        public class Json {
            public String reason;
            public String overreason;
            public String statu;
            public String overcode;
            public String stattime;
            public String overdays;
            public String endtime;
            public Object images;
        }
    }
}

