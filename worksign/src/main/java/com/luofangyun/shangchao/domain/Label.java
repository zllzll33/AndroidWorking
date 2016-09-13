package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * 标签信息
 */
public class Label {
    public Result result;
    public String status;
    public String summary;

    public class Result {
        public int             totalpage;
        public int             pindex;
        public ArrayList<Json> data;

        public class Json {
            public String labelcode;
            public String labelname;
            public String labelarea;
            public String labelsn;
            public int labeltype;

        }
    }
}
