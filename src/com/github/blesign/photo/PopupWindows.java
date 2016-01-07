package com.github.blesign.photo;

import java.io.File;

import com.github.blesign.R;
import com.github.blesign.R.anim;
import com.github.blesign.R.id;
import com.github.blesign.R.layout;
import com.github.blesign.utils.Consts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class PopupWindows extends PopupWindow
{

	private Activity mActivity;
	public static final int TAKE_PICTURE = 0x000009;
	public static final int OTHER_PICTURE=0x0001020;
//	public static final String PIC_PATH="/myimage/";
	private String mPicName;
	public PopupWindows(Activity activity, View parent,String picName)
	{
		 super(activity);
		 mActivity = activity;
		 mPicName = picName;
		View view = View.inflate(activity, R.layout.item_popupwindows, null);
		view.startAnimation(
				AnimationUtils.loadAnimation(activity, R.anim.fade_ins));
		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		ll_popup.startAnimation(
				AnimationUtils.loadAnimation(activity, R.anim.push_bottom_in_2));
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();

		Button camera = (Button) view
				.findViewById(R.id.item_popupwindows_camera);
		Button photo = (Button) view
				.findViewById(R.id.item_popupwindows_Photo);
		Button cancel = (Button) view
				.findViewById(R.id.item_popupwindows_cancel);
		camera.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				photo();
				dismiss();
			}
		});
//		photo.setVisibility(View.GONE);		
		photo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(mActivity,TestPicActivity.class);
				// 设置物品照片；上传头像
				intent.putExtra("image", "image");
				mActivity.startActivityForResult(intent,OTHER_PICTURE);
				dismiss();
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});

	}
	
	public void photo() {
		String status=Environment.getExternalStorageState(); 
		if(status.equals(Environment.MEDIA_MOUNTED))  {
			File dir=new File(Consts.IMG_PATH); 
			if(!dir.exists())dir.mkdirs(); 
			
			Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File file = new File(dir,mPicName);
//			path = file.getPath();
			Uri imageUri = Uri.fromFile(file);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			mActivity.startActivityForResult(openCameraIntent, TAKE_PICTURE);
		}
		else{ 
			Toast.makeText(mActivity, "没有储存卡",Toast.LENGTH_SHORT).show(); 
		} 
	}
}
