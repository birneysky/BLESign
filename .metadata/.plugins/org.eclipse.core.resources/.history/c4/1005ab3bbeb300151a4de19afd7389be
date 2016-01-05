package com.example.slidingmenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.http.LoadingIndicator;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.entity.User;
import com.zjjin.utils.ConstsUser;
import com.zjjin.utils.MobileNumber;

public class LoginActivity extends Activity {
	private static final String TAG=LoginActivity.class.getSimpleName();
	private EditText edt_username,edt_pass;
	private Button btn_login,btn_del_pass,btn_register,btn_lose_pass;
	
	private SharedPreferences usersp;
	private String phoneNum;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		IbeaconApplication.getInstance().addActivity(this);
		getdata();
		initView();
		setListener();
	}

	private void getdata() {
		// TODO Auto-generated method stub
		phoneNum = getIntent().getStringExtra(ConstsUser.PHONENUM);
	}

	private void setListener() {
		 edt_username.setOnClickListener(clickListener);
		 edt_pass.setOnClickListener(clickListener);
		 btn_login.setOnClickListener(clickListener);
		 btn_del_pass.setOnClickListener(clickListener);
		 btn_register.setOnClickListener(clickListener);
		 btn_lose_pass.setOnClickListener(clickListener);
	}

	public static void actionStart(Context context, String phoneNum){
		Intent intent = new Intent(context, LoginActivity.class);
		intent.putExtra(ConstsUser.PHONENUM, phoneNum);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	private void initView() {
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
		edt_username = (EditText)findViewById(R.id.et_phonenum);
		edt_pass = (EditText)findViewById(R.id.et_password);
		if(phoneNum!=null){
			edt_username.setText(phoneNum);
		}else{
			edt_username.setText(usersp.getString(ConstsUser.PHONENUM, null));
		}
//		edt_username.setText("13260398606");
//		edt_pass.setText("111111");
		btn_login =(Button)findViewById(R.id.btn_login);
		btn_del_pass =(Button)findViewById(R.id.btn_delete_pwd);
		btn_register =(Button)findViewById(R.id.btn_login_regist);
		btn_lose_pass =(Button)findViewById(R.id.btn_login_forget_pwd);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	//监听
	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_login:
				String username = edt_username.getText().toString();
				String pass = edt_pass.getText().toString();
				checkUser(username, pass);
				break;
			case R.id.btn_delete_pwd:
				String pwd = edt_pass.getText().toString();
				if(pwd != null){
					edt_pass.setText("");
				}
				break;
			case R.id.btn_login_regist:
				Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_login_forget_pwd:
				String tell = edt_username.getText().toString();
				Intent intentFort = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
				if(tell!=null && !"".equals(tell))intentFort.putExtra("phoneNum", tell);
				startActivity(intentFort);
				break;
			}
		}
	};
	protected Editor edit;
	
	private void checkUser(String username, String pass) {
		if(username != null && pass != null){
			if(username.length() != 11){
				Utils.showMsg(getApplicationContext(), "手机号码不是11位，请重新输入！");
				return;
			}
			if(!MobileNumber.isMobileNO(username)){
				Utils.showMsg(getApplicationContext(), "手机号码输入错误，请重新输入！");
				return;
			}
			Editor editor = usersp.edit();
			editor.putString(ConstsUser.PHONENUM, username);
			editor.commit();
			AppRequest request = new AppRequest();
			request.setmRequestURL("/login/MyUserAction!loginJson");
			request.putPara("telephone",username);
			request.putPara("password",pass);
			request.putPara("clientType", "android");
			new AppThread(LoginActivity.this,request,new AppHandler() {
				@Override
				protected void handle(AppRequest request, AppResponse response) {
					if("0".equals(response.getmCode())){//成功返回
						//解析json
						Gson gson = new Gson();
						User user = gson.fromJson(response.getData(), User.class);
						edit = usersp.edit();//保存全部信息到sp
						edit.putInt(ConstsUser.ID, user.getId());
						edit.putString(ConstsUser.USERNAME, user.getUserName());
						edit.putString(ConstsUser.USERPHOTOADDRESS, user.getUserPhoto());
						edit.putString(ConstsUser.USERGENDER, user.getUserGender());
						edit.putString(ConstsUser.USERRIGON, user.getUserRigon());
						edit.putString(ConstsUser.USERADDRESS, user.getUserAddress());
						edit.putString(ConstsUser.USERSIGN, user.getUserSign());
						edit.putString(ConstsUser.PHONENUM, user.getPhoneNum());
						edit.putString(ConstsUser.PASSWORD, user.getPassword());
						edit.commit();
						// 密码正确
						Intent intent = new Intent(LoginActivity.this,MainActivity.class);
						startActivity(intent);
						LoginActivity.this.finish();
					}else{
						Utils.showMsg(getApplicationContext(), "登录失败,请重新登录！");
					}
				}
			}).start();
			LoadingIndicator.show(LoginActivity.this, "正在登录，请稍后...");
		}else{
			Utils.showMsg(getApplicationContext(), "用户名或密码不能为空!");
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		IbeaconApplication.getInstance().removeAcitvity(LoginActivity.this);
	}

}
