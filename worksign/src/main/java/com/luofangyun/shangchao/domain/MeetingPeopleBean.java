package com.luofangyun.shangchao.domain;

import java.util.List;

/**
 * Created by win7 on 2016/9/14.
 */
public class MeetingPeopleBean {
    public String status;
    public String summary;
    public Result result;
    public class  Result
    {
      public  List<MeetingPeole> data;
    }
    public class MeetingPeole
    {
       public String empcode,empname,empphone,statu;
    }
}
