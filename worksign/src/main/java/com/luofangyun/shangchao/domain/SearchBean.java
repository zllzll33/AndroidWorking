package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * 搜索团队
 */
public class SearchBean {
    public ArrayList<Result> result;
    public String            status;
    public String            summary;
    public class Result {
        public String companyname;
        public String companycode;
    }
}