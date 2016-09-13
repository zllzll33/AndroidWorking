package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * Created by win7 on 2016/9/13.
 */
public class ConnenctBean {
    public ArrayList<Result> result;
    public String            status;
    public String            summary;
    public static class Result {
        public String empname;
        public String empcode,empphone;
    }
}
