package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * Created by win7 on 2016/9/19.
 */
public class PatrolRecordBean {
    public Result result;
    public String status;
    public String summary;

    public class Result {
        public int             totalpage;
        public int             pindex;
        public ArrayList<Json> data;

        public class Json {
            public String patroldate;
            public String linename,linecode;
            public String starttime;
            public String summary;
        }
    }
}
