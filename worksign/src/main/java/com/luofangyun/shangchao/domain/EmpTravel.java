package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * 出差信息展示
 */
public class EmpTravel {
    public Result result;
    public String status;
    public String summary;
    public class Result{
        public int             pindex;
        public int             totalpage;
        public ArrayList<Json> data;
        public class Json{
            public String reason;
            public String images;
            public String traveladdress;
            public String statu;
            public String stattime;
            public String traveldays;
            public String endtime;
            public String travelreason;
            public String travelcode;
        }
    }
}