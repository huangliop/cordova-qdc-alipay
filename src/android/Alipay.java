package com.qdc.plugins.alipay;

import com.alipay.sdk.app.PayTask;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

		boolean ret = false;

		if ("payment".equalsIgnoreCase(action)) {
			LOG.d(LOG_TAG, "Alipay#payment.start");

			cbContext = callbackContext;

			PluginResult pluginResult = new PluginResult(
					PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(true);
			callbackContext.sendPluginResult(pluginResult);

			// 参数检查
			if (args.length() != 1) {
				LOG.e(LOG_TAG, "args is empty", new NullPointerException());
				ret = false;
				PluginResult result = new PluginResult(
						PluginResult.Status.ERROR, "args is empty");
				result.setKeepCallback(true);
				cbContext.sendPluginResult(result);
				return ret;
			}

			JSONObject jsonObj = args.getJSONObject(0);

			final String payInfo = jsonObj.getString("pay_info");
			if (payInfo == null || "".equals(payInfo)) {
				LOG.e(LOG_TAG, "pay_info is empty", new NullPointerException());
				ret = false;
				PluginResult result = new PluginResult(
						PluginResult.Status.ERROR, "pay_info is empty");
				result.setKeepCallback(true);
				cbContext.sendPluginResult(result);
				return ret;
			}

			Runnable payRunnable = new Runnable() {

				@Override
				public void run() {
					// 构造PayTask 对象
					PayTask alipay = new PayTask(cordova.getActivity());
					// 调用支付接口
					String resultMsg = alipay.pay(payInfo,true);
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
				}
			};

			// 此处必须通过启动线程调起支付
			Thread payThread = new Thread(payRunnable);
			payThread.start();

			LOG.d(LOG_TAG, "Alipay#payment.end");
			return true;
		}

		return true;
	}
}
