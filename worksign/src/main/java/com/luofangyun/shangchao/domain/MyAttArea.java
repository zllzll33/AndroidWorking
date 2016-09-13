package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : luofang
 * Author     : Android-mjc
 * Date       : 2016/8/28 17:56
 */
public class MyAttArea {
    public Result result;
    public String status;
    public String summary;
    public class Result{
        public long            pindex;
        public long            totalpage;
        public ArrayList<Json> data;
        public class Json{
            public String sbtime;
            public String xbtime;
            public int sbstatu;
            public String attdate;
            public int xbstatu;
        }
    }
}


