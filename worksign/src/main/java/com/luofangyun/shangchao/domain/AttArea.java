package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * 考勤区域
 */
public class AttArea {
    public Result result;
    public String status;
    public String summary;
    public class Result {
        public int             totalpage;
        public int             pindex;
        public ArrayList<Json> data;
        public class Json {
            public String areacode;
            public String areax;
            public String areay;
            public String areaname;
            public int    arearange;

        }
    }
}
