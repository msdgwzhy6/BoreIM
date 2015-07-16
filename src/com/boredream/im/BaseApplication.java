package com.boredream.im;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.boredream.im.utils.CollectionUtils;
import com.boredream.im.utils.CommonConstants;
import com.boredream.im.utils.ImageOptHelper;
import com.boredream.im.utils.SharePreferenceUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class BaseApplication extends Application {

	public static BaseApplication mInstance;
	
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;

	public static BmobGeoPoint lastPoint = null;// 上一次定位到的经纬度

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		BmobChat.DEBUG_MODE = true;
		mInstance = this;
		init();
	}

	private void init() {
		mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		
		initImageLoader(getApplicationContext());
		
		initBaidu();
		
		// 若用户登陆过，则先从好友数据库中取出好友list存入内存中
		if (BmobUserManager.getInstance(getApplicationContext())
				.getCurrentUser() != null) {
			// 获取本地好友user list到内存,方便以后获取好友list
			contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList());
		}
		// BmobIM SDK初始化--只需要这一段代码即可完成初始化
		// 请到Bmob官网(http://www.bmob.cn/)申请ApplicationId,具体地址:http://docs.bmob.cn/android/faststart/index.html?menukey=fast_start&key=start_android
		BmobChat.getInstance(this).init(CommonConstants.APP_ID);
	}
	
	// 初始化图片处理
	private void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.defaultDisplayImageOptions(ImageOptHelper.getImgOptions())
//				.imageDownloader(new HttpClientImageDownloader(
//						context, new DefaultHttpClient(manager, params)))
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 初始化百度相关sdk initBaidumap
	 * 
	 * @Title: initBaidumap
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initBaidu() {
		// 初始化地图Sdk
		SDKInitializer.initialize(this);
		// 初始化定位sdk
		initBaiduLocClient();
	}

	/**
	 * 初始化百度定位sdk
	 * 
	 * @Title: initBaiduLocClient
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initBaiduLocClient() {
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			double latitude = location.getLatitude();
			double longtitude = location.getLongitude();
			if (lastPoint != null) {
				if (lastPoint.getLatitude() == location.getLatitude()
						&& lastPoint.getLongitude() == location.getLongitude()) {
					// BmobLog.i("两次获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
					mLocationClient.stop();
					return;
				}
			}
			lastPoint = new BmobGeoPoint(longtitude, latitude);
		}
	}

	// 单例模式，才能及时返回数据
	SharePreferenceUtil mSpUtil;
	public static final String PREFERENCE_NAME = "_sharedinfo";

	public synchronized SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null) {
			String currentId = BmobUserManager.getInstance(
					getApplicationContext()).getCurrentUserObjectId();
			String sharedName = currentId + PREFERENCE_NAME;
			mSpUtil = new SharePreferenceUtil(this, sharedName);
		}
		return mSpUtil;
	}

	NotificationManager mNotificationManager;

	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	MediaPlayer mMediaPlayer;

	public synchronized MediaPlayer getMediaPlayer() {
		if (mMediaPlayer == null)
			mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		return mMediaPlayer;
	}

	public final String PREF_LONGTITUDE = "longtitude";// 经度
	private String longtitude = "";

	/**
	 * 获取经度
	 * 
	 * @return
	 */
	public String getLongtitude() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		longtitude = preferences.getString(PREF_LONGTITUDE, "");
		return longtitude;
	}

	/**
	 * 设置经度
	 * 
	 * @param pwd
	 */
	public void setLongtitude(String lon) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_LONGTITUDE, lon).commit()) {
			longtitude = lon;
		}
	}

	public final String PREF_LATITUDE = "latitude";// 经度
	private String latitude = "";

	/**
	 * 获取纬度
	 * 
	 * @return
	 */
	public String getLatitude() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		latitude = preferences.getString(PREF_LATITUDE, "");
		return latitude;
	}

	/**
	 * 设置维度
	 * 
	 * @param pwd
	 */
	public void setLatitude(String lat) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_LATITUDE, lat).commit()) {
			latitude = lat;
		}
	}

	private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();

	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, BmobChatUser> getContactList() {
		return contactList;
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, BmobChatUser> contactList) {
		if (this.contactList != null) {
			this.contactList.clear();
		}
		this.contactList = contactList;
	}

	/**
	 * 退出登录,清空缓存数据
	 */
	public void logout() {
		BmobUserManager.getInstance(getApplicationContext()).logout();
		setContactList(null);
		setLatitude(null);
		setLongtitude(null);
	}
}
