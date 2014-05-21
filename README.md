# ![Logo](https://github.com/xiaopansky/FlowText/raw/master/res/drawable-mdpi/ic_launcher.png) FlowText

FlowText是Android上一个随机显示文字的View，注意：文字数量不能太多、太长

![sample](https://github.com/xiaopansky/FlowText/raw/master/docs/sample.png)

##Usage
####1.在布局中引用FlowText
activity_mai.xml
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >
    
    <me.xiaopan.android.flowtext.FlowText 
        android:id="@+id/flowText_main"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
```

####2.在Activity中添加文字
```java
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
```

##Downloads
>* **[android-flow-text-1.0.0.jar](https://github.com/xiaopansky/FlowText/raw/master/releases/android-flow-text-1.0.0.jar)**

##License
```java
/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```
