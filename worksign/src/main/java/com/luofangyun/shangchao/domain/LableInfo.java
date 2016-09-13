package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : luofang
 * Author     : Android-mjc
 * Date       : 2016/8/29 17:02
 */
public class LableInfo {
    public Result result;
    public String status;
    public String summary;

    public class Result {
        public int             pindex;
        public int             totalpage;
        public ArrayList<Json> data;
        public class Json {
            public String labelcode;
            public String labelname;
            public int labeltype;
        }
    }
}
