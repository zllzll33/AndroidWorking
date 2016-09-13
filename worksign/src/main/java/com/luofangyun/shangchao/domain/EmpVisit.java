package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : luofang
 * Author     : Android-mjc
 * Date       : 2016/7/27 15:15
 */
public class EmpVisit {
    public Result result;
    public String status;
    public String summary;
    public class Result {
        public int pindex;
        public int totalpage;
        public ArrayList<Json> data;
        public class Json {
            public String custom;
            public String visitaddress;
            public String visitsummary;
            public String visitcode;
            public String id;
            public String updateuser;
            public String images;
            public String imgcode;
            public String updatetime;
            public String visittime;
            public String visity;
            public String visitx;
        }
    }
}