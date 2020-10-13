package com.qualcomm.qti.gaiacontrol.services;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import androidx.annotation.NonNull;
import org.uplus.twspopup.utils.SplitStringUtils;
import com.qualcomm.qti.gaiacontrol.Consts;
import com.qualcomm.qti.gaiacontrol.Utils;
import com.qualcomm.qti.gaiacontrol.receivers.UUIDReceiver;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;

abstract class BREDRProvider implements UUIDReceiver.UUIDListener {
    private static final long UNREGISTER_UUID_RECEIVER_DELAY_MS = 5000;
    private final String TAG = "BREDRProvider";
    /* access modifiers changed from: private */
    public boolean isWaitingForUUIDs = false;
    /* access modifiers changed from: private */
    public final BluetoothAdapter mBluetoothAdapter;
    /* access modifiers changed from: private */
    public CommunicationThread mCommunicationThread = null;
    /* access modifiers changed from: private */
    public ConnectionThread mConnectionThread = null;
    /* access modifiers changed from: private */
    public BluetoothDevice mDevice = null;
    private Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public boolean mShowDebugLogs = Consts.DEBUG;
    /* access modifiers changed from: private */
    public int mState = 4;
    /* access modifiers changed from: private */
    public UUID mUUIDTransport;

    @Retention(RetentionPolicy.SOURCE)
    @interface Errors {
        public static final int CONNECTION_FAILED = 0;
        public static final int CONNECTION_LOST = 1;
    }

    @Retention(RetentionPolicy.SOURCE)
    @interface State {
        public static final int CONNECTED = 2;
        public static final int CONNECTING = 1;
        public static final int DISCONNECTED = 0;
        public static final int DISCONNECTING = 3;
        public static final int NO_STATE = 4;
    }

    private static String getConnectionStateName(int i) {
        switch (i) {
            case 0:
                return "DISCONNECTED";
            case 1:
                return "CONNECTING";
            case 2:
                return "CONNECTED";
            case 3:
                return "DISCONNECTING";
            case 4:
                return "NO STATE";
            default:
                return "UNKNOWN";
        }
    }

    /* access modifiers changed from: package-private */
    public abstract void onCommunicationRunning();

    /* access modifiers changed from: package-private */
    public abstract void onConnectionError(int i);

    /* access modifiers changed from: package-private */
    public abstract void onConnectionStateChanged(int i);

    /* access modifiers changed from: package-private */
    public abstract void onDataFound(byte[] bArr);

    private static class UUIDs {
        /* access modifiers changed from: private */
        public static final UUID GAIA = UUID.fromString("00001107-D102-11E1-9B23-00025B00A5A5");
        /* access modifiers changed from: private */
        public static final UUID SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        private UUIDs() {
        }
    }

    BREDRProvider(BluetoothManager bluetoothManager) {
        if (this.mShowDebugLogs) {
            Log.d("BREDRProvider", "Creation of a new instance of BREDRProvider: " + this);
        }
        if (bluetoothManager == null) {
            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Log.i("BREDRProvider", "No available BluetoothManager, BluetoothAdapter initialised with BluetoothAdapter.getDefaultAdapter.");
        } else {
            this.mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        if (this.mBluetoothAdapter == null) {
            Log.e("BREDRProvider", "Initialisation of the Bluetooth Adapter failed: unable to initialize BluetoothAdapter.");
        }
    }

    public void onUUIDFound(BluetoothDevice bluetoothDevice, ParcelUuid[] parcelUuidArr) {
        if (this.isWaitingForUUIDs && parcelUuidArr != null && parcelUuidArr.length > 0 && bluetoothDevice != null) {
            UUID uUIDTransport = getUUIDTransport(parcelUuidArr);
            if (uUIDTransport == null) {
                Log.w("BREDRProvider", "UUIDs found but nothing to match");
                return;
            }
            this.isWaitingForUUIDs = false;
            connect(bluetoothDevice, uUIDTransport);
        }
    }

    /* access modifiers changed from: package-private */
    public void showDebugLogs(boolean z) {
        this.mShowDebugLogs = z;
        StringBuilder sb = new StringBuilder();
        sb.append("Debug logs are now ");
        sb.append(z ? "activated" : "deactivated");
        sb.append(SplitStringUtils.SPLIT_FILE_SUFFIX);
        Log.i("BREDRProvider", sb.toString());
    }

    /* access modifiers changed from: package-private */
    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    /* access modifiers changed from: package-private */
    public boolean connect(String str, @NonNull Context context) {
        if (this.mShowDebugLogs) {
            Log.d("BREDRProvider", "Request received to connect to a device with address " + str);
        }
        if (str == null) {
            Log.w("BREDRProvider", "connection failed: Bluetooth address is null.");
            return false;
        } else if (str.length() == 0) {
            Log.w("BREDRProvider", "connection failed: Bluetooth address null or empty.");
            return false;
        } else if (!isBluetoothAvailable()) {
            Log.w("BREDRProvider", "connection failed: unable to get the adapter to get the device object from BT address.");
            return false;
        } else if (!BluetoothAdapter.checkBluetoothAddress(str)) {
            Log.w("BREDRProvider", "connection failed: unknown BT address.");
            return false;
        } else {
            BluetoothDevice remoteDevice = this.mBluetoothAdapter.getRemoteDevice(str);
            if (remoteDevice != null) {
                return connect(remoteDevice, context);
            }
            Log.w("BREDRProvider", "connection failed: get device from BT address failed.");
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean reconnectToDevice(@NonNull Context context) {
        BluetoothDevice bluetoothDevice = this.mDevice;
        return bluetoothDevice != null && connect(bluetoothDevice, context);
    }

    /* access modifiers changed from: package-private */
    public boolean disconnect() {
        if (this.mShowDebugLogs) {
            StringBuilder sb = new StringBuilder();
            sb.append("Receives request to disconnect from device ");
            BluetoothDevice bluetoothDevice = this.mDevice;
            sb.append(bluetoothDevice != null ? bluetoothDevice.getAddress() : "null");
            Log.d("BREDRProvider", sb.toString());
        }
        if (this.mState == 0) {
            Log.w("BREDRProvider", "disconnection failed: no device connected.");
            return false;
        }
        setState(3);
        cancelConnectionThread();
        cancelCommunicationThread();
        setState(0);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Provider disconnected from BluetoothDevice ");
        BluetoothDevice bluetoothDevice2 = this.mDevice;
        sb2.append(bluetoothDevice2 != null ? bluetoothDevice2.getAddress() : "null");
        Log.i("BREDRProvider", sb2.toString());
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean sendData(byte[] bArr) {
        if (this.mShowDebugLogs) {
            Log.d("BREDRProvider", "Request received for sending data to a device.");
        }
        synchronized (this) {
            if (this.mState != 2) {
                Log.w("BREDRProvider", "Attempt to send data failed: provider not currently connected to a device.");
                return false;
            } else if (this.mCommunicationThread == null) {
                Log.w("BREDRProvider", "Attempt to send data failed: CommunicationThread is null.");
                return false;
            } else {
                CommunicationThread communicationThread = this.mCommunicationThread;
                return communicationThread.sendStream(bArr);
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0005, code lost:
        r0 = r2.mCommunicationThread;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isGaiaReady() {
        /*
            r2 = this;
            int r0 = r2.mState
            r1 = 2
            if (r0 != r1) goto L_0x0011
            com.qualcomm.qti.gaiacontrol.services.BREDRProvider$CommunicationThread r0 = r2.mCommunicationThread
            if (r0 == 0) goto L_0x0011
            boolean r0 = r0.mmIsRunning
            if (r0 == 0) goto L_0x0011
            r0 = 1
            goto L_0x0012
        L_0x0011:
            r0 = 0
        L_0x0012:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.qti.gaiacontrol.services.BREDRProvider.isGaiaReady():boolean");
    }

    /* access modifiers changed from: package-private */
    public synchronized int getState() {
        return this.mState;
    }

    /* access modifiers changed from: private */
    public synchronized void setState(int i) {
        if (this.mShowDebugLogs) {
            Log.d("BREDRProvider", "Connection state changes from " + getConnectionStateName(this.mState) + " to " + getConnectionStateName(i));
        }
        this.mState = i;
        onConnectionStateChanged(i);
    }

    private UUID getUUIDTransport(ParcelUuid[] parcelUuidArr) {
        if (parcelUuidArr == null) {
            return null;
        }
        for (ParcelUuid uuid : parcelUuidArr) {
            UUID uuid2 = uuid.getUuid();
            if (checkUUID(uuid2)) {
                return uuid2;
            }
        }
        return null;
    }

    private boolean checkUUID(UUID uuid) {
        return uuid.equals(UUIDs.SPP) || uuid.equals(UUIDs.GAIA);
    }

    private void cancelConnectionThread() {
        ConnectionThread connectionThread = this.mConnectionThread;
        if (connectionThread != null) {
            connectionThread.cancel();
            this.mConnectionThread = null;
        }
    }

    private void cancelCommunicationThread() {
        CommunicationThread communicationThread = this.mCommunicationThread;
        if (communicationThread != null) {
            communicationThread.cancel();
            this.mCommunicationThread = null;
        }
    }

    private boolean connect(@NonNull BluetoothDevice bluetoothDevice, @NonNull Context context) {
        if (this.mShowDebugLogs) {
            Log.d("BREDRProvider", "Request received to connect to a BluetoothDevice " + bluetoothDevice.getAddress());
        }
        int i = this.mState;
        if (i == 2) {
            Log.w("BREDRProvider", "connection failed: a device is already connected");
            return false;
        } else if (i == 1) {
            Log.w("BREDRProvider", "connection failed: Provider is already connecting to a device with an active communication.");
            return false;
        } else {
            if (!(bluetoothDevice.getType() == 1 || bluetoothDevice.getType() == 3)) {
                Log.e("BREDRProvider", "connect() the device is not BR/EDR compatible, type is " + bluetoothDevice.getType());
            }
            if (!isBluetoothAvailable()) {
                Log.w("BREDRProvider", "connection failed: Bluetooth is not available.");
                return false;
            } else if (!BluetoothAdapter.checkBluetoothAddress(bluetoothDevice.getAddress())) {
                Log.w("BREDRProvider", "connection failed: device address not found in list of devices known by the system.");
                return false;
            } else {
                ParcelUuid[] uuids = bluetoothDevice.getUuids();
                if (uuids == null) {
                    Log.i("BREDRProvider", "No UUIDs found, starting fetch UUIDS with SDP procedure.");
                    fetchUuidsWithSdp(bluetoothDevice, context);
                    return true;
                }
                UUID uUIDTransport = getUUIDTransport(uuids);
                if (uUIDTransport == null && bluetoothDevice.getBondState() != 12) {
                    Log.i("BREDRProvider", "connection: device not bonded, no UUID available, attempt to connect using SPP.");
                    uUIDTransport = UUIDs.SPP;
                } else if (uUIDTransport == null) {
                    Log.w("BREDRProvider", "connection failed: device bonded and no compatible UUID available.");
                    return false;
                }
                return connect(bluetoothDevice, uUIDTransport);
            }
        }
    }

    private boolean connect(@NonNull BluetoothDevice bluetoothDevice, @NonNull UUID uuid) {
        if (this.mShowDebugLogs) {
            Log.d("BREDRProvider", "Request received to connect to a BluetoothDevice with UUID " + uuid.toString());
        }
        if (this.mState == 2 && this.mCommunicationThread != null) {
            Log.w("BREDRProvider", "connection failed: Provider is already connected to a device with an active communication.");
            return false;
        } else if (this.mState == 1) {
            Log.w("BREDRProvider", "connection failed: Provider is already connecting to a device with an active communication.");
            return false;
        } else {
            cancelConnectionThread();
            cancelCommunicationThread();
            setState(1);
            BluetoothSocket createSocket = createSocket(bluetoothDevice, uuid);
            if (createSocket == null) {
                Log.w("BREDRProvider", "connection failed: creation of a Bluetooth socket failed.");
                return false;
            }
            if (this.mShowDebugLogs) {
                Log.d("BREDRProvider", "Request connect to BluetoothDevice " + createSocket.getRemoteDevice().getAddress() + " over RFCOMM starts.");
            }
            this.mUUIDTransport = uuid;
            this.mDevice = bluetoothDevice;
            this.mConnectionThread = new ConnectionThread(createSocket);
            this.mConnectionThread.start();
            return true;
        }
    }

    private void fetchUuidsWithSdp(BluetoothDevice bluetoothDevice, final Context context) {
        this.isWaitingForUUIDs = true;
        final UUIDReceiver uUIDReceiver = new UUIDReceiver(this, bluetoothDevice);
        context.registerReceiver(uUIDReceiver, new IntentFilter("android.bluetooth.device.action.UUID"));
        bluetoothDevice.fetchUuidsWithSdp();
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                try {
                    context.unregisterReceiver(uUIDReceiver);
                } catch (Exception unused) {
                }
                if (BREDRProvider.this.isWaitingForUUIDs && BREDRProvider.this.getState() == 1) {
                    boolean unused2 = BREDRProvider.this.isWaitingForUUIDs = false;
                    Log.e("BREDRProvider", "Connection failed: no corresponding UUID found.");
                    BREDRProvider.this.setState(0);
                    BREDRProvider.this.onConnectionError(0);
                }
            }
        }, UNREGISTER_UUID_RECEIVER_DELAY_MS);
    }

    private boolean isBluetoothAvailable() {
        return this.mBluetoothAdapter != null;
    }

    @SuppressLint({"ObsoleteSdkInt"})
    private boolean btIsSecure() {
        return Build.VERSION.SDK_INT >= 10;
    }

    private BluetoothSocket createSocket(BluetoothDevice bluetoothDevice, UUID uuid) {
        if (this.mShowDebugLogs) {
            Log.d("BREDRProvider", "Creating Bluetooth socket for device " + bluetoothDevice.getAddress() + " using UUID " + uuid);
        }
        try {
            if (btIsSecure()) {
                return bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            }
            return bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.w("BREDRProvider", "Exception occurs while creating Bluetooth socket: " + e.toString());
            Log.i("BREDRProvider", "Attempting to invoke method to create Bluetooth Socket.");
            try {
                return (BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{Integer.TYPE}).invoke(bluetoothDevice, new Object[]{1});
            } catch (Exception unused) {
                Log.w("BREDRProvider", "Exception occurs while creating Bluetooth socket by invoking method: " + e.toString());
                return null;
            }
        }
    }

    /* access modifiers changed from: private */
    public void onConnectionFailed() {
        setState(0);
        onConnectionError(0);
    }

    /* access modifiers changed from: private */
    public void onConnectionLost() {
        setState(0);
        onConnectionError(1);
    }

    /* access modifiers changed from: private */
    public void onSocketConnected(BluetoothSocket bluetoothSocket) {
        Log.i("BREDRProvider", "Successful connection to device: " + getDevice().getAddress());
        if (this.mShowDebugLogs) {
            Log.d("BREDRProvider", "Initialisation of ongoing communication by creating and running a CommunicationThread.");
        }
        cancelConnectionThread();
        cancelCommunicationThread();
        setState(2);
        this.mCommunicationThread = new CommunicationThread(bluetoothSocket);
        this.mCommunicationThread.start();
    }

    private class ConnectionThread extends Thread {
        private final String THREAD_TAG;
        private final BluetoothSocket mmConnectorSocket;

        private ConnectionThread(@NonNull BluetoothSocket bluetoothSocket) {
            this.THREAD_TAG = "ConnectionThread";
            setName("ConnectionThread" + getId());
            this.mmConnectorSocket = bluetoothSocket;
        }

        public void run() {
            try {
                if (BREDRProvider.this.mShowDebugLogs) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Attempt to connect device over BR/EDR: ");
                    sb.append(BREDRProvider.this.mDevice.getAddress());
                    sb.append(" using ");
                    sb.append(BREDRProvider.this.mUUIDTransport.equals(UUIDs.SPP) ? "SPP" : "GAIA");
                    Log.d("ConnectionThread", sb.toString());
                }
                BREDRProvider.this.mBluetoothAdapter.cancelDiscovery();
                this.mmConnectorSocket.connect();
                BREDRProvider.this.onSocketConnected(this.mmConnectorSocket);
            } catch (IOException e) {
                Log.w("ConnectionThread", "Exception while connecting: " + e.toString());
                try {
                    this.mmConnectorSocket.close();
                } catch (IOException e2) {
                    Log.w("ConnectionThread", "Could not close the client socket", e2);
                }
                BREDRProvider.this.onConnectionFailed();
                ConnectionThread unused = BREDRProvider.this.mConnectionThread = null;
            }
        }

        /* access modifiers changed from: private */
        public void cancel() {
            interrupt();
        }
    }

    private class CommunicationThread extends Thread {
        private final String THREAD_TAG = "CommunicationThread";
        private final InputStream mmInputStream;
        /* access modifiers changed from: private */
        public boolean mmIsRunning = false;
        private final OutputStream mmOutputStream;
        private final BluetoothSocket mmSocket;

        CommunicationThread(@NonNull BluetoothSocket bluetoothSocket) {
            InputStream inputStream;
            setName("CommunicationThread" + getId());
            this.mmSocket = bluetoothSocket;
            OutputStream outputStream = null;
            try {
                inputStream = this.mmSocket.getInputStream();
                try {
                    outputStream = this.mmSocket.getOutputStream();
                } catch (IOException e) {
                    Log.e("CommunicationThread", "Error occurred when getting input and output streams", e);
                }
            } catch (IOException e2) {
                inputStream = null;
                Log.e("CommunicationThread", "Error occurred when getting input and output streams", e2);
            }
            this.mmInputStream = inputStream;
            this.mmOutputStream = outputStream;
        }

        public void run() {
            if (this.mmInputStream == null) {
                Log.w("CommunicationThread", "Run thread failed: InputStream is null.");
                BREDRProvider.this.disconnect();
            } else if (this.mmOutputStream == null) {
                Log.w("CommunicationThread", "Run thread failed: OutputStream is null.");
                BREDRProvider.this.disconnect();
            } else {
                BluetoothSocket bluetoothSocket = this.mmSocket;
                if (bluetoothSocket == null) {
                    Log.w("CommunicationThread", "Run thread failed: BluetoothSocket is null.");
                    BREDRProvider.this.disconnect();
                } else if (!bluetoothSocket.isConnected()) {
                    Log.w("CommunicationThread", "Run thread failed: BluetoothSocket is not connected.");
                    BREDRProvider.this.disconnect();
                } else {
                    listenStream();
                }
            }
        }

        private void listenStream() {
            byte[] bArr = new byte[1024];
            if (BREDRProvider.this.mShowDebugLogs) {
                Log.d("CommunicationThread", "Start to listen for incoming streams.");
            }
            this.mmIsRunning = true;
            BREDRProvider.this.onCommunicationRunning();
            while (BREDRProvider.this.mState == 2 && this.mmIsRunning) {
                try {
                    int read = this.mmInputStream.read(bArr);
                    if (read > 0) {
                        byte[] bArr2 = new byte[read];
                        System.arraycopy(bArr, 0, bArr2, 0, read);
                        if (BREDRProvider.this.mShowDebugLogs) {
                            Log.d("CommunicationThread", "Reception of data: " + Utils.getStringFromBytes(bArr2));
                        }
                        BREDRProvider.this.onDataFound(bArr2);
                    }
                } catch (IOException e) {
                    Log.e("CommunicationThread", "Reception of data failed: exception occurred while reading: " + e.toString());
                    this.mmIsRunning = false;
                    if (BREDRProvider.this.mState == 2) {
                        BREDRProvider.this.onConnectionLost();
                    }
                    CommunicationThread unused = BREDRProvider.this.mCommunicationThread = null;
                }
            }
            if (BREDRProvider.this.mShowDebugLogs) {
                Log.d("CommunicationThread", "Stop to listen for incoming streams.");
            }
        }

        /* access modifiers changed from: package-private */
        public boolean sendStream(byte[] bArr) {
            if (BREDRProvider.this.mShowDebugLogs) {
                Log.d("BREDRProvider", "Process sending of data to the device starts");
            }
            BluetoothSocket bluetoothSocket = this.mmSocket;
            if (bluetoothSocket == null) {
                Log.w("CommunicationThread", "Sending of data failed: BluetoothSocket is null.");
                return false;
            } else if (!bluetoothSocket.isConnected()) {
                Log.w("CommunicationThread", "Sending of data failed: BluetoothSocket is not connected.");
                return false;
            } else if (BREDRProvider.this.mState != 2) {
                Log.w("CommunicationThread", "Sending of data failed: Provider is not connected.");
                return false;
            } else {
                OutputStream outputStream = this.mmOutputStream;
                if (outputStream == null) {
                    Log.w("CommunicationThread", "Sending of data failed: OutputStream is null.");
                    return false;
                }
                try {
                    outputStream.write(bArr);
                    this.mmOutputStream.flush();
                    if (!BREDRProvider.this.mShowDebugLogs) {
                        return true;
                    }
                    Log.d("BREDRProvider", "Success sending of data.");
                    return true;
                } catch (IOException e) {
                    Log.w("CommunicationThread", "Sending of data failed: Exception occurred while writing data: " + e.toString());
                    return false;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void cancel() {
            if (BREDRProvider.this.mShowDebugLogs) {
                Log.d("BREDRProvider", "Thread is cancelled.");
            }
            this.mmIsRunning = false;
            try {
                this.mmSocket.close();
            } catch (IOException e) {
                Log.w("CommunicationThread", "Cancellation of the Thread: Close of BluetoothSocket failed: " + e.toString());
            }
        }
    }
}
