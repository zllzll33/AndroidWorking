package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

public class AttPeopBean {
    public Result result;
    public String status;
    public String summary;
    public class Result{
        public int pindex;
        public int totalpage;
        public ArrayList<Json> data;
        public class Json{
            public String empphone;
            public String empname;
        }
    }

}
