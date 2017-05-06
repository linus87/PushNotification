package com.linus.pushnotification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.Devices;
import javapns.notification.PayloadPerDevice;
import javapns.notification.PushNotificationPayload;
import javapns.notification.transmission.PushQueue;

import org.json.JSONException;

public class PushUtil {
	public static String certificatePath = "/Users/lyan2/DuoshoujiRemoteNotification.p12";
	public static String msgCertificatePassword  = "Duoshouji!";
	public static final int numberOfThreads = 8;
    public static Boolean production = false; //true：production false: sandbox
    
    /**
     * Push a simple alert to one or more devices.
     * @param msg message content
     * @param devices 
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushMsgNotification(String msg,Object devices) throws CommunicationException, KeystoreException{
        Push.alert(msg, certificatePath, msgCertificatePassword, production, devices);
    }
    
    /**
     * Push a simple badge number to one or more devices.
     * @param badge
     * @param devices
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushBadgeNotification(int badge,Object devices) throws CommunicationException, KeystoreException{
        Push.badge(badge, certificatePath, msgCertificatePassword, production, devices);
    }
	
	/**
     * Push a sound notification
     * @param sound 
     * @param devices 
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushSoundNotification(String sound,Object devices) throws CommunicationException, KeystoreException{
        Push.sound(sound, certificatePath, msgCertificatePassword, production, devices);
    }
    
    /**
     * Push a notification combining an alert, a badge and a sound. 
     * @param message
     * @param badge
     * @param sound
     * @param devices
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushCombinedNotification(String message,int badge,String sound,Object devices) throws CommunicationException, KeystoreException{
        Push.combined(message, badge, sound, certificatePath, msgCertificatePassword, production, devices);
    }
    
    /**
     * Push a content-available notification for Newsstand.
     * @param devices
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void contentAvailable(Object devices) throws CommunicationException, KeystoreException{
        Push.contentAvailable(certificatePath, msgCertificatePassword, production, devices);
    }
    
    /**
     * Push a special test notification with an alert message containing useful debugging information.
     * @param devices
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void test(Object devices) throws CommunicationException, KeystoreException{
        Push.test(certificatePath, msgCertificatePassword, production, devices);
    }
    
    /**
     * Push a preformatted payload to a list of devices.
     * @param devices
     * @param msg
     * @param badge
     * @param sound
     * @param map
     * @throws JSONException
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushPayload(List<Device> devices, String msg,Integer badge,String sound,Map<String,String> map) throws JSONException, CommunicationException, KeystoreException{
        PushNotificationPayload payload = customPayload(msg, badge, sound, map);
        Push.payload(payload, certificatePath, msgCertificatePassword, production, devices);
    }
    
    /**
     * Push a preformatted payload to a list of devices using multiple simulatenous threads (and connections).
     * @param devices
     * @param msg
     * @param badge
     * @param sound
     * @param map
     * @throws Exception
     */
    public static void pushPayLoadByThread(List<Device> devices, String msg,Integer badge,String sound,Map<String,String> map) throws Exception{
        PushNotificationPayload payload = customPayload(msg, badge, sound, map);
        Push.payload(payload, certificatePath, msgCertificatePassword, production, numberOfThreads, devices);
    }
    
    /**
     * Push a different preformatted payload for each device.
     * @param devices
     * @param msg
     * @param badge
     * @param sound
     * @param map
     * @throws JSONException
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushPayloadDevicePairs(List<Device> devices,String msg,Integer badge,String sound,Map<String,String> map) throws JSONException, CommunicationException, KeystoreException{
        List<PayloadPerDevice> payloadDevicePairs = new ArrayList<PayloadPerDevice>();
        PayloadPerDevice perDevice = null;
        for (int i = 0; i <devices.size(); i++) {
            perDevice = new PayloadPerDevice(customPayload(msg+"--->"+i, badge, sound, map), devices.get(i));
            payloadDevicePairs.add(perDevice);
        }
        Push.payloads(certificatePath, msgCertificatePassword, production, payloadDevicePairs);
    }
    /**
     * Push a different preformatted payload for each device using multiple simulatenous threads (and connections).
     * @param devices
     * @param msg
     * @param badge
     * @param sound
     * @param map
     * @throws Exception
     */
    public static void pushPayloadDevicePairsByThread(List<Device> devices,String msg,Integer badge,String sound,Map<String,String> map) throws Exception{
        List<PayloadPerDevice> payloadDevicePairs = new ArrayList<PayloadPerDevice>();
        PayloadPerDevice perDevice = null;
        for (int i = 0; i <devices.size(); i++) {
            perDevice = new PayloadPerDevice(customPayload(msg+"--->"+i, badge, sound, map), devices.get(i));
            payloadDevicePairs.add(perDevice);
        }
        Push.payloads(certificatePath, msgCertificatePassword, production,numberOfThreads, payloadDevicePairs);
    }
    /**
     * Build and start an asynchronous queue for sending notifications later without opening and closing connections.
	 * The returned queue is not started, meaning that underlying threads and connections are not initialized.
	 * The queue will start if you invoke its start() method or one of the add() methods.
	 * Once the queue is started, its underlying thread(s) and connection(s) will remain active until the program ends.
	 * 
     * @param devices
     * @param msg
     * @param badge
     * @param sound
     * @param map
     * @throws KeystoreException
     * @throws JSONException
     */
    public static void queue(List<Device> devices,String msg,Integer badge,String sound,Map<String,String> map) throws KeystoreException, JSONException{
        PushQueue queue = Push.queue(certificatePath, msgCertificatePassword, production, numberOfThreads);
        queue.start();
        PayloadPerDevice perDevice = null;
        for (int i = 0; i <devices.size(); i++) {
            perDevice = new PayloadPerDevice(customPayload(msg+"--->"+i, badge, sound, map), devices.get(i));
            queue.add(perDevice);
        }
    }
    /**
     * Set custom payload
     * @param msg
     * @param badge
     * @param sound
     * @param map 
     * @return
     * @throws JSONException
     */
    private static PushNotificationPayload customPayload(String msg,Integer badge,String sound,Map<String,String> map) throws JSONException{
        PushNotificationPayload payload = PushNotificationPayload.complex();
        if(msg != null && !msg.isEmpty()){
            payload.addAlert(msg);         
        }
        if(badge != null){         
            payload.addBadge(badge);
        }
        
        if (sound == null || sound.isEmpty()) {
        	payload.addSound("default");
        }
        
        if(map!=null && !map.isEmpty()){
            Object[] keys = map.keySet().toArray();    
            Object[] vals = map.values().toArray();
            if(keys!= null && vals != null && keys.length == vals.length){
                for (int i = 0; i < map.size(); i++) {                  
                    payload.addCustomDictionary(String.valueOf(keys[i]),String.valueOf(vals[i]));
                }
            }
        }
        return payload;
    }

	public static void main(String[] args) throws KeystoreException, JSONException {
        //pushMsgNotification("hello!!!2", true, "iostoken");
		//pushBadgeNotification(1,  "iostoken");
        String[] devs= new String[1];
        for (int i = 0; i < devs.length; i++) {
        	devs[i] = "a1c71c7f77203f7a8fae5db566df2513ad93b921edea8ed771e928e7b9ec7594";
        }
        List<Device> devices=Devices.asDevices(devs);
        System.out.println(devices.size());
        //pushPayLoadByThread(devices, "Hello 2222222", 1, null, null);
        //pushPayloadDevicePairs(devices, "Hello 111111111", 1, null, null);
        //pushPayloadDevicePairs(devices, "Hello +++", 1, null, null);
        queue(devices,"Hello 2222222", 1, null, null);
	}

}
