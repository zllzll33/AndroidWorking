package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * 外出数据
 */
public class OutDone{
    public Result result;
    public String status;
    public String summary;
    public class Result{
        public int            pindex;
        public int            totalpage;
        public ArrayList<Json> data;
        public class Json{
            public String reason;
            public String outdays;
            public String images;
            public int statu;
            public String stattime;
            public String outcode;
            public String endtime;
            public String outreason;
        }
    }}


