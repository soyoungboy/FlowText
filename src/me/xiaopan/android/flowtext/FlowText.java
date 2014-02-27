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

package me.xiaopan.android.flowtext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 流动的文字
 */
public class FlowText extends FrameLayout implements OnGlobalLayoutListener{
	/**
	 * 关键字列表
	 */
	private List<Keyword> keywordList;
	/**
	 * 关键字列表最大长度
	 */
	private int keywordListMaxLength;
	/**
	 * 当前容器的宽
	 */
	private int containerWidth;
	/**
	 * 当前容器的高
	 */
	private int containerHeight;
	/**
	 * 平均宽度
	 */
	private int averageWidth;
	/**
	 * 平均高度
	 */
	private int averageHeight;
	/**
	 * X轴中心坐标
	 */
	private int centerXAxis;
	/**
	 * Y轴中心坐标
	 */
	private int CenterYAxis;
	/**
	 * 是否允许显示动画
	 */
	private boolean isAllowShowAnimation;
	/**
	 * 动画持续时间
	 */
	private long animationDuration;
	/**
	 * 上次显示动画时间
	 */
	private long lastShowAnimationTime;
	/**
	 * 进入动画 - 透明度
	 */
	private AlphaAnimation inAnimationAlpha;
	/**
	 * 进入动画 - 缩放
	 */
	private ScaleAnimation inAnimationScale;
	/**
	 * 进入动画 - 旋转
	 */
	private RotateAnimation inAnimationRotate;
	/**
	 * 退出动画 - 透明度
	 */
	private AlphaAnimation outAnimationAlpha;
	/**
	 * 退出动画 - 缩放
	 */
	private ScaleAnimation outAnimationScale;
	/**
	 * 退出动画 - 旋转
	 */
	private RotateAnimation outAnimationRotate;
	/**
	 * 动画插值器
	 */
	private Interpolator interpolator;
	/**
	 * 位移距离
	 */
	private int displacement;
	/**
	 * 进入动画
	 */
	private boolean inAnimation;
	/**
	 * 随即数分配器
	 */
	private Random random;
	/**
	 * 文字大小最大值
	 */
	private int textSizeMax;
	/**
	 * 文字大小最小值
	 */
	private int textSizeMin;
	/**
	 * 阴影半径
	 */
	private int textShadowRadius;
	/**
	 * 阴影X轴坐标
	 */
	private int textShadowDX;
	/**
	 * 阴影Y轴坐标
	 */
	private int textShadowDY;
	/**
	 * 阴影颜色
	 */
	private int textShadowColor;
	/**
	 * 关键字有更新
	 */
	private boolean keywordHasUpdate;
	/**
	 * 关键字点击事件监听器
	 */
	private OnKeywordClickListener onKeywordClickListener;
	
	public FlowText(Context context, AttributeSet attrs) {
		super(context, attrs);
		getViewTreeObserver().addOnGlobalLayoutListener(this);
		setKeywordList(new ArrayList<Keyword>());//实例化关键字列表
		setKeywordListMaxLength(20);//初始化关键字列表最大长度为10
		setAllowShowAnimation(false);//初始化不允许显示动画
		setAnimationDuration(1000);//设置动画持续时间
		setInAnimationAlpha(new AlphaAnimation(0.0f, 1.0f));//实例化进入动画 - 透明度
		setInAnimationScale(new ScaleAnimation(2.0f, 1.0f, 2.0f, 1.0f));//实例化进入动画 - 缩放
		setOutAnimationAlpha(new AlphaAnimation(1.0f, 0.0f));//实例化退出动画 - 透明度
		setOutAnimationScale(new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f));//实例化退出动画 - 缩放
		setInterpolator(new AccelerateDecelerateInterpolator());//实例化动画插值器
		setInAnimation(true);//初始化动画类型为进入动画
		setDisplacement(100);//初始化动画位移距离
		setRandom(new Random());//实例化随机器
		setTextSizeMax(25);//实例化文字大小最大值
		setTextSizeMin(14);//实例化文字大小最小值
		setTextShadowRadius(3);//设置文字阴影半径
		setTextShadowDX(3);//实例化文字阴影X轴坐标
		setTextShadowDY(3);//实例化文字阴影Y轴坐标
		setTextShadowColor(0x55000000);//实例化文字阴影颜色
	}
	
	/**
	 * 显示进入动画
	 * @return 执行是否成功。false：上次动画尚未结束、尚未获取容器的宽和高、容器内没有子视图、不允许显示动画、接下来该执行退出动画了
	 */
	public boolean showInAnimation(){
		boolean result = false;
		//如果上次动画已经结束
		if(System.currentTimeMillis() - getLastShowAnimationTime() > getAnimationDuration()){
			//设置允许显示动画
			setAllowShowAnimation(true);
			
			//如果关键字有更新，就更新视图
			if(isKeywordHasUpdate()){
				//如果更新视图成功
				if(updateView()){
					//设置关键字没有更新
					setKeywordHasUpdate(false);
				}
			}
			
			//执行进入动画并设置返回结果
			result = executeInAnimation();
		}
		return result;
	}
	
	/**
	 * 显示退出动画
	 * @return 执行是否成功。false：上次动画尚未结束、尚未获取容器的宽和高、容器内没有子视图、不允许显示动画、接下来该执行进入动画了
	 */
	public boolean showOutAnimation(){
		boolean result = false;
		//如果上次动画已经结束
		if(System.currentTimeMillis() - getLastShowAnimationTime() > getAnimationDuration()){
			//设置允许显示动画
			setAllowShowAnimation(true);
			//执行退出动画并设置返回结果
			result = executeOutAnimation();
		}
		return result;
	}
	
	/**
	 * 刷新
	 * @return 
	 */
	public boolean refresh(){
		setKeywordHasUpdate(true);
		setInAnimation(true);
		return showInAnimation();
	}
	
	/**
	 * 更新容器内的子视图
	 * @return 更新是否成功。false：尚未获取容器的宽和高、关键字列表的长度小于等于0
	 */
	private boolean updateView(){
		boolean result = false;
		
		//先删除容器中所有的视图
		removeAllViews();
		
		//如果容器的宽、高都已经知道了并且关键字列表的长度大于0
		if(getContainerWidth() > 0 && getContainerHeight() > 0 && getKeywordList().size() > 0){
        	//设置平均宽、高
			setAverageWidth(getContainerWidth() / getKeywordList().size());
			setAverageHeight(getContainerHeight() / getKeywordList().size());
			
			//根据平均宽、高将当前容器平均分成keywordList.size()份并分别记录其X、Y轴坐标值
			List<Integer> xList = new ArrayList<Integer>();
			List<Integer> yList = new ArrayList<Integer>();
			for(int w = 0; w < getKeywordList().size(); w++){
				xList.add(w * getAverageWidth());
				yList.add(w * getAverageHeight());
			}
			
			//为每一个关键字随机分配一个坐标
			for(Keyword keyword : getKeywordList()){
				keyword.setLeftMargin(xList.remove(getRandom().nextInt(xList.size())));
				keyword.setTopMargin(yList.remove(getRandom().nextInt(yList.size())));
			}
			
			//遍历所有的关键字为其创建TextView并添加到容器中
			for(final Keyword keyword : getKeywordList()){
				//根据关键字创建一个TextView
				TextView textView = getTextView(keyword);
				//设置关键字的实际长度
				keyword.setWidth((int)textView.getPaint().measureText(keyword.getName()));
				//修正左外边距
				reviseLeftMargin(keyword);
            	//添加到容器中
				LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.leftMargin = keyword.getLeftMargin();
				layoutParams.topMargin = keyword.getTopMargin();
				layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
				textView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(getOnKeywordClickListener() != null){
							getOnKeywordClickListener().onKeywordClickListener(keyword.getName());
						}
					}
				});
				textView.setVisibility(View.GONE);
				addView(textView, layoutParams);
			}
			
			result = true;
		}
		return result;
	}
	
	/**
	 * 执行进入动画
	 * @return 执行是否成功。false：尚未获取容器的宽和高、容器内没有子视图、不允许显示动画、接下来该执行退出动画了
	 */
	private boolean executeInAnimation(){
		boolean result = false;
		int childViewCount = getChildCount();
		if(getContainerWidth() > 0 && getContainerHeight() > 0 && childViewCount > 0 && isAllowShowAnimation() && isInAnimation()){
			//遍历容器内所有的子视图让其执行进入动画
			for(int w  = 0; w < childViewCount; w++){
				final TextView textView = (TextView) getChildAt(w);
				textView.setVisibility(View.VISIBLE);
				AnimationSet animationSet = getInAnimation((Keyword)textView.getTag());
				textView.startAnimation(animationSet);
				animationSet.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						textView.setClickable(true);
					}
				});
			}
			
			//设置返回结果为成功
			result = true;
			//设置不允许显示动画
			setAllowShowAnimation(false);
			//更新上次显示动画时间
			setLastShowAnimationTime(System.currentTimeMillis());
			//标记为接下来该执行退出动画了
			setInAnimation(false);
		}
		return result;
	}
	
	/**
	 * 执行退出动画
	 * @return 执行是否成功。false：尚未获取容器的宽和高、容器内没有子视图、不允许显示动画、接下来该执行进入动画了
	 */
	private boolean executeOutAnimation(){
		boolean result = false;
		int childViewCount = getChildCount();
		if(getContainerWidth() > 0 && getContainerHeight() > 0 && childViewCount > 0 && isAllowShowAnimation() && !isInAnimation()){
			//遍历容器内所有的子视图让其执行退出动画并在动画执行完毕时将自己隐藏
			for(int w  = 0; w < childViewCount; w++){
				final TextView textView = (TextView) getChildAt(w);
				textView.setVisibility(View.VISIBLE);
				AnimationSet animationSet = getOutAnimation((Keyword)textView.getTag());
				animationSet.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						textView.setClickable(false);
						textView.setVisibility(View.GONE);
					}
				});
				textView.startAnimation(animationSet);
			}
			
			//设置返回结果为成功
			result = true;
			//设置不允许显示动画
			setAllowShowAnimation(false);
			//更新上次显示动画时间
			setLastShowAnimationTime(System.currentTimeMillis());
			//标记为接下来该执行进入动画了
			setInAnimation(true);
		}
		return result;
	}
	
	/**
	 * 修正左外边距
	 * @param keyword
	 */
	private void reviseLeftMargin(Keyword keyword){
		//如果当前关键字的左外边距小于左内边距
		if(keyword.getLeftMargin() < getLeftPadding()){
			keyword.setLeftMargin(getAverageWidth() + getRandom().nextInt(getAverageWidth() >> 1));
		//如果当前关键字的右外边距超出了当前容器的右内边距
		}else if(keyword.getRightMargin() > getRightPadding()){
			keyword.setLeftMargin(getRightPadding() - getRandom().nextInt(getAverageWidth() >> 1) - keyword.getWidth());
		}
	}
	
	/**
	 * 根据给定的关键字获取用于显示关键字的文本视图
	 * @param keyword 给定的关键字
	 * @return 用于显示关键字的文本视图
	 */
	private TextView getTextView(Keyword keyword){
		TextView textView = new TextView(getContext());
		textView.setText(keyword.getName());
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getTextSize(keyword));
		textView.setTextColor(getTextColor(keyword));
		textView.setShadowLayer(getTextShadowRadius(), getTextShadowDX(), getTextShadowDY(), getTextShadowColor());
		textView.setGravity(Gravity.CENTER);
		textView.setTag(keyword);
		return textView;
	}
	
	/**
	 * 根据给定的关键字获取其文字大小
	 * @param keyword 给定的关键字
	 * @return 文字大小
	 */
	private int getTextSize(Keyword keyword){
		return getTextSizeMin() + getRandom().nextInt(getTextSizeMax() - getTextSizeMin());
	}
	
	/**
	 * 很据给定的关键字获取其文字颜色
	 * @param keyword 给定的关键字
	 * @return 文字颜色
	 */
	private int getTextColor(Keyword keyword){
		return 0xff000000 | getRandom().nextInt(0x0077ffff);
	}
	
	/**
	 * 根据给定的关键字获取进入动画
	 * @param keyword 给定的关键字
	 * @return 进入动画
	 */
	private AnimationSet getInAnimation(Keyword keyword){
		AnimationSet animationSet = new AnimationSet(true);
		//如果插值器不为null
		if(getInterpolator() != null){
			animationSet.setInterpolator(getInterpolator());
		}
		//如果透明度动画不为null
		if(getInAnimationAlpha() != null){
			animationSet.addAnimation(getInAnimationAlpha());
		}
		//如果缩放动画不为null
		if(getInAnimationScale() != null){
			animationSet.addAnimation(getInAnimationScale());
		}
		//如果旋转动画不为null
		if(getInAnimationRotate() != null){
			animationSet.addAnimation(getInAnimationRotate());
		}
		TranslateAnimation translateAnimation = null;
		//如果是在第1象限
		if(keyword.getLeftMargin() < getCenterXAxis() && keyword.getTopMargin() < getCenterYAxis()){
			translateAnimation = new TranslateAnimation(-getDisplacement(), 0, -getDisplacement(), 0);
		//如果是在第2象限
		}else if(keyword.getLeftMargin() > getCenterXAxis() && keyword.getTopMargin() < getCenterYAxis()){
			translateAnimation = new TranslateAnimation(getDisplacement(), 0, -getDisplacement(), 0);
		//如果是在第3象限
		}else if(keyword.getLeftMargin() < getCenterXAxis() && keyword.getTopMargin() > getCenterYAxis()){
			translateAnimation = new TranslateAnimation(-getDisplacement(), 0, getDisplacement(), 0);
		//如果是在第4象限
		}else if(keyword.getLeftMargin() > getCenterXAxis() && keyword.getTopMargin() > getCenterYAxis()){
			translateAnimation = new TranslateAnimation(getDisplacement(), 0, getDisplacement(), 0);
		}
		//如果位移动画不为null
		if(translateAnimation != null){
			translateAnimation.setDuration(getAnimationDuration());
			animationSet.addAnimation(translateAnimation);
		}
		return animationSet;
	}
	
	/**
	 * 根据给定的关键字获取退出动画
	 * @param keyword 给定的关键字
	 * @return 退出动画
	 */
	private AnimationSet getOutAnimation(Keyword keyword){
		AnimationSet animationSet = new AnimationSet(true);
		//如果插值器不为null
		if(getInterpolator() != null){
			animationSet.setInterpolator(getInterpolator());
		}
		//如果透明度动画不为null
		if(getOutAnimationAlpha() != null){
			animationSet.addAnimation(getOutAnimationAlpha());
		}
		//如果缩放动画不为null
		if(getOutAnimationScale() != null){
			animationSet.addAnimation(getOutAnimationScale());
		}
		//如果旋转动画不为null
		if(getOutAnimationRotate() != null){
			animationSet.addAnimation(getOutAnimationRotate());
		}
		
		TranslateAnimation translateAnimation = null;
		//如果是在第1象限
		if(keyword.getLeftMargin() < getCenterXAxis() && keyword.getTopMargin() < getCenterYAxis()){
			translateAnimation = new TranslateAnimation(0, -getDisplacement(), 0, -getDisplacement());
		//如果是在第2象限
		}else if(keyword.getLeftMargin() > getCenterXAxis() && keyword.getTopMargin() < getCenterYAxis()){
			translateAnimation = new TranslateAnimation(0, getDisplacement(), 0, -getDisplacement());
		//如果是在第3象限
		}else if(keyword.getLeftMargin() < getCenterXAxis() && keyword.getTopMargin() > getCenterYAxis()){
			translateAnimation = new TranslateAnimation(0, -getDisplacement(), 0, getDisplacement());
		//如果是在第4象限
		}else if(keyword.getLeftMargin() > getCenterXAxis() && keyword.getTopMargin() > getCenterYAxis()){
			translateAnimation = new TranslateAnimation(0, getDisplacement(), 0, getDisplacement());
		}
		//如果位移动画不为null
		if(translateAnimation != null){
			translateAnimation.setDuration(getAnimationDuration());
			animationSet.addAnimation(translateAnimation);
		}
		return animationSet;
	}

	@Override
	public void onGlobalLayout() {
		//记录当前容器的宽和高
		setContainerWidth(getWidth());
		setContainerHeight(getHeight());
		//设置X、Y轴中心坐标
		setCenterXAxis(getContainerWidth() >> 1);
		setCenterYAxis(getContainerHeight() >> 1);
		//如果关键字有更新，就更新视图
		if(isKeywordHasUpdate()){
			//如果更新视图成功
			if(updateView()){
				//设置关键字没有更新
				setKeywordHasUpdate(false);
			}
		}
		//执行进入动画
		executeInAnimation();
	}
	
	/**
	 * 从给定的关键字名字数组中取出最多getKeywordsMaxLength()个关键字名字放入关键字列表里
	 * @param keywordNames 关键字名字数组，最多只能放入getKeywordsMaxLength()个
	 */
	public void putKeywords(String... keywordNames) {
        getKeywordList().clear();
		for (int w = 0; w < (keywordNames.length < getKeywordListMaxLength() ? keywordNames.length : getKeywordListMaxLength()); w++) {
            getKeywordList().add(new Keyword(keywordNames[w]));
        }
		setKeywordHasUpdate(true);
	}
	
	/**
	 * 从给定的关键字名字列表中取出最多getKeywordsMaxLength()个关键字名字放入关键字列表里
	 * @param keywordNameList 关键字名字列表，最多只能放入getKeywordsMaxLength()个
	 */
	public void putKeywords(List<String> keywordNameList) {
		getKeywordList().clear();
		for (int w = 0; w < (keywordNameList.size() < getKeywordListMaxLength() ? keywordNameList.size() : getKeywordListMaxLength()); w++) {
            getKeywordList().add(new Keyword(keywordNameList.get(w)));
        }
		setKeywordHasUpdate(true);
	}
	
	/**
	 * 从给定的关键字数组中取出最多getKeywordsMaxLength()个关键字名字放入关键字列表里
	 * @param keywords 关键字数组，最多只能放入getKeywordsMaxLength()个
	 */
	public void putKeywords(Keyword... keywords) {
		getKeywordList().clear();
		for (int w = 0; w < (keywords.length < getKeywordListMaxLength() ? keywords.length : getKeywordListMaxLength()); w++) {
            getKeywordList().add(keywords[w]);
        }
		setKeywordHasUpdate(true);
	}
	
	/**
	 * 从给定的关键字列表中取出最多getKeywordsMaxLength()个关键字名字放入关键字列表里
	 * @param keywordList 关键字列表，最多只能放入getKeywordsMaxLength()个
	 * @return 放进去的关键字的个数
	 */
	public void putKeywordList(List<Keyword> keywordsList) {
		getKeywordList().clear();
		for (int w = 0; w < (keywordList.size() < getKeywordListMaxLength() ? keywordList.size() : getKeywordListMaxLength()); w++) {
			getKeywordList().add(keywordList.get(w));
        }
		setKeywordHasUpdate(true);
	}
	
	/**
	 * 获取左内边距
	 * @return 左内边距
	 */
	public int getLeftPadding(){
		return getAverageWidth() >> 1;
	}
	
	/**
	 * 获取右内边距
	 * @return 右内边距
	 */
	public int getRightPadding(){
		return getContainerWidth() - (getAverageWidth() >> 1);
	}
	
	/**
	 * 获取顶内边距
	 * @return 顶内边距
	 */
	public int getTopPadding(){
		return getAverageHeight() >> 1;
	}
	
	/**
	 * 获取底内边距
	 * @return 底内边距
	 */
	public int getBottomPadding(){
		return getContainerHeight() - (getAverageHeight() >> 1);
	}
	
	public List<Keyword> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List<Keyword> keywordList) {
		this.keywordList = keywordList;
	}

	public int getKeywordListMaxLength() {
		return keywordListMaxLength;
	}

	public void setKeywordListMaxLength(int keywordListMaxLength) {
		this.keywordListMaxLength = keywordListMaxLength;
	}

	public int getContainerWidth() {
		return containerWidth;
	}

	public void setContainerWidth(int containerWidth) {
		this.containerWidth = containerWidth;
	}

	public int getContainerHeight() {
		return containerHeight;
	}

	public void setContainerHeight(int containerHeight) {
		this.containerHeight = containerHeight;
	}

	public int getAverageWidth() {
		return averageWidth;
	}

	public void setAverageWidth(int averageWidth) {
		this.averageWidth = averageWidth;
	}

	public int getAverageHeight() {
		return averageHeight;
	}

	public void setAverageHeight(int averageHeight) {
		this.averageHeight = averageHeight;
	}

	public int getCenterXAxis() {
		return centerXAxis;
	}

	public void setCenterXAxis(int centerXAxis) {
		this.centerXAxis = centerXAxis;
	}

	public int getCenterYAxis() {
		return CenterYAxis;
	}

	public void setCenterYAxis(int centerYAxis) {
		this.CenterYAxis = centerYAxis;
	}

	public boolean isAllowShowAnimation() {
		return isAllowShowAnimation;
	}

	public void setAllowShowAnimation(boolean isAllowShowAnimation) {
		this.isAllowShowAnimation = isAllowShowAnimation;
	}

	public long getAnimationDuration() {
		return animationDuration;
	}

	public void setAnimationDuration(long animationDuration) {
		this.animationDuration = animationDuration;
		if(getInAnimationAlpha() != null){
			getInAnimationAlpha().setDuration(getAnimationDuration());
		}
		if(getInAnimationScale() != null){
			getInAnimationScale().setDuration(getAnimationDuration());
		}
		if(getInAnimationRotate() != null){
			getInAnimationRotate().setDuration(getAnimationDuration());
		}
		if(getOutAnimationAlpha() != null){
			getOutAnimationAlpha().setDuration(getAnimationDuration());
		}
		if(getOutAnimationScale() != null){
			getOutAnimationScale().setDuration(getAnimationDuration());
		}
		if(getOutAnimationRotate() != null){
			getOutAnimationRotate().setDuration(getAnimationDuration());
		}
	}

	public long getLastShowAnimationTime() {
		return lastShowAnimationTime;
	}

	public void setLastShowAnimationTime(long lastShowAnimationTime) {
		this.lastShowAnimationTime = lastShowAnimationTime;
	}

	public AlphaAnimation getInAnimationAlpha() {
		return inAnimationAlpha;
	}

	public void setInAnimationAlpha(AlphaAnimation inAnimationAlpha) {
		this.inAnimationAlpha = inAnimationAlpha;
		if(this.inAnimationAlpha != null){
			this.inAnimationAlpha.setDuration(getAnimationDuration());
		}
	}

	public ScaleAnimation getInAnimationScale() {
		return inAnimationScale;
	}

	public void setInAnimationScale(ScaleAnimation inAnimationScale) {
		this.inAnimationScale = inAnimationScale;
		if(this.inAnimationScale != null){
			this.inAnimationScale.setDuration(getAnimationDuration());
		}
	}

	public RotateAnimation getInAnimationRotate() {
		return inAnimationRotate;
	}

	public void setInAnimationRotate(RotateAnimation inAnimationRotate) {
		this.inAnimationRotate = inAnimationRotate;
		if(this.inAnimationRotate != null){
			this.inAnimationRotate.setDuration(getAnimationDuration());
		}
	}

	public AlphaAnimation getOutAnimationAlpha() {
		return outAnimationAlpha;
	}

	public void setOutAnimationAlpha(AlphaAnimation outAnimationAlpha) {
		this.outAnimationAlpha = outAnimationAlpha;
		if(this.outAnimationAlpha != null){
			this.outAnimationAlpha.setDuration(getAnimationDuration());
		}
	}

	public ScaleAnimation getOutAnimationScale() {
		return outAnimationScale;
	}

	public void setOutAnimationScale(ScaleAnimation outAnimationScale) {
		this.outAnimationScale = outAnimationScale;
		if(this.outAnimationScale != null){
			this.outAnimationScale.setDuration(getAnimationDuration());
		}
	}

	public RotateAnimation getOutAnimationRotate() {
		return outAnimationRotate;
	}

	public void setOutAnimationRotate(RotateAnimation outAnimationRotate) {
		this.outAnimationRotate = outAnimationRotate;
		if(this.outAnimationRotate != null){
			this.outAnimationRotate.setDuration(getAnimationDuration());
		}
	}

	public Interpolator getInterpolator() {
		return interpolator;
	}

	public void setInterpolator(Interpolator interpolator) {
		this.interpolator = interpolator;
	}

	public boolean isInAnimation() {
		return inAnimation;
	}

	public void setInAnimation(boolean inAnimation) {
		this.inAnimation = inAnimation;
	}

	public int getDisplacement() {
		return displacement;
	}

	public void setDisplacement(int displacement) {
		this.displacement = displacement;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public int getTextSizeMax() {
		return textSizeMax;
	}

	public void setTextSizeMax(int textSizeMax) {
		this.textSizeMax = textSizeMax;
	}

	public int getTextSizeMin() {
		return textSizeMin;
	}

	public void setTextSizeMin(int textSizeMin) {
		this.textSizeMin = textSizeMin;
	}

	public int getTextShadowRadius() {
		return textShadowRadius;
	}

	public void setTextShadowRadius(int textShadowRadius) {
		this.textShadowRadius = textShadowRadius;
	}

	public int getTextShadowDX() {
		return textShadowDX;
	}

	public void setTextShadowDX(int textShadowDX) {
		this.textShadowDX = textShadowDX;
	}

	public int getTextShadowDY() {
		return textShadowDY;
	}

	public void setTextShadowDY(int textShadowDY) {
		this.textShadowDY = textShadowDY;
	}

	public int getTextShadowColor() {
		return textShadowColor;
	}

	public void setTextShadowColor(int textShadowColor) {
		this.textShadowColor = textShadowColor;
	}

	public boolean isKeywordHasUpdate() {
		return keywordHasUpdate;
	}

	public void setKeywordHasUpdate(boolean keywordHasUpdate) {
		this.keywordHasUpdate = keywordHasUpdate;
	}

	public OnKeywordClickListener getOnKeywordClickListener() {
		return onKeywordClickListener;
	}

	public void setOnKeywordClickListener(
			OnKeywordClickListener onKeywordClickListener) {
		this.onKeywordClickListener = onKeywordClickListener;
	}

	/**
	 * 关键字
	 * @author xiaopan
	 */
	public class Keyword{
		/**
		 * 名字
		 */
		private String name;
		/**
		 * 左外边距
		 */
		private int leftMargin;
		/**
		 * 顶外边距
		 */
		private int topMargin;
		/**
		 * 宽度
		 */
		private int width;
		
		/**
		 * 创建一个关键字
		 * @param name 关键字的名字
		 */
		public Keyword(String name){
			setName(name);
		}
		
		/**
		 * 获取名字
		 * @return 名字
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * 设置名字
		 * @param name 名字
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * 获取左外边距
		 * @return 左外边距
		 */
		public int getLeftMargin() {
			return leftMargin;
		}

		/**
		 * 设置左外边距
		 * @param leftMargin 左外边距
		 */
		public void setLeftMargin(int leftMargin) {
			this.leftMargin = leftMargin;
		}

		/**
		 * 获取顶外边距
		 * @return 顶外边距
		 */
		public int getTopMargin() {
			return topMargin;
		}

		/**
		 * 设置顶外边距
		 * @param topMargin 顶外边距
		 */
		public void setTopMargin(int topMargin) {
			this.topMargin = topMargin;
		}

		/**
		 * 获取右外边距
		 * @return 右外边距
		 */
		public int getRightMargin() {
			return getLeftMargin() + getWidth();
		}

		/**
		 * 获取宽度
		 * @return 宽度
		 */
		public int getWidth() {
			return width;
		}

		/**
		 * 设置宽度
		 * @param width 宽度
		 */
		public void setWidth(int width) {
			this.width = width;
		}
	}
	
	/**
	 * 关键字点击事件监听器
	 */
	public interface OnKeywordClickListener{
		public void onKeywordClickListener(String keywordName);
	}
}
