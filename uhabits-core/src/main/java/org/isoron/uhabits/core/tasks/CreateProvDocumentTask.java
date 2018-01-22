/*
 * Copyright (C) 2017 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.core.tasks;

import android.support.annotation.*;

import com.google.auto.factory.*;

import org.isoron.uhabits.core.io.*;
import org.openprovenance.prov.model.Document;

import java.io.*;
import java.util.*;

@AutoFactory(allowSubclasses = true)
public class CreateProvDocumentTask implements Task
{
    private String username;
    private Document provDoc;

    @NonNull
    private final CreateProvDocumentTask.Listener listener;

    public CreateProvDocumentTask(@NonNull String username,
                          @NonNull Listener listener)
    {
        this.listener = listener;
        this.username = username;
    }

    @Override
    public void doInBackground()
    {
        HabitsProvExporter exporter;
        exporter = new HabitsProvExporter(new File("/"), username);
        provDoc = exporter.createDocument();
    }

    @Override
    public void onPostExecute()
    {
        listener.onCreateProvFinished(provDoc);
    }

    public interface Listener
    {
        void onCreateProvFinished(@Nullable Document provDoc);
    }
}
