package com.linus.pushnotification;

import java.util.ArrayList;
import java.util.List;

import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

import org.json.JSONException;

public class PushClient {
	private static String defaultSound = "default";
	private PushNotificationManager pushManager;
	private String msgCertificatePassword;
	private String certificatePath;
	private List<PushedNotification> notifications = new ArrayList<PushedNotification>();
	
	public PushClient(String certificatePath, String msgCertificatePassword) throws CommunicationException, KeystoreException {
		pushManager = new PushNotificationManager();
		
		// true：表示的是产品测试推送服务 false：表示的是产品发布推送服务
		pushManager.initializeConnection(new AppleNotificationServerBasicImpl(
			certificatePath, msgCertificatePassword, true));
	}

	/**
	 * Create a simple push notification payload.
	 * 
	 * @param simpleMsg
	 * @param badge
	 * @param sound
	 * @return
	 * @throws JSONException
	 */
	public static PushNotificationPayload createSimplePayload(String simpleMsg,
			Integer badge, String sound) throws JSONException {
		PushNotificationPayload payload = new PushNotificationPayload();
		payload.addAlert(simpleMsg);

		if (badge != null) {
			payload.addBadge(badge);
		}

		if (null == sound || sound.isEmpty()) {
			payload.addSound(defaultSound);
		}

		return payload;
	}
	
	/**
	 * Send a payload to a single device.
	 * @param deviceToken
	 * @param payload
	 * @throws CommunicationException
	 */
	public void sendPayloadToSingleDevice(String deviceToken, PushNotificationPayload payload) throws CommunicationException {
		Device device = new BasicDevice();
		device.setToken(deviceToken);
		PushedNotification notification = pushManager.sendNotification(
				device, payload, true);
		notifications.add(notification);
	}
	
	/**
	 * Stop connection to APNS.
	 * @throws CommunicationException
	 * @throws KeystoreException
	 */
	public void stopConnection() throws CommunicationException, KeystoreException {
		pushManager.stopConnection();
	}
	
	/**
	 * Clear all notfications.
	 */
	public void clearNotifications() {
		notifications.clear();
	}
	
	/**
	 * Send a payload to a single device.
	 * @param deviceToken
	 * @param payload
	 * @throws KeystoreException 
	 * @throws CommunicationException
	 */
	public void sendPayloadToMultipleDevices(List<Device> devices, PushNotificationPayload payload) throws CommunicationException, KeystoreException  {
		notifications.addAll(pushManager.sendNotifications(payload, devices));
	}

	public static void main(String[] args) throws JSONException,
			CommunicationException, KeystoreException,
			InvalidDeviceTokenFormatException {
		
		// String certificatePath = requestRealPath
		// + "/WEB-INF/classes/certificate/msg.p12";
		// java必须要用导出p12文件 php的话是pem文件
		String certificatePath = "/Users/lyan2/DuoshoujiRemoteNotification.p12";
		String msgCertificatePassword = "Duoshouji!";// 导出证书时设置的密码
		PushClient manager = new PushClient(certificatePath, msgCertificatePassword);
		
		System.out.println("Linus==========开始推送消息");
		int badge = 1; // 图标小红圈的数值
		
		// //手机设备token号
		String deviceToken = "a1c71c7f77203f7a8fae5db566df2513ad93b921edea8ed771e928e7b9ec7594";
		String message = "test push message to ios device";

		List<String> tokens = new ArrayList<String>();
		tokens.add(deviceToken);
		
		boolean sendCount = true;

		PushNotificationPayload payload = createSimplePayload(message, badge, null);

		// 开始推送消息
		if (sendCount) {
			manager.sendPayloadToSingleDevice(deviceToken, payload);
		} else {
			List<Device> devices = new ArrayList<Device>();
			for (String token : tokens) {
				devices.add(new BasicDevice(token));
			}
			manager.sendPayloadToMultipleDevices(devices, payload);
		}

		List<PushedNotification> failedNotification = PushedNotification
			.findFailedNotifications(manager.notifications);
		List<PushedNotification> successfulNotification = PushedNotification
			.findSuccessfulNotifications(manager.notifications);
		int failed = failedNotification.size();
		int successful = successfulNotification.size();
		System.out.println("Linus==========成功数：" + successful);
		System.out.println("Linus==========失败数：" + failed);
		manager.stopConnection();
		System.out.println("Linus==========消息推送完毕");
	}

	public static String getDefaultSound() {
		return defaultSound;
	}

	public static void setDefaultSound(String sound) {
		defaultSound = sound;
	}

	public PushNotificationManager getPushManager() {
		return pushManager;
	}

	public void setPushManager(PushNotificationManager pushManager) {
		this.pushManager = pushManager;
	}

	public String getMsgCertificatePassword() {
		return msgCertificatePassword;
	}

	public void setMsgCertificatePassword(String msgCertificatePassword) {
		this.msgCertificatePassword = msgCertificatePassword;
	}

	public String getCertificatePath() {
		return certificatePath;
	}

	public void setCertificatePath(String certificatePath) {
		this.certificatePath = certificatePath;
	}

}
