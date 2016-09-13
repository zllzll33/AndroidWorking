package com.luofangyun.shangchao.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : luofang
 * Author     : Android-mjc
 * Date       : 2016/7/5 14:38
 */
public class Sign {
    public static String generateSign(Map<String, String> params) {

        List<Map.Entry> paramList = new ArrayList(params.entrySet());

        Collections.sort(paramList, new Comparator() {
            public int compare(Object o1, Object o2) {
                Map.Entry<String, Object> _o1 = (Map.Entry<String, Object>) o1;
                Map.Entry<String, Object> _o2 = (Map.Entry<String, Object>) o2;
                return ((String) _o1.getKey()).toString().compareTo((String) _o2.getKey());
            }
        });
        String paramStr = "";
        for (Map.Entry entry : paramList) {
            Object strKey = entry.getKey();
            Object strObj = entry.getValue();
            //String[] v=(String[])strObj;
            if (strKey.equals("sign"))
                continue;
            paramStr = paramStr + (String) strKey + "=" + strObj.toString();
        }
        return paramStr;
    }

}
