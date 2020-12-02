package com.example.myapplication5;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;


import java.util.Map;

/**
 *  重要说明：
 *  
 *  本 Demo 只是为了方便直接向商户展示支付宝的整个支付流程，所以将加签过程直接放在客户端完成
 *  （包括 OrderInfoUtil2_0_HK 和 OrderInfoUtil2_0）。
 *
 *  在真实 App 中，私钥（如 RSA_PRIVATE 等）数据严禁放在客户端，同时加签过程务必要放在服务端完成，
 *  否则可能造成商户私密数据泄露或被盗用，造成不必要的资金损失，面临各种安全风险。
 *
 *  Warning:
 *
 *  For demonstration purpose, the assembling and signing of the request parameters are done on
 *  the client side in this demo application.
 *
 *  However, in practice, both assembling and signing must be carried out on the server side.
 */
public class PayDemoActivity extends AppCompatActivity {

	/**
	 * 用于支付宝支付业务的入参 app_id。
	 */
	public static final String APPID = "2016110200785729";

	/**
	 * 用于支付宝账户登录授权业务的入参 pid。
	 */
	public static final String PID = "2088102181631744";

	/**
	 * 用于支付宝账户登录授权业务的入参 target_id。
	 */
	public static final String TARGET_ID = "com.alipay.sdk.pay";

	/**
	 *  pkcs8 格式的商户私钥。
	 *
	 * 	如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
	 * 	使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
	 * 	RSA2_PRIVATE。
	 *
	 * 	建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
	 * 	工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
	 */
	public static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCKqwmJy1mltdPElGRltks6eEx3E9uSC7NON18Sk9PP7wSSAVbmqHAKDeS+49AspezFBjUqW6G8MAvCLh5pRJcUyDt/JB1c2Ab58tuAAZ8kCUuPFBt9TZzXgIcnlwlKSdLLiagSaDrW9heCsfRpp7JyrAfkfyqOAW642O5m9d+JewxGex5H6SBqMhGUGhx/eKkBfOOq8TcGgYiAnyxv1V3aXunceHmAyMaEw/p66cJRsk0ADGs0JnHqCJrvqaOD7IN4K8JfCU+3lHwppMNfJCbvAKLskhC5S2CzCAtDACb9978eVmDCto6NRFNtNFw76F6SAEiuf0KUpfmgNGSDWsLXAgMBAAECggEAHMnhkw/6rqKPMpK0PUGZYKw1A8vFnA59zVFFla/HG7Y9tqo8hmqVyCCWSuM1Bu/ztfvR8ddQPqei6U911Nj+nLvtTjoLNolK+X1oAK1Vgr/DRhMgmeURGNSAOsHqlde5wbP0hs3I2XQB8YpMedrs+02n3dJg3VaCzDGNXSbSn9Hzrfnro6rHIKOIG/8kazUa40qTp6IdjHXRK9a2PDVUu0OM4rjgb1E4Q+mkXHG1wK8AS9SYNLeZrG0gOdi+WWcTPa5w4mz7CYx4EjGQJDbhxPR8F4vDgwfEDzk2dHIq+fk7e1+lDl/ST0P5o/M+faRyPL09Jp9pcEsa+IiL0k4MYQKBgQDExmw4AgZxn3nk+G1ALJ+885JXw50NH3dGHpuAKxErOqHJNGCQLb0fZsfFTiJsw9RPWAeBsxXE6D71nGaN/asZKf4zr6+q95CrUb1N5Ca6YTG5pnslkLL8UdmRNnV0OKwPtpoe4HJWAkaWtbU3FT4OjKF+/ka5aIQF8wMrMO0njwKBgQC0Z3SVkDxEFTcgFgajm6I0otR6LPywMdyB8nrY4YsE95AeZMborlfwBg3QSTnXCH2Zzpx8pvZwpbDfaIrqvGEDQkrvpHCbtXM1h67Rmp4ugR87iIwxXLB+I0sLWLtT5tIJQV5mHyLUbOtH/Q8JIyXkrTZDDJfQo1+RF26hUQnMOQKBgQCSaHh4q7kTrW7KmLTg/NLVif0m49rkurbKK1fT4zdhDLz3scrvO7jttlGJUnt2pbZAWuUq8Y6O9aZypK4Bk+5MSNxkpKF1+cFgVu8dF1ZhcpPG6EHUT3d9GYFh9D0r/ka3YkwGEUXBDOxskkKE+38y4BwBGzyQE12393oyFrM9rQKBgEIAa6XgfDwIav+hL1KiOQj63bPJS7WGuH8OYKWCduMdU6vbAO7WAjQ9csZWVAP5BkLEVXpBd34lEH3b+J8CxpdzpIjiZ5SAISNffbUP1Xl8IhoczfWtTKEJdoYzM23xz7w1Hz1LfOms47OVwO993Xo5aNXFALIDY45ovT/lryj5AoGARc6bM7v/+DPEFq9Woj2ZvlKVuLFfCC3fCSUPPCXFqw6OCxlGL4x0U+XQ9IBvFvpe8L7fjieYTJVdsNPSVVSyIAf882BoHdq0Wdai3eBu4vnx1cq9oQ08AANs6LsdpGaIpkbYn6Bj8mnddvlL8N8sxLfuVz/gn3u7m8pZfIMH+Dg=";
    public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKGPMVtqoZ3TCsT1BPEZYWrhaNOd89wWFdlweDQS6fSl8VQhUVtHNrDdL27/QWrm+A08YYXHVI/aFGDO4nCLVHrqSBjYFVKv1ozl7shitLmAg8dmwewJHcJuQ8LO7MFwe0mXCZsectk2yYYNFLmGWF0kln7rbaB+1cqF4S4u17QDAgMBAAECgYALAcd6E/S43PUB4DOa/YCumHbc3AkOOI76hngaDCPWYCvl8HMrhdmLCTa/GDLrxpqlxDRcuezf9BqpUc8JneR+c5ntNbTsOWVtXHoQf+zIPnTbF+dgIvB+HRwBv2PO6YMlAQSo8c5vxRSiN5uihIinQzeOnZChm6jJd70aJvtYUQJBAM/ljp7pkP5y7apbWdylz1F2RpMrGE9EMVxgHeOf0GqQcbGiuGxbvOyYsKJuepwWCuZi/cpHnnL1gRQMiXk9nqsCQQDG8OnDoH6JGk3+qXMcbmzHYrqE5h5qSldbRVnYWSLxgL6LdnuUtoaPFYZz44eMcxFKQHjAaRRvKErKYVlsh2AJAkAFb+mE+nLSVMsmc3EsNiHv7Xn3C199YzkvQ0xE0b8vqktu6+SK4PNV9MBZ3y3RuznZwKkGi0z3kLgpgBJwW041AkBNaN69JU03Ugn5Rrwo2vru1obXQaeiGk1FkYW1PnHvYPZD1BWgNynCsVCA9Y7/4qJerxmNXRX7bsUzXI/sP/zpAkB+XXA42hHaJcJT76U6NnzzRzNRacY++J2NOsg+GAOC+/JuhJbXN0QT0S9tDsougu3I/YYXoR0QeS5J10YwPZP7";
	
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				@SuppressWarnings("unchecked")
                PayResult payResult = new PayResult((Map<String, String>) msg.obj);
				/**
				 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					showAlert(PayDemoActivity.this, getString(R.string.pay_success) + payResult);
				} else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					showAlert(PayDemoActivity.this, getString(R.string.pay_failed) + payResult);
				}
				break;
			}
			case SDK_AUTH_FLAG: {
				@SuppressWarnings("unchecked")
				AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
				String resultStatus = authResult.getResultStatus();

				// 判断resultStatus 为“9000”且result_code
				// 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
				if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
					// 获取alipay_open_id，调支付时作为参数extern_token 的value
					// 传入，则支付账户为该授权账户
					showAlert(PayDemoActivity.this, getString(R.string.auth_success) + authResult);
				} else {
					// 其他状态值则为授权失败
					showAlert(PayDemoActivity.this, getString(R.string.auth_failed) + authResult);
				}
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);
	}

	/**
	 * 支付宝支付业务示例
	 */
	public void payV2(View v) {
		if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
			showAlert(this, getString(R.string.error_missing_appid_rsa_private));
			return;
		}
	
		/*
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险； 
		 * 
		 * orderInfo 的获取必须来自服务端；
		 */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
		Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2,"15");
		String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
		final String orderInfo = orderParam + "&" + sign;
		
		final Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(PayDemoActivity.this);
				Map<String, String> result = alipay.payV2(orderInfo, true);
				Log.i("msp", result.toString());
				
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * 支付宝账户授权业务示例
	 */
	public void authV2(View v) {
		if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID)
				|| (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
				|| TextUtils.isEmpty(TARGET_ID)) {
			showAlert(this, getString(R.string.error_auth_missing_partner_appid_rsa_private_target_id));
			return;
		}

		/*
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险； 
		 * 
		 * authInfo 的获取必须来自服务端；
		 */
		boolean rsa2 = (RSA2_PRIVATE.length() > 0);
		Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
		String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
		
		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
		final String authInfo = info + "&" + sign;
		Runnable authRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造AuthTask 对象
				AuthTask authTask = new AuthTask(PayDemoActivity.this);
				// 调用授权接口，获取授权结果
				Map<String, String> result = authTask.authV2(authInfo, true);

				Message msg = new Message();
				msg.what = SDK_AUTH_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread authThread = new Thread(authRunnable);
		authThread.start();
	}
	

	private static void showAlert(Context ctx, String info) {
		showAlert(ctx, info, null);
	}

	private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
		new AlertDialog.Builder(ctx)
				.setMessage(info)
				.setPositiveButton(R.string.confirm, null)
				.setOnDismissListener(onDismiss)
				.show();
	}

	private static void showToast(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}

	private static String bundleToString(Bundle bundle) {
		if (bundle == null) {
			return "null";
		}
		final StringBuilder sb = new StringBuilder();
		for (String key: bundle.keySet()) {
			sb.append(key).append("=>").append(bundle.get(key)).append("\n");
		}
		return sb.toString();
	}
}
