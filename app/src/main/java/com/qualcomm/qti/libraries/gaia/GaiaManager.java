package com.qualcomm.qti.libraries.gaia;

import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;
import androidx.annotation.Nullable;
import org.uplus.twspopup.utils.SplitStringUtils;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacketBLE;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacketBREDR;
import com.qualcomm.qti.libraries.gaia.requests.GaiaAcknowledgementRequest;
import com.qualcomm.qti.libraries.gaia.requests.GaiaRequest;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class GaiaManager {
    protected static final int ACKNOWLEDGEMENT_RUNNABLE_DEFAULT_DELAY_MILLIS = 30000;
    private final String TAG = "GaiaManager";
    private final Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public boolean mShowDebugLogs = GaiaUtils.LOG_ENABLED;
    private int mTimeOutRequestDelay = ACKNOWLEDGEMENT_RUNNABLE_DEFAULT_DELAY_MILLIS;
    /* access modifiers changed from: private */
    public final ArrayMap<Integer, LinkedList<TimeOutRequestRunnable>> mTimeOutRequestRunnableMap = new ArrayMap<>();
    private final int mTransportType;

    /* access modifiers changed from: protected */
    public abstract void hasNotReceivedAcknowledgementPacket(GaiaPacket gaiaPacket);

    /* access modifiers changed from: protected */
    public abstract boolean manageReceivedPacket(GaiaPacket gaiaPacket);

    /* access modifiers changed from: protected */
    public abstract void onSendingFailed(GaiaPacket gaiaPacket);

    /* access modifiers changed from: protected */
    public abstract void receiveSuccessfulAcknowledgement(GaiaPacket gaiaPacket);

    /* access modifiers changed from: protected */
    public abstract void receiveUnsuccessfulAcknowledgement(GaiaPacket gaiaPacket);

    /* access modifiers changed from: protected */
    public abstract boolean sendGAIAPacket(byte[] bArr);

    protected GaiaManager(int i) {
        this.mTransportType = i;
    }

    public void reset() {
        if (this.mShowDebugLogs) {
            Log.d("GaiaManager", "Request received to reset the manager.");
        }
        resetTimeOutRequestRunnableMap();
    }

    public synchronized void setRequestTimeOut(int i) {
        if (this.mShowDebugLogs) {
            Log.d("GaiaManager", "Time out set up to " + i + ", previous time out was " + this.mTimeOutRequestDelay);
        }
        this.mTimeOutRequestDelay = i;
    }

    public int getTransportType() {
        return this.mTransportType;
    }

    /* access modifiers changed from: protected */
    public void showDebugLogs(boolean z) {
        this.mShowDebugLogs = z;
        StringBuilder sb = new StringBuilder();
        sb.append("Debug logs are now ");
        sb.append(z ? "activated" : "deactivated");
        sb.append(SplitStringUtils.SPLIT_FILE_SUFFIX);
        Log.i("GaiaManager", sb.toString());
    }

    private void sendGAIAAcknowledgement(GaiaPacket gaiaPacket, int i, @Nullable byte[] bArr) {
        if (this.mShowDebugLogs) {
            Log.d("GaiaManager", "Request to send acknowledgement for packet with command " + GaiaUtils.getHexadecimalStringFromInt(gaiaPacket.getCommand()));
        }
        if (gaiaPacket.isAcknowledgement()) {
            Log.w("GaiaManager", "Send of GAIA acknowledgement failed: packet is already an acknowledgement packet.");
            return;
        }
        try {
            sendGAIAPacket(gaiaPacket.getAcknowledgementPacketBytes(i, bArr));
        } catch (GaiaException e) {
            Log.w("GaiaManager", "ACK packet not created, exception occurred: " + e.toString());
        }
    }

    public void onReceiveGAIAPacket(byte[] bArr) {
        if (this.mShowDebugLogs) {
            Log.d("GaiaManager", "Received potential GAIA packet: " + GaiaUtils.getHexadecimalStringFromBytes(bArr));
        }
        try {
            GaiaPacket gaiaPacketBLE = this.mTransportType == 0 ? new GaiaPacketBLE(bArr) : new GaiaPacketBREDR(bArr);
            if (this.mShowDebugLogs) {
                Log.d("GaiaManager", "Manager could retrieve a packet from the given data with command: " + GaiaUtils.getGAIACommandToString(gaiaPacketBLE.getCommand()));
            }
            if (gaiaPacketBLE.isAcknowledgement()) {
                if (!cancelTimeOutRequestRunnable(gaiaPacketBLE.getCommand())) {
                    Log.w("GaiaManager", "Received unexpected acknowledgement packet for command " + GaiaUtils.getGAIACommandToString(gaiaPacketBLE.getCommand()));
                    return;
                }
                int status = gaiaPacketBLE.getStatus();
                if (this.mShowDebugLogs) {
                    Log.d("GaiaManager", "Received GAIA ACK packet for command " + GaiaUtils.getGAIACommandToString(gaiaPacketBLE.getCommand()) + " with status: " + GAIA.getStatusToString(status));
                }
                if (status == 0) {
                    receiveSuccessfulAcknowledgement(gaiaPacketBLE);
                } else {
                    receiveUnsuccessfulAcknowledgement(gaiaPacketBLE);
                }
            } else if (!manageReceivedPacket(gaiaPacketBLE)) {
                Log.i("GaiaManager", "Packet has not been managed by application, manager sends NOT_SUPPORTED acknowledgement, bytes: \n\t\t" + GaiaUtils.getGAIACommandToString(gaiaPacketBLE.getCommandId()));
                createAcknowledgmentRequest(gaiaPacketBLE, 1, (byte[]) null);
            }
        } catch (GaiaException unused) {
            Log.w("GaiaManager", "Impossible to retrieve packet from device: " + GaiaUtils.getHexadecimalStringFromBytes(bArr));
        }
    }

    /* access modifiers changed from: protected */
    public void createRequest(GaiaPacket gaiaPacket) {
        if (this.mShowDebugLogs) {
            Log.d("GaiaManager", "Received request to send a packet with expected acknowledgement for command: " + GaiaUtils.getGAIACommandToString(gaiaPacket.getCommand()));
        }
        GaiaRequest gaiaRequest = new GaiaRequest(1);
        gaiaRequest.packet = gaiaPacket;
        processRequest(gaiaRequest);
    }

    /* access modifiers changed from: protected */
    public void createUnacknowledgedRequest(GaiaPacket gaiaPacket) {
        if (this.mShowDebugLogs) {
            Log.d("GaiaManager", "Received request to send a packet with no acknowledgement for command: " + GaiaUtils.getGAIACommandToString(gaiaPacket.getCommand()));
        }
        GaiaRequest gaiaRequest = new GaiaRequest(3);
        gaiaRequest.packet = gaiaPacket;
        processRequest(gaiaRequest);
    }

    /* access modifiers changed from: protected */
    public void createAcknowledgmentRequest(GaiaPacket gaiaPacket, int i, @Nullable byte[] bArr) {
        if (this.mShowDebugLogs) {
            Log.d("GaiaManager", "Received request to send an acknowledgement packet for command: " + GaiaUtils.getGAIACommandToString(gaiaPacket.getCommand()) + " with status: " + GAIA.getStatusToString(i));
        }
        GaiaAcknowledgementRequest gaiaAcknowledgementRequest = new GaiaAcknowledgementRequest(i, bArr);
        gaiaAcknowledgementRequest.packet = gaiaPacket;
        processRequest(gaiaAcknowledgementRequest);
    }

    private void startTimeOutRequestRunnable(GaiaRequest gaiaRequest) {
        if (this.mShowDebugLogs) {
            Log.d("GaiaManager", "Set up TimeOutRequestRunnable for type request: " + gaiaRequest.type + " for command " + GaiaUtils.getGAIACommandToString(gaiaRequest.packet.getCommand()));
        }
        TimeOutRequestRunnable timeOutRequestRunnable = new TimeOutRequestRunnable(gaiaRequest);
        int command = gaiaRequest.packet.getCommand();
        if (this.mTimeOutRequestRunnableMap.containsKey(Integer.valueOf(command))) {
            this.mTimeOutRequestRunnableMap.get(Integer.valueOf(command)).add(timeOutRequestRunnable);
        } else {
            LinkedList linkedList = new LinkedList();
            linkedList.add(timeOutRequestRunnable);
            this.mTimeOutRequestRunnableMap.put(Integer.valueOf(gaiaRequest.packet.getCommand()), linkedList);
        }
        this.mHandler.postDelayed(timeOutRequestRunnable, (long) this.mTimeOutRequestDelay);
    }

    private boolean cancelTimeOutRequestRunnable(int i) {
        synchronized (this.mTimeOutRequestRunnableMap) {
            if (this.mShowDebugLogs) {
                Log.d("GaiaManager", "Request to cancel a TimeOutRequestRunnable for command: " + GaiaUtils.getGAIACommandToString(i));
            }
            if (!this.mTimeOutRequestRunnableMap.containsKey(Integer.valueOf(i))) {
                Log.w("GaiaManager", "No pending TimeOutRequestRunnable matches command: " + GaiaUtils.getGAIACommandToString(i));
                return false;
            }
            List list = this.mTimeOutRequestRunnableMap.get(Integer.valueOf(i));
            this.mHandler.removeCallbacks((TimeOutRequestRunnable) list.remove(0));
            if (list.isEmpty()) {
                this.mTimeOutRequestRunnableMap.remove(Integer.valueOf(i));
            }
            return true;
        }
    }

    private synchronized void resetTimeOutRequestRunnableMap() {
        if (this.mShowDebugLogs) {
            Log.d("GaiaManager", "Received request to reset the TimeOutRequestRunnable Map");
        }
        for (int i = 0; i < this.mTimeOutRequestRunnableMap.size(); i++) {
            Iterator it = this.mTimeOutRequestRunnableMap.valueAt(i).iterator();
            while (it.hasNext()) {
                this.mHandler.removeCallbacks((TimeOutRequestRunnable) it.next());
            }
        }
        this.mTimeOutRequestRunnableMap.clear();
    }

    private void processRequest(GaiaRequest gaiaRequest) {
        if (this.mShowDebugLogs) {
            Log.d("GaiaManager", "Processing request of type " + gaiaRequest.type);
        }
        switch (gaiaRequest.type) {
            case 1:
                try {
                    byte[] bytes = gaiaRequest.packet.getBytes();
                    startTimeOutRequestRunnable(gaiaRequest);
                    if (!sendGAIAPacket(bytes)) {
                        cancelTimeOutRequestRunnable(gaiaRequest.packet.getCommand());
                        Log.w("GaiaManager", "Fail to send GAIA packet for GAIA command: " + GaiaUtils.getGAIACommandToString(gaiaRequest.packet.getCommandId()));
                        onSendingFailed(gaiaRequest.packet);
                        return;
                    }
                    return;
                } catch (GaiaException e) {
                    Log.w("GaiaManager", "Exception when attempting to create GAIA packet: " + e.toString());
                    return;
                }
            case 2:
                GaiaAcknowledgementRequest gaiaAcknowledgementRequest = (GaiaAcknowledgementRequest) gaiaRequest;
                sendGAIAAcknowledgement(gaiaAcknowledgementRequest.packet, gaiaAcknowledgementRequest.status, gaiaAcknowledgementRequest.data);
                return;
            case 3:
                try {
                    if (!sendGAIAPacket(gaiaRequest.packet.getBytes())) {
                        Log.w("GaiaManager", "Fail to send GAIA packet for GAIA command: " + gaiaRequest.packet.getCommandId());
                        onSendingFailed(gaiaRequest.packet);
                        return;
                    }
                    return;
                } catch (GaiaException e2) {
                    Log.w("GaiaManager", "Exception when attempting to create GAIA packet: " + e2.toString());
                    return;
                }
            default:
                Log.w("GaiaManager", "Not possible to create request with type " + gaiaRequest.type + " for GAIA command: " + gaiaRequest.packet.getCommandId());
                return;
        }
    }

    private class TimeOutRequestRunnable implements Runnable {
        private final GaiaRequest request;

        TimeOutRequestRunnable(GaiaRequest gaiaRequest) {
            this.request = gaiaRequest;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0084, code lost:
            android.util.Log.w("GaiaManager", "No ACK packet for command: " + com.qualcomm.qti.libraries.gaia.GaiaUtils.getGAIACommandToString(r5.request.packet.getCommand()));
            r5.this$0.hasNotReceivedAcknowledgementPacket(r5.request.packet);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x00af, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r5 = this;
                com.qualcomm.qti.libraries.gaia.GaiaManager r0 = com.qualcomm.qti.libraries.gaia.GaiaManager.this
                android.util.ArrayMap r0 = r0.mTimeOutRequestRunnableMap
                monitor-enter(r0)
                com.qualcomm.qti.libraries.gaia.requests.GaiaRequest r1 = r5.request     // Catch:{ all -> 0x00b0 }
                com.qualcomm.qti.libraries.gaia.packets.GaiaPacket r1 = r1.packet     // Catch:{ all -> 0x00b0 }
                int r1 = r1.getCommand()     // Catch:{ all -> 0x00b0 }
                com.qualcomm.qti.libraries.gaia.GaiaManager r2 = com.qualcomm.qti.libraries.gaia.GaiaManager.this     // Catch:{ all -> 0x00b0 }
                boolean r2 = r2.mShowDebugLogs     // Catch:{ all -> 0x00b0 }
                if (r2 == 0) goto L_0x0031
                java.lang.String r2 = "GaiaManager"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b0 }
                r3.<init>()     // Catch:{ all -> 0x00b0 }
                java.lang.String r4 = "A request is timed out for command: "
                r3.append(r4)     // Catch:{ all -> 0x00b0 }
                java.lang.String r4 = com.qualcomm.qti.libraries.gaia.GaiaUtils.getGAIACommandToString(r1)     // Catch:{ all -> 0x00b0 }
                r3.append(r4)     // Catch:{ all -> 0x00b0 }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00b0 }
                android.util.Log.d(r2, r3)     // Catch:{ all -> 0x00b0 }
            L_0x0031:
                com.qualcomm.qti.libraries.gaia.GaiaManager r2 = com.qualcomm.qti.libraries.gaia.GaiaManager.this     // Catch:{ all -> 0x00b0 }
                android.util.ArrayMap r2 = r2.mTimeOutRequestRunnableMap     // Catch:{ all -> 0x00b0 }
                java.lang.Integer r3 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x00b0 }
                boolean r2 = r2.containsKey(r3)     // Catch:{ all -> 0x00b0 }
                if (r2 != 0) goto L_0x005d
                java.lang.String r2 = "GaiaManager"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b0 }
                r3.<init>()     // Catch:{ all -> 0x00b0 }
                java.lang.String r4 = "Unexpected runnable is running for command: "
                r3.append(r4)     // Catch:{ all -> 0x00b0 }
                java.lang.String r1 = com.qualcomm.qti.libraries.gaia.GaiaUtils.getGAIACommandToString(r1)     // Catch:{ all -> 0x00b0 }
                r3.append(r1)     // Catch:{ all -> 0x00b0 }
                java.lang.String r1 = r3.toString()     // Catch:{ all -> 0x00b0 }
                android.util.Log.w(r2, r1)     // Catch:{ all -> 0x00b0 }
                monitor-exit(r0)     // Catch:{ all -> 0x00b0 }
                return
            L_0x005d:
                com.qualcomm.qti.libraries.gaia.GaiaManager r2 = com.qualcomm.qti.libraries.gaia.GaiaManager.this     // Catch:{ all -> 0x00b0 }
                android.util.ArrayMap r2 = r2.mTimeOutRequestRunnableMap     // Catch:{ all -> 0x00b0 }
                java.lang.Integer r3 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x00b0 }
                java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x00b0 }
                java.util.LinkedList r2 = (java.util.LinkedList) r2     // Catch:{ all -> 0x00b0 }
                r2.remove(r5)     // Catch:{ all -> 0x00b0 }
                boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x00b0 }
                if (r2 == 0) goto L_0x0083
                com.qualcomm.qti.libraries.gaia.GaiaManager r2 = com.qualcomm.qti.libraries.gaia.GaiaManager.this     // Catch:{ all -> 0x00b0 }
                android.util.ArrayMap r2 = r2.mTimeOutRequestRunnableMap     // Catch:{ all -> 0x00b0 }
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x00b0 }
                r2.remove(r1)     // Catch:{ all -> 0x00b0 }
            L_0x0083:
                monitor-exit(r0)     // Catch:{ all -> 0x00b0 }
                java.lang.String r0 = "GaiaManager"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "No ACK packet for command: "
                r1.append(r2)
                com.qualcomm.qti.libraries.gaia.requests.GaiaRequest r2 = r5.request
                com.qualcomm.qti.libraries.gaia.packets.GaiaPacket r2 = r2.packet
                int r2 = r2.getCommand()
                java.lang.String r2 = com.qualcomm.qti.libraries.gaia.GaiaUtils.getGAIACommandToString(r2)
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                android.util.Log.w(r0, r1)
                com.qualcomm.qti.libraries.gaia.GaiaManager r0 = com.qualcomm.qti.libraries.gaia.GaiaManager.this
                com.qualcomm.qti.libraries.gaia.requests.GaiaRequest r1 = r5.request
                com.qualcomm.qti.libraries.gaia.packets.GaiaPacket r1 = r1.packet
                r0.hasNotReceivedAcknowledgementPacket(r1)
                return
            L_0x00b0:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x00b0 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.qti.libraries.gaia.GaiaManager.TimeOutRequestRunnable.run():void");
        }
    }
}
