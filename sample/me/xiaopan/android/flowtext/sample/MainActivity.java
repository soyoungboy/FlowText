package me.xiaopan.android.flowtext.sample;

import me.xiaopan.android.flowtext.FlowText;
import me.xiaopan.android.flowtext.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class MainActivity extends Activity {

	private FlowText flowText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		flowText = (FlowText) findViewById(R.id.flowText_main);
		
		flowText.putKeywords("小米", "三星", "摩托罗拉", "诺基亚", "HTC", "苹果", "中兴", "华为", "酷派", "联想", "青橙", "金立", "OPPO");
		flowText.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onGlobalLayout() {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
					flowText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}else{
					flowText.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
				flowText.showInAnimation();
			}
		});
	}
}
