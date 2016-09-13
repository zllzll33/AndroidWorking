package com.luofangyun.shangchao.domain;


import java.util.ArrayList;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : luofang
 * Author     : Android-mjc
 * Date       : 2016/7/21 10:31
 */
public class LeaveType {
    public ArrayList<Result> result;
    public String            status;
    public String            summary;
    public class Result{
        public int id;
        public String leavename;
    }
}
