package com.luofangyun.shangchao.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 忘记密码Dialog
 */

public class NumberSend{

    public static void Send(Context context, String text){
        final Dialog dialog = new AlertDialog.Builder(context).setMessage(text).create();
        dialog.show();
        Timer timer = new Timer(true);      //创建一个Timer定时器
        //1秒后让dialog自动消失
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 1000);
    }
}
