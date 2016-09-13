package com.luofangyun.shangchao.domain;


import java.util.ArrayList;

public class LabelBean {
    public ArrayList<Result> result;
    public String            status;
    public String            summary;
    public class Result{
        public String labelsn;
        public String labelcode;
        public int labeltype;
        public String labelremark;
        public String labelname;
    }
}

