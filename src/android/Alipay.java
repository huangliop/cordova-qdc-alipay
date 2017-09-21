package com.qdc.plugins.alipay;

import com.alipay.sdk.app.PayTask;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * 支付宝支付插件
 * 
 * @author NCIT
 * 
 */
public class Alipay extends CordovaPlugin {
	/** JS回调接口对象 */
	public static CallbackContext cbContext = null;

	/** LOG TAG */
	private static final String LOG_TAG = Alipay.class.getSimpleName();

	/**
	 * 插件主入口
	 */
	@Override
	public boolean execute(String action, final JSONArray args,
			CallbackContext callbackContext) throws JSONException {
			LOG.d(LOG_TAG, "Alipay#execute");
			cbContext = callbackContext;
			PluginResult pluginResult = new PluginResult(
					PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(true);
			callbackContext.sendPluginResult(pluginResult);
			// 参数检查
			if (args.length() != 1) {
				LOG.e(LOG_TAG, "args is empty", new NullPointerException());
				PluginResult result = new PluginResult(
						PluginResult.Status.ERROR, "args is empty");
				result.setKeepCallback(true);
				cbContext.sendPluginResult(result);
				return false;
			}
			JSONObject jsonObj = args.getJSONObject(0);
			final String payInfo = jsonObj.getString("pay_info");
			if (payInfo == null || "".equals(payInfo)) {
				LOG.e(LOG_TAG, "pay_info is empty", new NullPointerException());
				PluginResult result = new PluginResult(
						PluginResult.Status.ERROR, "pay_info is empty");
				result.setKeepCallback(true);
				cbContext.sendPluginResult(result);
				return false;
			}
		if ("payment".equalsIgnoreCase(action)) {
			pay(payInfo,1);
			return true;
		}else if ("payV2".equalsIgnoreCase(action)){
			pay(payInfo,2);
		}

		return true;
	}
	private void pay(final String payInfo, final int version){
		LOG.d(LOG_TAG, "Alipay#payment.start");
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(cordova.getActivity());
				// 调用支付接口
				switch (version){
					//调用老版接口
					case 1:
						String resultMsg =alipay.pay(payInfo,true);
						LOG.i(LOG_TAG, ">>>>>>>>>>支付回调通知>>>>>>>>>>>");
						LOG.i(LOG_TAG, resultMsg);
						LOG.i(LOG_TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

						String[] resArr = resultMsg.split(";");
						JSONObject resJo = new JSONObject();
						for (String res : resArr) {
							String[] ress = res.split("=");
							String key = ress[0];
							String value = ress[1].substring(1,
									ress[1].length() - 1);
							try {
								resJo.put(key, value);
							} catch (JSONException e) {
								LOG.e(LOG_TAG, e.getMessage(), e);
							}
						}

						PluginResult result = new PluginResult(
								PluginResult.Status.OK, resJo.toString());
						result.setKeepCallback(true);
						cbContext.sendPluginResult(result);
						break;
					//调用新版接口
					case 2:
					default:
						Map<String,String> resultMap= alipay.payV2(payInfo,true);
						JSONObject resJo2 = new JSONObject();
						try{
							resJo2=new JSONObject(resultMap);
						}catch (Exception ex){
							ex.printStackTrace();
						}
						PluginResult result2 = new PluginResult(
								PluginResult.Status.OK, resJo2.toString());
						result2.setKeepCallback(true);
						cbContext.sendPluginResult(result2);
						break;
				}

			}
		}; 
		// 此处必须通过启动线程调起支付
		Thread payThread = new Thread(payRunnable);
		payThread.start();
		LOG.d(LOG_TAG, "Alipay#payment.end");
	}
}
