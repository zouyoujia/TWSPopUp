package com.qualcomm.qti.gaiacontrol.rwcp;

import android.os.Handler;
import android.util.Log;
import org.uplus.twspopup.utils.SplitStringUtils;
import com.qualcomm.qti.gaiacontrol.Utils;
import java.util.Iterator;
import java.util.LinkedList;

public class RWCPClient {
    private final String TAG = "RWCPClient";
    private boolean isTimeOutRunning = false;
    private int mAcknowledgedSegments = 0;
    private int mCredits = this.mWindow;
    private int mDataTimeOutMs = 100;
    private final Handler mHandler = new Handler();
    private int mInitialWindow = 15;
    private boolean mIsResendingSegments = false;
    private int mLastAckSequence = -1;
    private final RWCPListener mListener;
    private int mMaximumWindow = 32;
    private int mNextSequence = 0;
    private final LinkedList<byte[]> mPendingData = new LinkedList<>();
    private boolean mShowDebugLogs = false;
    private int mState = 0;
    private final TimeOutRunnable mTimeOutRunnable = new TimeOutRunnable();
    private final LinkedList<Segment> mUnacknowledgedSegments = new LinkedList<>();
    private int mWindow = this.mInitialWindow;

    public interface RWCPListener {
        void onTransferFailed();

        void onTransferFinished();

        void onTransferProgress(int i);

        boolean sendRWCPSegment(byte[] bArr);
    }

    public RWCPClient(RWCPListener rWCPListener) {
        this.mListener = rWCPListener;
    }

    public boolean isRunningASession() {
        return this.mState != 0;
    }

    public void showDebugLogs(boolean z) {
        this.mShowDebugLogs = z;
        StringBuilder sb = new StringBuilder();
        sb.append("Debug logs are now ");
        sb.append(z ? "activated" : "deactivated");
        sb.append(SplitStringUtils.SPLIT_FILE_SUFFIX);
        Log.i("RWCPClient", sb.toString());
    }

    public boolean sendData(byte[] bArr) {
        this.mPendingData.add(bArr);
        int i = this.mState;
        if (i == 0) {
            return startSession();
        }
        if (i != 2 || this.isTimeOutRunning) {
            return true;
        }
        sendDataSegment();
        return true;
    }

    public void cancelTransfer() {
        logState("cancelTransfer");
        if (this.mState == 0) {
            Log.i("RWCPClient", "cancelTransfer: no ongoing transfer to cancel.");
            return;
        }
        reset(true);
        if (!sendRSTSegment()) {
            Log.w("RWCPClient", "Sending of RST segment has failed, terminating session.");
            terminateSession();
        }
    }

    public boolean onReceiveRWCPSegment(byte[] bArr) {
        if (bArr == null) {
            Log.w("RWCPClient", "onReceiveRWCPSegment called with a null bytes array.");
            return false;
        } else if (bArr.length < 1) {
            String str = "Analyse of RWCP Segment failed: the byte array does not contain the minimum required information.";
            if (this.mShowDebugLogs) {
                str = str + "\n\tbytes=" + Utils.getStringFromBytes(bArr);
            }
            Log.w("RWCPClient", str);
            return false;
        } else {
            Segment segment = new Segment(bArr);
            int operationCode = segment.getOperationCode();
            if (operationCode == -1) {
                Log.w("RWCPClient", "onReceivedRWCPSegment failed to get a RWCP segment from given bytes: " + Utils.getStringFromBytes(bArr));
                return false;
            }
            switch (operationCode) {
                case 0:
                    return receiveDataAck(segment);
                case 1:
                    return receiveSynAck(segment);
                case 2:
                    return receiveRST(segment);
                case 3:
                    return receiveGAP(segment);
                default:
                    Log.w("RWCPClient", "Received unknown operation code: " + operationCode);
                    return false;
            }
        }
    }

    public int getInitialWindowSize() {
        return this.mInitialWindow;
    }

    public boolean setInitialWindowSize(int i) {
        logState("set initial window size to " + i);
        if (this.mState != 0) {
            Log.w("RWCPClient", "FAIL to set initial window size to " + i + ": not possible when there is an ongoing " + "session.");
            return false;
        } else if (i <= 0 || i > this.mMaximumWindow) {
            Log.w("RWCPClient", "FAIL to set initial window to " + i + ": size is out of range.");
            return false;
        } else {
            this.mInitialWindow = i;
            this.mWindow = this.mInitialWindow;
            return true;
        }
    }

    public int getMaximumWindowSize() {
        return this.mMaximumWindow;
    }

    public boolean setMaximumWindowSize(int i) {
        logState("set maximum window size to " + i);
        if (this.mState != 0) {
            Log.w("RWCPClient", "FAIL to set maximum window size to " + i + ": not possible when there is an ongoing " + "session.");
            return false;
        } else if (i <= 0 || i > 32) {
            Log.w("RWCPClient", "FAIL to set maximum window to " + i + ": size is out of range.");
            return false;
        } else if (this.mInitialWindow > this.mMaximumWindow) {
            Log.w("RWCPClient", "FAIL to set maximum window to " + i + ": initial window is " + this.mInitialWindow + SplitStringUtils.SPLIT_FILE_SUFFIX);
            return false;
        } else {
            this.mMaximumWindow = i;
            if (this.mWindow <= this.mMaximumWindow) {
                return true;
            }
            Log.i("RWCPClient", "window is updated to be less than the maximum window size (" + this.mMaximumWindow + ").");
            this.mWindow = this.mMaximumWindow;
            return true;
        }
    }

    private boolean startSession() {
        logState("startSession");
        if (this.mState != 0) {
            Log.w("RWCPClient", "Start RWCP session failed: already an ongoing session.");
            return false;
        } else if (sendRSTSegment()) {
            return true;
        } else {
            Log.w("RWCPClient", "Start RWCP session failed: sending of RST segment failed.");
            terminateSession();
            return false;
        }
    }

    private void terminateSession() {
        logState("terminateSession");
        reset(true);
    }

    private boolean receiveSynAck(Segment segment) {
        if (this.mShowDebugLogs) {
            Log.d("RWCPClient", "Receive SYN_ACK for sequence " + segment.getSequenceNumber());
        }
        switch (this.mState) {
            case 1:
                cancelTimeOut();
                if (validateAckSequence(1, segment.getSequenceNumber()) >= 0) {
                    this.mState = 2;
                    if (this.mPendingData.size() > 0) {
                        sendDataSegment();
                    }
                } else {
                    Log.w("RWCPClient", "Receive SYN_ACK with unexpected sequence number: " + segment.getSequenceNumber());
                    terminateSession();
                    this.mListener.onTransferFailed();
                    sendRSTSegment();
                }
                return true;
            case 2:
                cancelTimeOut();
                if (this.mUnacknowledgedSegments.size() > 0) {
                    resendDataSegment();
                }
                return true;
            default:
                Log.w("RWCPClient", "Received unexpected SYN_ACK segment with header " + segment.getHeader() + " while in state " + RWCP.getStateLabel(this.mState));
                return false;
        }
    }

    private boolean receiveDataAck(Segment segment) {
        if (this.mShowDebugLogs) {
            Log.d("RWCPClient", "Receive DATA_ACK for sequence " + segment.getSequenceNumber());
        }
        switch (this.mState) {
            case 2:
                cancelTimeOut();
                int validateAckSequence = validateAckSequence(0, segment.getSequenceNumber());
                if (validateAckSequence >= 0) {
                    if (this.mCredits > 0 && !this.mPendingData.isEmpty()) {
                        sendDataSegment();
                    } else if (this.mPendingData.isEmpty() && this.mUnacknowledgedSegments.isEmpty()) {
                        sendRSTSegment();
                    } else if (this.mPendingData.isEmpty() || this.mCredits == 0) {
                        startTimeOut((long) this.mDataTimeOutMs);
                    }
                    this.mListener.onTransferProgress(validateAckSequence);
                }
                return true;
            case 3:
                if (this.mShowDebugLogs) {
                    Log.i("RWCPClient", "Received DATA_ACK(" + segment.getSequenceNumber() + ") segment while in state CLOSING: segment discarded.");
                }
                return true;
            default:
                Log.w("RWCPClient", "Received unexpected DATA_ACK segment with sequence " + segment.getSequenceNumber() + " while in state " + RWCP.getStateLabel(this.mState));
                return false;
        }
    }

    private boolean receiveRST(Segment segment) {
        if (this.mShowDebugLogs) {
            Log.d("RWCPClient", "Receive RST or RST_ACK for sequence " + segment.getSequenceNumber());
        }
        switch (this.mState) {
            case 1:
                Log.i("RWCPClient", "Received RST (sequence " + segment.getSequenceNumber() + ") in SYN_SENT state, ignoring " + "segment.");
                return true;
            case 2:
                Log.w("RWCPClient", "Received RST (sequence " + segment.getSequenceNumber() + ") in ESTABLISHED state, " + "terminating session, transfer failed.");
                terminateSession();
                this.mListener.onTransferFailed();
                return true;
            case 3:
                cancelTimeOut();
                validateAckSequence(2, segment.getSequenceNumber());
                reset(false);
                if (this.mPendingData.isEmpty()) {
                    this.mListener.onTransferFinished();
                } else if (!sendSYNSegment()) {
                    Log.w("RWCPClient", "Start session of RWCP data transfer failed: sending of SYN failed.");
                    terminateSession();
                    this.mListener.onTransferFailed();
                }
                return true;
            default:
                Log.w("RWCPClient", "Received unexpected RST segment with sequence=" + segment.getSequenceNumber() + " while in state " + RWCP.getStateLabel(this.mState));
                return false;
        }
    }

    private boolean receiveGAP(Segment segment) {
        if (this.mShowDebugLogs) {
            Log.d("RWCPClient", "Receive GAP for sequence " + segment.getSequenceNumber());
        }
        switch (this.mState) {
            case 2:
                if (this.mLastAckSequence > segment.getSequenceNumber()) {
                    Log.i("RWCPClient", "Ignoring GAP (" + segment.getSequenceNumber() + ") as last ack sequence is " + this.mLastAckSequence + SplitStringUtils.SPLIT_FILE_SUFFIX);
                    return true;
                }
                if (this.mLastAckSequence <= segment.getSequenceNumber()) {
                    decreaseWindow();
                    validateAckSequence(0, segment.getSequenceNumber());
                }
                cancelTimeOut();
                resendDataSegment();
                return true;
            case 3:
                if (this.mShowDebugLogs) {
                    Log.i("RWCPClient", "Received GAP(" + segment.getSequenceNumber() + ") segment while in state CLOSING: segment discarded.");
                }
                return true;
            default:
                Log.w("RWCPClient", "Received unexpected GAP segment with header " + segment.getHeader() + " while in state " + RWCP.getStateLabel(this.mState));
                return false;
        }
    }

    /* access modifiers changed from: private */
    public void onTimeOut() {
        if (this.isTimeOutRunning) {
            this.isTimeOutRunning = false;
            this.mIsResendingSegments = true;
            this.mAcknowledgedSegments = 0;
            if (this.mShowDebugLogs) {
                Log.i("RWCPClient", "TIME OUT > re sending segments");
            }
            if (this.mState == 2) {
                this.mDataTimeOutMs *= 2;
                if (this.mDataTimeOutMs > 2000) {
                    this.mDataTimeOutMs = 2000;
                }
                resendDataSegment();
                return;
            }
            resendSegment();
        }
    }

    private int validateAckSequence(int i, int i2) {
        if (i2 < 0) {
            Log.w("RWCPClient", "Received ACK sequence (" + i2 + ") is less than 0.");
            return -1;
        } else if (i2 > 63) {
            Log.w("RWCPClient", "Received ACK sequence (" + i2 + ") is bigger than its maximum value (" + 63 + ").");
            return -1;
        } else {
            int i3 = this.mLastAckSequence;
            int i4 = this.mNextSequence;
            if (i3 >= i4 || (i2 >= i3 && i2 <= i4)) {
                int i5 = this.mLastAckSequence;
                int i6 = this.mNextSequence;
                if (i5 <= i6 || i2 >= i5 || i2 <= i6) {
                    int i7 = 0;
                    int i8 = this.mLastAckSequence;
                    synchronized (this.mUnacknowledgedSegments) {
                        while (i8 != i2) {
                            i8 = increaseSequenceNumber(i8);
                            if (removeSegmentFromQueue(i, i8)) {
                                this.mLastAckSequence = i8;
                                if (this.mCredits < this.mWindow) {
                                    this.mCredits++;
                                }
                                i7++;
                            } else {
                                Log.w("RWCPClient", "Error validating sequence " + i8 + ": no corresponding segment in " + "pending segments.");
                            }
                        }
                    }
                    logState(i7 + " segment(s) validated with ACK sequence(code=" + i + ", seq=" + i2 + ")");
                    increaseWindow(i7);
                    return i7;
                }
                Log.w("RWCPClient", "Received ACK sequence (" + i2 + ") is out of interval: last received is " + this.mLastAckSequence + " and next will be " + this.mNextSequence);
                return -1;
            }
            Log.w("RWCPClient", "Received ACK sequence (" + i2 + ") is out of interval: last received is " + this.mLastAckSequence + " and next will be " + this.mNextSequence);
            return -1;
        }
    }

    private boolean sendRSTSegment() {
        boolean sendSegment;
        if (this.mState == 3) {
            return true;
        }
        reset(false);
        synchronized (this.mUnacknowledgedSegments) {
            this.mState = 3;
            Segment segment = new Segment(2, this.mNextSequence);
            sendSegment = sendSegment(segment, 1000);
            if (sendSegment) {
                this.mUnacknowledgedSegments.add(segment);
                this.mNextSequence = increaseSequenceNumber(this.mNextSequence);
                this.mCredits--;
                logState("send RST segment");
            }
        }
        return sendSegment;
    }

    private void logState(String str) {
        if (this.mShowDebugLogs) {
            Log.d("RWCPClient", str + "\t\t\tstate=" + RWCP.getStateLabel(this.mState) + "\n\tWindow: \tcurrent = " + this.mWindow + " \t\tdefault = " + this.mInitialWindow + " \t\tcredits = " + this.mCredits + "\n\tSequence: \tlast = " + this.mLastAckSequence + " \t\tnext = " + this.mNextSequence + "\n\tPending: \tPSegments = " + this.mUnacknowledgedSegments.size() + " \t\tPData = " + this.mPendingData.size());
        }
    }

    private boolean sendSYNSegment() {
        boolean sendSegment;
        synchronized (this.mUnacknowledgedSegments) {
            this.mState = 1;
            Segment segment = new Segment(1, this.mNextSequence);
            sendSegment = sendSegment(segment, 1000);
            if (sendSegment) {
                this.mUnacknowledgedSegments.add(segment);
                this.mNextSequence = increaseSequenceNumber(this.mNextSequence);
                this.mCredits--;
                logState("send SYN segment");
            }
        }
        return sendSegment;
    }

    private void sendDataSegment() {
        while (this.mCredits > 0 && !this.mPendingData.isEmpty() && !this.mIsResendingSegments && this.mState == 2) {
            synchronized (this.mUnacknowledgedSegments) {
                Segment segment = new Segment(0, this.mNextSequence, this.mPendingData.poll());
                sendSegment(segment, this.mDataTimeOutMs);
                this.mUnacknowledgedSegments.add(segment);
                this.mNextSequence = increaseSequenceNumber(this.mNextSequence);
                this.mCredits--;
            }
        }
        logState("send DATA segments");
    }

    private int increaseSequenceNumber(int i) {
        return (i + 1) % 64;
    }

    private int decreaseSequenceNumber(int i, int i2) {
        return (((i - i2) + 63) + 1) % 64;
    }

    private void resendSegment() {
        if (this.mState == 2) {
            Log.w("RWCPClient", "Trying to resend non data segment while in ESTABLISHED state.");
            return;
        }
        this.mIsResendingSegments = true;
        this.mCredits = this.mWindow;
        synchronized (this.mUnacknowledgedSegments) {
            Iterator it = this.mUnacknowledgedSegments.iterator();
            while (it.hasNext()) {
                Segment segment = (Segment) it.next();
                int i = 1000;
                if (segment.getOperationCode() != 1) {
                    if (segment.getOperationCode() != 2) {
                        i = this.mDataTimeOutMs;
                    }
                }
                sendSegment(segment, i);
                this.mCredits--;
            }
        }
        logState("resend segments");
        this.mIsResendingSegments = false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0064, code lost:
        r8.mNextSequence = decreaseSequenceNumber(r8.mNextSequence, r3);
        r3 = r8.mUnacknowledgedSegments.iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0076, code lost:
        if (r3.hasNext() == false) goto L_0x0089;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0078, code lost:
        sendSegment((com.qualcomm.qti.gaiacontrol.rwcp.Segment) r3.next(), r8.mDataTimeOutMs);
        r8.mCredits--;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void resendDataSegment() {
        /*
            r8 = this;
            int r0 = r8.mState
            r1 = 2
            if (r0 == r1) goto L_0x000d
            java.lang.String r0 = "RWCPClient"
            java.lang.String r1 = "Trying to resend data segment while not in ESTABLISHED state."
            android.util.Log.w(r0, r1)
            return
        L_0x000d:
            r0 = 1
            r8.mIsResendingSegments = r0
            int r1 = r8.mWindow
            r8.mCredits = r1
            java.lang.String r1 = "reset credits"
            r8.logState(r1)
            java.util.LinkedList<com.qualcomm.qti.gaiacontrol.rwcp.Segment> r1 = r8.mUnacknowledgedSegments
            monitor-enter(r1)
            r2 = 0
            r3 = r2
        L_0x001e:
            java.util.LinkedList<com.qualcomm.qti.gaiacontrol.rwcp.Segment> r4 = r8.mUnacknowledgedSegments     // Catch:{ all -> 0x0099 }
            int r4 = r4.size()     // Catch:{ all -> 0x0099 }
            int r5 = r8.mCredits     // Catch:{ all -> 0x0099 }
            if (r4 <= r5) goto L_0x0064
            java.util.LinkedList<com.qualcomm.qti.gaiacontrol.rwcp.Segment> r4 = r8.mUnacknowledgedSegments     // Catch:{ all -> 0x0099 }
            java.lang.Object r4 = r4.getLast()     // Catch:{ all -> 0x0099 }
            com.qualcomm.qti.gaiacontrol.rwcp.Segment r4 = (com.qualcomm.qti.gaiacontrol.rwcp.Segment) r4     // Catch:{ all -> 0x0099 }
            int r5 = r4.getOperationCode()     // Catch:{ all -> 0x0099 }
            if (r5 != 0) goto L_0x0045
            r8.removeSegmentFromQueue(r4)     // Catch:{ all -> 0x0099 }
            java.util.LinkedList<byte[]> r5 = r8.mPendingData     // Catch:{ all -> 0x0099 }
            byte[] r4 = r4.getPayload()     // Catch:{ all -> 0x0099 }
            r5.addFirst(r4)     // Catch:{ all -> 0x0099 }
            int r3 = r3 + 1
            goto L_0x001e
        L_0x0045:
            java.lang.String r5 = "RWCPClient"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0099 }
            r6.<init>()     // Catch:{ all -> 0x0099 }
            java.lang.String r7 = "Segment "
            r6.append(r7)     // Catch:{ all -> 0x0099 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0099 }
            r6.append(r4)     // Catch:{ all -> 0x0099 }
            java.lang.String r4 = " in pending segments but not a DATA segment."
            r6.append(r4)     // Catch:{ all -> 0x0099 }
            java.lang.String r4 = r6.toString()     // Catch:{ all -> 0x0099 }
            android.util.Log.w(r5, r4)     // Catch:{ all -> 0x0099 }
        L_0x0064:
            int r4 = r8.mNextSequence     // Catch:{ all -> 0x0099 }
            int r3 = r8.decreaseSequenceNumber(r4, r3)     // Catch:{ all -> 0x0099 }
            r8.mNextSequence = r3     // Catch:{ all -> 0x0099 }
            java.util.LinkedList<com.qualcomm.qti.gaiacontrol.rwcp.Segment> r3 = r8.mUnacknowledgedSegments     // Catch:{ all -> 0x0099 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0099 }
        L_0x0072:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x0099 }
            if (r4 == 0) goto L_0x0089
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x0099 }
            com.qualcomm.qti.gaiacontrol.rwcp.Segment r4 = (com.qualcomm.qti.gaiacontrol.rwcp.Segment) r4     // Catch:{ all -> 0x0099 }
            int r5 = r8.mDataTimeOutMs     // Catch:{ all -> 0x0099 }
            r8.sendSegment(r4, r5)     // Catch:{ all -> 0x0099 }
            int r4 = r8.mCredits     // Catch:{ all -> 0x0099 }
            int r4 = r4 - r0
            r8.mCredits = r4     // Catch:{ all -> 0x0099 }
            goto L_0x0072
        L_0x0089:
            monitor-exit(r1)     // Catch:{ all -> 0x0099 }
            java.lang.String r0 = "Resend DATA segments"
            r8.logState(r0)
            r8.mIsResendingSegments = r2
            int r0 = r8.mCredits
            if (r0 <= 0) goto L_0x0098
            r8.sendDataSegment()
        L_0x0098:
            return
        L_0x0099:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0099 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.qti.gaiacontrol.rwcp.RWCPClient.resendDataSegment():void");
    }

    private boolean sendSegment(Segment segment, int i) {
        if (!this.mListener.sendRWCPSegment(segment.getBytes())) {
            return false;
        }
        startTimeOut((long) i);
        return true;
    }

    private boolean removeSegmentFromQueue(int i, int i2) {
        synchronized (this.mUnacknowledgedSegments) {
            Iterator it = this.mUnacknowledgedSegments.iterator();
            while (it.hasNext()) {
                Segment segment = (Segment) it.next();
                if (segment.getOperationCode() == i && segment.getSequenceNumber() == i2) {
                    this.mUnacknowledgedSegments.remove(segment);
                    return true;
                }
            }
            Log.w("RWCPClient", "Pending segments does not contain acknowledged segment: code=" + i + " \tsequence=" + i2);
            return false;
        }
    }

    private boolean removeSegmentFromQueue(Segment segment) {
        synchronized (this.mUnacknowledgedSegments) {
            if (this.mUnacknowledgedSegments.remove(segment)) {
                return true;
            }
            Log.w("RWCPClient", "Pending unack segments does not contain segment (code=" + segment.getOperationCode() + ", seq=" + segment.getSequenceNumber() + ")");
            return false;
        }
    }

    private void reset(boolean z) {
        synchronized (this.mUnacknowledgedSegments) {
            this.mLastAckSequence = -1;
            this.mNextSequence = 0;
            this.mState = 0;
            this.mUnacknowledgedSegments.clear();
            this.mWindow = this.mInitialWindow;
            this.mAcknowledgedSegments = 0;
            this.mCredits = this.mWindow;
            cancelTimeOut();
        }
        if (z) {
            this.mPendingData.clear();
        }
        logState("reset");
    }

    private void increaseWindow(int i) {
        this.mAcknowledgedSegments += i;
        int i2 = this.mAcknowledgedSegments;
        int i3 = this.mWindow;
        if (i2 > i3 && i3 < this.mMaximumWindow) {
            this.mAcknowledgedSegments = 0;
            this.mWindow = i3 + 1;
            this.mCredits++;
            logState("increase window to " + this.mWindow);
        }
    }

    private void decreaseWindow() {
        this.mWindow = ((this.mWindow - 1) / 2) + 1;
        int i = this.mWindow;
        if (i > this.mMaximumWindow || i < 1) {
            this.mWindow = 1;
        }
        this.mAcknowledgedSegments = 0;
        this.mCredits = this.mWindow;
        logState("decrease window to " + this.mWindow);
    }

    private void startTimeOut(long j) {
        if (this.isTimeOutRunning) {
            this.mHandler.removeCallbacks(this.mTimeOutRunnable);
        }
        this.isTimeOutRunning = true;
        this.mHandler.postDelayed(this.mTimeOutRunnable, j);
    }

    private void cancelTimeOut() {
        if (this.isTimeOutRunning) {
            this.mHandler.removeCallbacks(this.mTimeOutRunnable);
            this.isTimeOutRunning = false;
        }
    }

    private class TimeOutRunnable implements Runnable {
        private TimeOutRunnable() {
        }

        public void run() {
            RWCPClient.this.onTimeOut();
        }
    }
}
