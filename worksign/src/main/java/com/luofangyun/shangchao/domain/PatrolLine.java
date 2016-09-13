package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * 巡检线路
 */
public class PatrolLine {
    public Result result;
    public String status;
    public String summary;

    public class Result {
        public int             totalpage;
        public int             pindex;
        public ArrayList<Json> data;

        public class Json {
            public String linecode;
            public String linename;
            public String starttime;
            public String endtime,contacts,empname;
        }
    }
}
