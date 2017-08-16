package com.golshadi.majid.core.mainWorker;

import android.webkit.MimeTypeMap;

import com.golshadi.majid.Utils.helper.FileUtils;
import com.golshadi.majid.appConstants.DispatchEcode;
import com.golshadi.majid.appConstants.DispatchElevel;
import com.golshadi.majid.core.chunkWorker.Moderator;
import com.golshadi.majid.core.chunkWorker.Rebuilder;
import com.golshadi.majid.core.enums.TaskStates;
import com.golshadi.majid.database.ChunksDataSource;
import com.golshadi.majid.database.TasksDataSource;
import com.golshadi.majid.database.elements.Chunk;
import com.golshadi.majid.database.elements.Task;
import com.golshadi.majid.report.listener.DownloadManagerListenerModerator;
import com.songshu.squirrelvideo.utils.L;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Majid Golshadi on 4/20/2014.
 */
public class AsyncStartDownload extends Thread {

    private static final String TAG = AsyncStartDownload.class.getSimpleName() + ":";

    private final long MegaByte = 1048576;
    private final TasksDataSource tasksDataSource;
    private final ChunksDataSource chunksDataSource;
    private final Moderator moderator;
    private final DownloadManagerListenerModerator downloadManagerListener;
    private final Task task;

    public AsyncStartDownload(TasksDataSource taskDs, ChunksDataSource chunkDs, Moderator moderator, DownloadManagerListenerModerator listener, Task task) {
        this.tasksDataSource = taskDs;
        this.chunksDataSource = chunkDs;
        this.moderator = moderator;
        this.downloadManagerListener = listener;
        this.task = task;
    }

    @Override
    public void run() {

        // switch on task state
        switch (task.state) {

            case TaskStates.INIT:
                L.d(TAG, task.id + "------TaskStates.INIT------");
                // -->get file info
                // -->save in table
                // -->slice file to some chunks ( in some case maybe user set 16 but we need only 4 chunk)
                //      and make file in directory
                // -->save chunks in tables

                if (!getTaskFileInfo(task))
                    break;
                while (task.size <= 0) {
                    getTaskFileInfo(task);
                }
                L.d(TAG, task.id + "______Task Size______" + task.size);
                convertTaskToChunks(task);


            case TaskStates.READY:
            case TaskStates.PAUSED:
                // -->-->if it's not resumable
                //          * fetch chunks
                //          * delete it's chunk
                //          * delete old file
                //          * insert new chunk
                //          * make new file
                // -->start to download any chunk
                if (!task.resumable) {
                    deleteChunk(task);
                    generateNewChunk(task);
                }
                L.d(TAG, task.id + "------StartDownload------Moderator Start");
                moderator.start(task, downloadManagerListener);
                break;

            case TaskStates.DOWNLOAD_FINISHED:
                // -->rebuild general file
                // -->save in database
                // -->report to user
                Thread rb = new Rebuilder(task,
                        chunksDataSource.chunksRelatedTask(task.id), moderator);
                rb.run();

            case TaskStates.END:

            case TaskStates.DOWNLOADING:
                // -->do nothing
                break;
        }

        return;
    }

    private boolean getTaskFileInfo(Task task) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(task.url).openConnection();
            urlConnection .setRequestProperty("Accept-Encoding", "identity");
            urlConnection.connect();
            task.size = urlConnection.getContentLength();
            task.extension = MimeTypeMap.getFileExtensionFromUrl(task.url);
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            L.d(TAG, DispatchEcode.EXCEPTION + " ...... " + DispatchElevel.URL_INVALID);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            L.d(TAG, DispatchEcode.EXCEPTION + " ...... " + DispatchElevel.OPEN_CONNECTION);
            return false;
        }
        return true;
    }


    private void convertTaskToChunks(Task task) {
        if (task.size == 0) {
            // it's NOT resumable!!
            // one chunk
            task.resumable = false;
            task.chunks = 1;
        } else {
            // resumable
            // depend on file size assign number of chunks; up to Maximum user
            task.resumable = true;
            int MaximumUserCHUNKS = task.chunks / 2;
            task.chunks = 1;

            for (int f = 1; f <= MaximumUserCHUNKS; f++)
                if (task.size > MegaByte * f)
                    task.chunks = f * 2;
        }


        // Change Task State
        int firstChunkID =
                chunksDataSource.insertChunks(task);
        makeFileForChunks(firstChunkID, task);
        task.state = TaskStates.READY;
        L.d(TAG, task.id + "----------startDownload----------TaskStates.READY");
        tasksDataSource.update(task);
    }

    private void makeFileForChunks(int firstId, Task task) {
        for (int endId = firstId + task.chunks; firstId < endId; firstId++)
            FileUtils.create(task.save_address, String.valueOf(firstId));

    }


    private void deleteChunk(Task task) {
        List<Chunk> TaskChunks = chunksDataSource.chunksRelatedTask(task.id);

        for (Chunk chunk : TaskChunks) {
            FileUtils.delete(task.save_address, String.valueOf(chunk.id));
            chunksDataSource.delete(chunk.id);
        }
    }

    private void generateNewChunk(Task task) {
        int firstChunkID =
                chunksDataSource.insertChunks(task);
        makeFileForChunks(firstChunkID, task);
    }

}
