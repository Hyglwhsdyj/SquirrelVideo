package com.golshadi.majid.core.chunkWorker;

import com.golshadi.majid.Utils.helper.FileUtils;
import com.golshadi.majid.database.elements.Chunk;
import com.golshadi.majid.database.elements.Task;
import com.songshu.squirrelvideo.utils.L;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Majid Golshadi on 4/14/2014.
 */
public class AsyncWorker extends Thread {
    private static final String TAG = AsyncWorker.class.getSimpleName() + ":";

    private final int BUFFER_SIZE = 2048;

    private final Task task;
    private final Chunk chunk;
    private final Moderator observer;
    private byte[] buffer;
    private ConnectionWatchDog watchDog;

    public boolean stop = false;


    public AsyncWorker(Task task, Chunk chunk, Moderator moderator) {
        buffer = new byte[BUFFER_SIZE];

        this.task = task;
        this.chunk = chunk;
        this.observer = moderator;
    }


    @Override
    public void run() {
        try {
            L.d(TAG, task.id + "______task.url______" + task.url);
            URL url = new URL(task.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(20 * 1000);
            if (chunk.end != 0) // support unresumable links
                connection.setRequestProperty("Range", "bytes=" + chunk.begin + "-" + chunk.end);
            connection.connect();

            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            FileOutputStream chunkFile = new FileOutputStream(new File(FileUtils.address(task.save_address, String.valueOf(chunk.id))), true);

            int len = 0;
            watchDog = new ConnectionWatchDog(22 * 1000, this);
            watchDog.start();
            L.d(TAG, task.id + "______开始网络读取______");

            while (!this.isInterrupted() && (len = bis.read(buffer)) != -1) {
                watchDog.reset();
                chunkFile.write(buffer, 0, len);
                process(len);
            }

            chunkFile.flush();
            chunkFile.close();
            watchDog.interrupt();
            connection.disconnect();
            L.d(TAG, task.id + "______结束网络读取______");

            if (!this.isInterrupted()) {
                L.d(TAG, task.id + "______准备构建文件______");
                observer.rebuild(chunk);
            }

        } catch (SocketTimeoutException e) {
            L.d(TAG, task.id + "______网络请求超时______");
            e.printStackTrace();
            pauseRelatedTask();
            observer.connectionLost(task.id);
        } catch (MalformedURLException e) {
            L.d(TAG, task.id + "______MalformedURLException______" + e);
            e.printStackTrace();
            pauseRelatedTask();
        } catch (IOException e) {
            L.d(TAG, task.id + "______IOException______" + e);
            e.printStackTrace();
            pauseRelatedTask();
        }

        return;
    }

    private void process(int read) {
        observer.process(chunk.task_id, read);
    }

    private void pauseRelatedTask() {
        observer.pause(task.id);
    }

    private boolean flag = true;

    public void connectionTimeOut() {
        if (flag) {
            watchDog.interrupt();
            flag = false;
            pauseRelatedTask();
            observer.connectionLost(task.id);
        }

    }

}
