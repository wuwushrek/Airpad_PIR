package com.example.airpad_pir;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class BluetoothHandle {
	
	private final static long DELAY_ENVOI = 100;
	private final static char DELIMITER = '\n';
	
	private BluetoothAdapter myBluetooth = null;
	private BluetoothSocket btSocket = null;
	private InputStream inSocket;
	private Set<BluetoothDevice> pairedDevices;
	private boolean isBTConnect = false;
	private Context mContext;
	public static String EXTRA_ADDRESS = "ZMR";
	static final UUID myUUID = UUID
			.fromString("00001101-0000-1000-8000-00805f9b34fb");

	private final int POTARD_MIN = 180;
	private final int POTARD_MAX = 830;
	private final int ACC_MAX = 10;
	
	private volatile boolean isSendingPosition = false;
	public volatile boolean isUsingSensor = false;

	private Drone mDrone;
	private SendData send = new SendData();
	private ReceiveData receive = new ReceiveData();
	private TransfertSensor transfertSensor = new TransfertSensor();
	public double angleToSendAccelerometer;

	public BluetoothHandle(Context context, Drone drone) {
		mContext = context;
		mDrone = drone;
		myBluetooth = BluetoothAdapter.getDefaultAdapter();
		if (myBluetooth == null) {
			Toast.makeText(mContext, "BLUETOOTH INDISPONIBLE",
					Toast.LENGTH_LONG).show();
		} else if (!myBluetooth.isEnabled()) {
			Intent turnBOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			((Activity) mContext).startActivityForResult(turnBOn, 1);
		}
		angleToSendAccelerometer = mDrone.getDesireAngle();
	}

	@SuppressLint("NewApi")
	BluetoothDevice pairedDevicesList() {
		pairedDevices = myBluetooth.getBondedDevices();
		if (pairedDevices.size() <= 0) {
			Toast.makeText(mContext, "No Paired Bluetooth Devices Found.",
					Toast.LENGTH_LONG).show();
		}
		for (BluetoothDevice bt : pairedDevices) {
			System.out.println("TAG: "+ bt.getName() + "    |    " + bt.getAddress()+"    |    " + bt.getType());
			if(bt.getName().equals(EXTRA_ADDRESS)){
				return bt;
			}
		}
		return null;
	}

	void disconnect() {
		if (btSocket != null) {
			try {
				btSocket.close();
			} catch (IOException e) {
				Toast.makeText(mContext, "Error !!!!!", Toast.LENGTH_LONG)
						.show();
			}
		}
		isBTConnect = false;
	}

	private double potardToAndroid(int potardAngle) {
		return (((potardAngle - POTARD_MIN) * (2 * Drone.MAX_ANGLE) / ((double) ((POTARD_MAX - POTARD_MIN)))) - Drone.MAX_ANGLE);
	}

	private int androidToPotard(double angle) {
		return (int) (((angle + Drone.MAX_ANGLE) * (POTARD_MAX - POTARD_MIN) / (2 * Drone.MAX_ANGLE)) + POTARD_MIN);
	}

	public void sendConsigne() throws IOException {
		if (btSocket != null) {
			int valueToSend = androidToPotard(mDrone.getDesireAngle());
			String to =String.valueOf(valueToSend)+'\n';
			btSocket.getOutputStream().write(to.getBytes());
			System.out.println("SEND VALUE: "+to);
		}
	}
	
	public void changeAccelConsigne(double value){
		angleToSendAccelerometer= (((value + ACC_MAX) * (POTARD_MAX-POTARD_MIN) / (2*ACC_MAX)) +POTARD_MIN);
	}
	
	public void getPosition() throws IOException {
		if (btSocket != null) {
			byte ch,buffer[] = new byte[1024];
			int i=0;
			while((ch=(byte) inSocket.read())!=DELIMITER){
				buffer[i++]=ch;
			}
			buffer[i]='\0';
			String msg =  new String(buffer);
			final String response = msg.trim();
			try{
				int value = Integer.parseInt(response);
				mDrone.setCurrentAngle(potardToAndroid(value));
				mDrone.setPosition();
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
			System.out.println("RECEIVED: "+response);
		}
	}

	public void connect() {
		new ConnectBT().execute();
	}
	
	public boolean isConnect(){
		return isBTConnect || (btSocket!=null && btSocket.isConnected());
	}
	
	public void launchAcquisition(){
		new Thread(receive).start();
	}
	
	public void startSendingPosition(){
		if(!isBTConnect)
			return ;
		isSendingPosition = true;
		new Thread(send).start();
	}
	
	public void stopSendingPosition(){
		isSendingPosition = false;
	}
	
	public void launchAccelerometerTransfert(){
		new Thread(transfertSensor).start();
	}
	public void setUsingSensor(boolean value){
		isUsingSensor = value;
	}
	
	class SendData implements Runnable{
		@Override
		public void run() {
			while(isBTConnect && isSendingPosition){
				try {
					sendConsigne();
					Thread.sleep(DELAY_ENVOI);
				} catch (IOException e) {
					e.printStackTrace();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	class ReceiveData implements Runnable {
		@Override
		public void run(){
			while(isBTConnect){
				try {
					getPosition();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	class TransfertSensor implements Runnable{
		@Override
		public void run(){
			while (isUsingSensor && isBTConnect){
				try {
					String to =String.valueOf((int) angleToSendAccelerometer)+'\n';
					btSocket.getOutputStream().write(to.getBytes());
					Thread.sleep(DELAY_ENVOI);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class ConnectBT extends AsyncTask<Void, Void, Void> {
		private boolean connectSuccess = true;

		@Override
		protected void onPreExecute() {
			//JoystickActivity.progress = ProgressDialog.show(mContext,
				//	"Connecting....", "Please wait");
		}

		@Override
		protected Void doInBackground(Void... devices) {
			try {
				if (btSocket == null || !isBTConnect) {
					myBluetooth = BluetoothAdapter.getDefaultAdapter();
					BluetoothDevice dispositivo = pairedDevicesList();
					btSocket= dispositivo.createRfcommSocketToServiceRecord(myUUID);
					btSocket.connect();
					inSocket = btSocket.getInputStream();
					myBluetooth.cancelDiscovery();
				}
			} catch (IOException e){//IOException e) {
				connectSuccess = false;
				isBTConnect=false;
				e.printStackTrace();
			} catch (NullPointerException e){
				connectSuccess=false;
				isBTConnect = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (!connectSuccess) {
				Toast.makeText(mContext,
						"Connection Failed. Is it a SPP Bluetooth? Try again.",
						Toast.LENGTH_LONG).show();
				isBTConnect = false;
			} else {
				Toast.makeText(mContext, "Connection Reussie !",
						Toast.LENGTH_LONG).show();
				isBTConnect = true;
				launchAcquisition();
				if(isUsingSensor){
					launchAccelerometerTransfert();
				}
			}
			//JoystickActivity.progress.dismiss();
		}
	}
}
