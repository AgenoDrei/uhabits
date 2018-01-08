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
import org.isoron.uhabits.core.models.*;

import java.io.*;
import java.util.*;

@AutoFactory(allowSubclasses = true)
public class ExportProvTask implements Task
{
    private String archiveFilename;

    private File outputDir;

    @NonNull
    private final ExportProvTask.Listener listener;

    public ExportProvTask(@NonNull File outputDir,
                         @NonNull Listener listener)
    {
        this.listener = listener;
        this.outputDir = outputDir;
    }

    @Override
    public void doInBackground()
    {
        try
        {
            HabitsProvExporter exporter;
            exporter = new HabitsProvExporter(outputDir);
            archiveFilename = exporter.writeArchive();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostExecute()
    {
        listener.onExportProvFinished(archiveFilename);
    }

    public interface Listener
    {
        void onExportProvFinished(@Nullable String archiveFilename);
    }
}
