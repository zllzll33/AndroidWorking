package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * 巡检点设置
 */
public class PatrolPoint {
    public Result result;
    public String status;
    public String summary;
    public class Result {
        public int             totalpage;
        public int             pindex;
        public ArrayList<Json> data;
        public class Json {
           public String pointcode;
           public String pointname;
           public String labelcode;
            public String labelname;
        }
    }
}
