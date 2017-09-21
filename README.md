# cordova-qdc-alipay
支付宝APP支付cordova,ionic插件(Android版，IOS版)

* 2016.05.16 升级到最新的SDK
* 2015.07.02 IOS版支付宝集成
* 2015.07.02 支付宝签名算法移至服务端
* 2015.07.01 支付宝Android集成，初步完成

# 1. Android客户端安装
开发工程下执行以下命令导入本插件：

	$ cordova plugin add https://github.com/huangliop/cordova-qdc-alipay.git  --variable IOS_URL=[自己定义一个Url，用于ios支付成功后回掉app，例如：'myappalipay']

已安装插件查看：

	$cordova plugin list


执行以下命令删本插件：

	# 【com.qdc.plugins.alipay】是插件ID，不是插件文件夹名
	$cordova plugin rm com.qdc.plugins.alipay

## 1.3 JS调用说明

* 事先前调用后台服务端API生成订单数据及签名数据
* 调用plugin的JS方法【alipay.payment】进行支付

```js
	alipay.payment(json, cb_success, cb_failure);
	# 参数说明：格式为JSON格式
	# cb_success:调用成功回调方法
	# cb_failure:调用失败回调方法
	 json: {
	    pay_info: 支付信息 
	  }
```

