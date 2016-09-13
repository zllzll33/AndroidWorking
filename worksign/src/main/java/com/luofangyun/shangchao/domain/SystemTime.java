package com.luofangyun.shangchao.domain;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : luofang
 * Author     : Android-mjc
 * Date       : 2016/8/25 10:38
 */
public class SystemTime {
    public String summary;
    public String status;
    public Result result;
    public class Result{
        public String mindate;
        public String maxdate;
        public String curdate;
    }
}
