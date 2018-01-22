package org.isoron.uhabits.core.tasks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.factory.AutoFactory;

import org.isoron.uhabits.core.io.ProvUploader;
import org.openprovenance.prov.model.Document;

@AutoFactory(allowSubclasses = true)
public class UploadProvTask implements Task {

    private int resCode;
    private Document document;

    @NonNull
    private final UploadProvTask.Listener listener;

    public UploadProvTask(@NonNull Document provDoc,
                          @NonNull Listener listener)
    {
        this.listener = listener;
        this.document = provDoc;
    }


    @Override
    public void doInBackground() {
        try
        {
            ProvUploader uploader;
            uploader = new ProvUploader();
            resCode = uploader.uploadDocument(document);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostExecute()
    {
        listener.onUploadProvFinished(resCode);
    }

    public interface Listener
    {
        void onUploadProvFinished(@Nullable int responseCode);
    }
}
