package com.github.blesign.photo;

import java.io.Serializable;
import java.util.List;

import com.github.blesign.R;
import com.github.blesign.R.drawable;
import com.github.blesign.R.id;
import com.github.blesign.R.layout;
import com.github.blesign.utils.Consts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
/**
 * 添加照片？相册
 * @author birney
 *
 */
public class TestPicActivity extends Activity {
	// ArrayList<Entity> dataList;//用来装载数据源的列表
	private Button btnCancle;
	List<ImageBucket> dataList;
	GridView gridView;
	ImageBucketAdapter adapter;// 自定义的适配器
	AlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static Bitmap bimap;
	private String type;//根据它判断是发布众寻还是其他
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_bucket);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		initData();
		initView();
		addListener();
	}

	private void addListener() {
		btnCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				TestPicActivity.this.finish();
			}
		});
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		type = intent.getStringExtra("image");
		dataList = helper.getImagesBucketList(false);
		bimap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_addpic_unfocused);
	}

	/**
	 * 初始化view视图
	 */
	private void initView() {
		btnCancle = (Button)findViewById(R.id.btn_activity_image_bucket_cancle);
		gridView = (GridView) findViewById(R.id.gridview);
		adapter = new ImageBucketAdapter(TestPicActivity.this, dataList);
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/**
				 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状态，
				 * 来判断是否显示选中效果。 至于选中效果的规则，下面适配器的代码中会有说明
				 */
				// if(dataList.get(position).isSelected()){
				// dataList.get(position).setSelected(false);
				// }else{
				// dataList.get(position).setSelected(true);
				// }
				/**
				 * 通知适配器，绑定的数据发生了改变，应当刷新视图
				 */
				// adapter.notifyDataSetChanged();
				Intent intent = new Intent(TestPicActivity.this, ImageGridActivity.class);
				intent.putExtra(TestPicActivity.EXTRA_IMAGE_LIST,
						(Serializable) dataList.get(position).imageList);
				intent.putExtra("image", "image");
				startActivityForResult(intent,PopupWindows.OTHER_PICTURE);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PopupWindows.OTHER_PICTURE && data != null){ // 上传头像；设置物品照片
			 String imagePath = data.getStringExtra("imagePath");// /storage/emulated/0/UCDownloads/雨.jpg;
			 Intent retIntent = new Intent();
			 retIntent.putExtra("imagePath", imagePath);
			 setResult(1, retIntent);
			 finish();
		}
	}
}
