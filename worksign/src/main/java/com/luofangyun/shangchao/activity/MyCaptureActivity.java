package com.luofangyun.shangchao.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanwe.zxing.CaptureActivity;
import com.fanwe.zxing.CaptureActivityHandler;
import com.google.zxing.Result;
import com.luofangyun.shangchao.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class MyCaptureActivity extends CaptureActivity implements View.OnClickListener
{
	/** 是否扫描成功后结束二维码扫描activity，0：否，1:是,值为字符串 */
	public static final String EXTRA_IS_FINISH_ACTIVITY = "extra_is_finish_activity";
	/** 扫描成功返回码 */
	public static final int RESULT_CODE_SCAN_SUCCESS = 10;
	/** 扫描成功，扫描activity结束后Intent中取扫描结果的key */
	public static final String EXTRA_RESULT_SUCCESS_STRING = "extra_result_success_string";
	public    LinearLayout           title;
	protected LinearLayout           buttom;
	private   ImageView              iv_back;
	private   TextView               tv_select_image_local;
	private   CaptureActivityHandler handler;
	private boolean mIsStartByAdvs = false;
	private int mFinishActivityWhenScanFinish = 1;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		init();

	}

	private void init()
	{
		initIntentData();
		LayoutInflater inflater = getLayoutInflater();
		View includeTitle = inflater.inflate(R.layout.include_title, null);
		View includeButtom = inflater.inflate(R.layout.include_buttom, null);
		iv_back = (ImageView) includeTitle.findViewById(R.id.iv_back);
		tv_select_image_local = (TextView) includeButtom.findViewById(R.id.tv_select_image_local);

		title = (LinearLayout) super.findViewById(R.id.act_capture_ll_title);
		buttom = (LinearLayout) super.findViewById(R.id.act_capture_ll_bottom);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, convertDIP2PX(getApplicationContext(), 46));
		title.addView(includeTitle, lp);
		buttom.addView(includeButtom, lp);

		registeClick();

	}

	private void initIntentData()
	{
		mFinishActivityWhenScanFinish = getIntent().getIntExtra(EXTRA_IS_FINISH_ACTIVITY, 1);
//		mIsStartByAdvs = getIntent().getBooleanExtra(BaseActivity.EXTRA_IS_ADVS, false);
		mIsStartByAdvs=false;
	}

	// 转换dip为px
	public static int convertDIP2PX(Context context, int dip)
	{
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
	}
	private void registeClick()
	{
		tv_select_image_local.setOnClickListener(this);
		iv_back.setOnClickListener(this);
	}

	private void clickPhoto()
	{
		selectImageFromAlbum();
	}

	private void clickBack()
	{
		finish();
	}

	@Override
	public void onClick(View arg0)
	{
		switch (arg0.getId())
		{
		case R.id.iv_back:
			clickBack();
			break;
		case R.id.tv_select_image_local:
			clickPhoto();
			break;

		default:
			break;
		}

	}

	@Override
	public void handleDecode(Result result, Bitmap barcode)
	{
		final String resultString = result.getText();
		Log.e("scan",resultString);
		if (resultString.equals(""))
		{
			Toast.makeText(MyCaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		} else
		{
			Intent intent = new Intent();
			intent.putExtra(EXTRA_RESULT_SUCCESS_STRING, resultString);
			if (mFinishActivityWhenScanFinish == 1)
			{

				setResult(RESULT_CODE_SCAN_SUCCESS, intent);
				finish();
				return;
			}
			AlertDialog resutlDialog = new AlertDialog.Builder(MyCaptureActivity.this).create();
			resutlDialog.setTitle("扫描结果");
			resutlDialog.setMessage(resultString);
			resutlDialog.setButton(AlertDialog.BUTTON_POSITIVE, "打开链接", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					if (!isLegalUrlParameters2(resultString)) // 如果url不合法
					{
						Toast.makeText(getApplicationContext(), "该链接不是合法的URL", Toast.LENGTH_SHORT).show();
						// 实现连续扫描
						handler = new CaptureActivityHandler(MyCaptureActivity.this, null, null);
						handler.restartPreviewAndDecode();
						return;
					}
					Intent intent = new Intent(); // 打开链接
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(resultString);
					intent.setData(content_url);
					startActivity(intent);
					finish();
				}
			});

			resutlDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();

					handler = new CaptureActivityHandler(MyCaptureActivity.this, null, null);
					handler.restartPreviewAndDecode();

				}
			});
			resutlDialog.show();
		}
	}

	/**
	 * 过滤url
	 * 
	 * @param str
	 * @return
	 */
	protected boolean isLegalUrlParameters2(String str)
	{
		String strPattern = "[a-zA-z]+://[^\\s]*";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(str);
		return m.find();
	}

	@Override
public void finish()
{
/*	if (mIsStartByAdvs)
	{
		startActivity(new Intent(this, MainActivity.class));
	}*/
	super.finish();
}

}
