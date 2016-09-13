package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * 企业通讯录
 */
public class Corpor {
    public Result result;
    public String status;
    public String summary;
    public class Result {
        public long            pindex;
        public ArrayList<Emps>      emps;
        public long            totalpage;
        public ArrayList<Data> data;
        public class Data {
            public String deptcode;
            public String deptmemo;
            public String deptid;
            public String deptname;
            public String parentdeptname;
            public String parentdept;
            public String updateuser;
            public String companyid;
            public String updatetime;
        }
        public class Emps {
            public String updateuser;
            public String updatetime;
            public String createtime;
            public String empleftday;
            public String empno;
            public String ismng;
            public String empidcard;
            public String companycode;
            public String empname;
            public String deletetime;
            public String deptid;
            public String companyid;
            public String emppassword;
            public String empphone;
            public String deleted;
            public String empstatus;
            public String deptcode;
            public String empaddress;
            public String deptname;
            public String empbirthday;
            public String empid;
            public String rights;
            public String moduleinfo;
            public String regtype;
            public String empcode;
            public String empphoto;
            public String empsex;
            public String emppost;
            public String companyname;
            public String empemail;
            public String username;
            public String empmarriage;
        }
    }
}


