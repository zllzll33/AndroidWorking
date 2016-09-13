package com.luofangyun.shangchao.domain;

/**
 * 登录
 */
public class UerEnBean {
    public Result result;
    public String status;
    public String summary;
    public class Result {
        public String     updateuser;
        public String     updatetime;
        public String     createtime;
        public String     empleftday;
        public String     empno;
        public int        ismng;
        public String     empidcard;
        public String     companycode;
        public String     empname;
        public String     deletetime;
        public String     deptid;
        public String     companyid;
        public String     emppassword;
        public String     empphone;
        public String     deleted;
        public String     empstatus;
        public String     deptcode;
        public String     empaddress;
        public String     deptname;
        public String     empbirthday;
        public String     empid;
        public String     rights;
        public Moduleinfo moduleinfo;
        public String     regtype;
        public String     empcode;
        public String     empphoto;
        public String     empsex;
        public String     emppost;
        public String     companyname;
        public String     empemail;
        public String     username;
        public String     empmarriage;
        public class Moduleinfo {
            public int work;
            public int att;
            public int notice;
            public int meet;
            public int patrol;
            public int label;
        }
    }

}
