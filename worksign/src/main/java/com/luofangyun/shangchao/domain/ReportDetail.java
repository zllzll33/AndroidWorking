package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 *
 */
public class ReportDetail {
    public Result result;
    public String status;
    public String summary;
    public class Result {
        public int             totalpage;
        public int             pindex;
        public ArrayList<Json> data;
        public class Json {
            public String empname;
            public String deptcode;
            public String empphone;
            public String deptname;
        }
    }
}
