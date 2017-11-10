package com.example.skinhealthchecker;

/**
 * Created by chief on 6/17/2017.
 */

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ProgressInputStream extends InputStream {

    /* Key to retrieve progress value from message bundle passed to handler */
    public static final String PROGRESS_UPDATE = "progress_update";

    private static final int TEN_KILOBYTES = 1024 * 10;

    private InputStream inputStream;
    private Handler handler;

    private long progress;
    private long lastUpdate;

    private boolean closed;

    public ProgressInputStream(InputStream inputStream, Handler handler) {
        this.inputStream = inputStream;
        this.handler = handler;

        this.progress = 0;
        this.lastUpdate = 0;

        this.closed = false;
    }

    @Override
    public int read() throws IOException {
        int count = inputStream.read();
        return incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = inputStream.read(b, off, len);
        return incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (closed)
            throw new IOException("already closed");
        closed = true;
    }

    private int incrementCounterAndUpdateDisplay(int count) {
        if (count > 0)
            progress += count;
        lastUpdate = maybeUpdateDisplay(progress, lastUpdate);
        return count;
    }

    private long maybeUpdateDisplay(long progress, long lastUpdate) {
        if (progress - lastUpdate > TEN_KILOBYTES) {
            lastUpdate = progress;
            sendLong(PROGRESS_UPDATE, progress);
        }
        return lastUpdate;
    }

    public void sendLong(String key, long value) {
        Bundle data = new Bundle();
        data.putLong(key, value);

        Message message = Message.obtain();
        message.setData(data);
        handler.sendMessage(message);
    }
}