package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * Created by win7 on 2016/9/19.
 */
public class PatrolRecordDetailBean {
    public Result result;
    public String status;
    public String summary;

    public class Result {
        public int             totalpage;
        public int             pindex;
        public ArrayList<Json> data;

        public class Json {
            public String pointname;
            public String linename,linecode,pointcode;
            public String status,starttime,endtime;

        }
    }
}
