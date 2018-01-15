package org.isoron.uhabits.core.io;

import android.support.annotation.NonNull;

import org.isoron.uhabits.core.utils.DateUtils;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.IndexedDocument;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.StatementOrBundle;
import org.openprovenance.prov.template.Expand;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by muel_s12 on 20.12.2017.
 */

public class HabitsProvExporter {

    private String exportDirName;
    private ProvenanceUtil util;
    private InteropFramework fw;

    public HabitsProvExporter(@NonNull File dir, @NonNull String username)
    {
        this.exportDirName = dir.getAbsolutePath() + "/";
        util = new ProvenanceUtil("user", "loophabits.org", username);

        ProvFactory p = InteropFramework.newXMLProvFactory();
        fw = new InteropFramework(p);
    }

    public String writeArchive() throws IOException
    {
        Document input = util.createInputDocument("Loop Tracking User", "HabitData", "Habit Userdata", "Loop Habit Tracking");
        Document agg = util.createAggregationDocument("HabitData", "Loop Tracking User", "Aggregated Userdata", "Habit Userdata", "Loop Habit Tracking", "HabitSummary");
        Document export = util.createExportDocument("HabitSummary", "Loop Tracking User", "Exported Userdata", "Aggregated Userdata", "Loop Habit Tracking", "HabitReport");

        Document merge = util.mergeDocuments(input, agg, export);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(DateUtils.getStartOfToday());
        String provNFilename = String.format("%s/Loop Habits PROVN %s.provn", exportDirName, date);

        fw.writeDocument(provNFilename, merge);
        return provNFilename;
    }
}

class ProvenanceUtil {
    private  ProvFactory p;
    private InteropFramework fw;
    private Namespace ns;
    private Expand ep;

    private String gPrefix;
    private QualifiedName user;

    ProvenanceUtil(String globalPrefix, String prefixDomain, String username) {
        p = InteropFramework.newXMLProvFactory();
        fw = new InteropFramework(p);
        ep = new Expand(p, false, true);
        ns = new Namespace();
        ns.addKnownNamespaces();
        ns.register(globalPrefix, prefixDomain);

        gPrefix = globalPrefix;
        user = getQN(username);
    }

    private Document expandDocument(Document tmpl, Document binding) {
        return ep.expander(tmpl, binding);
    }

    Document mergeDocuments(Document... docs) {
        IndexedDocument merge = new IndexedDocument(p, p.newDocument(), true);

        for(Document doc : docs)
            merge.merge(doc);

        return merge.toDocument();
    }

    private void changeDocumentEntity(Document doc, int entityIdx, String newValue) {
        changeDocumentEntity(doc, entityIdx, 0, newValue, null);
    }
    private void changeDocumentEntity(Document doc, int entityIdx, QualifiedName newQValue) {
        changeDocumentEntity(doc, entityIdx, 0, null, newQValue);
    }

    private void changeDocumentEntity(Document doc, int entityIdx, int otherIdx, String newValue, QualifiedName newQValue) {
        List<StatementOrBundle> bundle = doc.getStatementOrBundle();
        Entity e = (Entity) bundle.get(entityIdx);
        if(newValue != null) e.getOther().get(otherIdx).setValue(newValue);
        else if(newQValue != null)  e.getOther().get(otherIdx).setValue(newQValue);
    }

    private QualifiedName getQN(String name) {
        return ns.qualifiedName(gPrefix, name, p);
    }

    private Document getDocument(String name) {
        String path = "/raw/" + name + ".provn";
        InputStream stream = getClass().getResourceAsStream(path);

        assert stream != null;

        return fw.readDocument(stream, InteropFramework.ProvFormat.PROVN, "base" );
    }

    Document createInputDocument(String userLabel, String userdata, String userdataLabel, String agentLabel) {
        Document exBinding = getDocument("input_binding");
        Document template = getDocument("input");
        ns.extendWith(exBinding.getNamespace());
        exBinding.setNamespace(ns);

        changeDocumentEntity(exBinding, 0, userLabel);
        changeDocumentEntity(exBinding, 1, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        changeDocumentEntity(exBinding, 3, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        changeDocumentEntity(exBinding, 4, getQN(userdata));
        changeDocumentEntity(exBinding, 6, userdataLabel);
        changeDocumentEntity(exBinding, 7, agentLabel);
        changeDocumentEntity(exBinding, 8, user);
        changeDocumentEntity(exBinding, 9, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        changeDocumentEntity(exBinding, 10, getQN("input"));

        //fw.writeDocument(System.out, ProvFormat.PROVN, exBinding);
        //printDocument(input, "Input");
        return expandDocument(template, exBinding);
    }

    Document createAggregationDocument(String userdata, String userLabel, String resUserdataLabel, String userdataLabel, String agentLabel, String resUserdata) {
        Document aggregationBind = getDocument("aggregate_binding");
        Document template = getDocument("aggregate");
        ns.extendWith(aggregationBind.getNamespace());
        aggregationBind.setNamespace(ns);

        changeDocumentEntity(aggregationBind, 0, getQN(userdata));
        changeDocumentEntity(aggregationBind, 1, userLabel);
        changeDocumentEntity(aggregationBind, 2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        changeDocumentEntity(aggregationBind, 3, getQN("aggregate"));
        changeDocumentEntity(aggregationBind, 5, resUserdataLabel);
        changeDocumentEntity(aggregationBind, 6, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        changeDocumentEntity(aggregationBind, 7, userdataLabel);
        changeDocumentEntity(aggregationBind, 8, getQN(resUserdata));
        changeDocumentEntity(aggregationBind, 11, agentLabel);
        changeDocumentEntity(aggregationBind, 12, user);
        changeDocumentEntity(aggregationBind, 13, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        changeDocumentEntity(aggregationBind, 14, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));

        //fw.writeDocument(System.out, ProvFormat.PROVN, aggregationBind);
        //printDocument(agg, "Aggregation");
        return expandDocument(template, aggregationBind);
    }

    Document createExportDocument(String userdata, String userLabel, String resUserdataLabel, String userdataLabel, String agentLabel, String resUserdata) {
        Document exportBind = getDocument("export_binding");
        Document template = getDocument("export");
        ns.extendWith(exportBind.getNamespace());
        exportBind.setNamespace(ns);

        changeDocumentEntity(exportBind, 0, getQN(userdata));
        changeDocumentEntity(exportBind, 1, userLabel);
        changeDocumentEntity(exportBind, 2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        changeDocumentEntity(exportBind, 3, getQN("export"));
        changeDocumentEntity(exportBind, 5, resUserdataLabel);
        changeDocumentEntity(exportBind, 6, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        changeDocumentEntity(exportBind, 7, userdataLabel);
        changeDocumentEntity(exportBind, 8, getQN(resUserdata));
        changeDocumentEntity(exportBind, 11, agentLabel);
        changeDocumentEntity(exportBind, 12, user);
        changeDocumentEntity(exportBind, 13, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        changeDocumentEntity(exportBind, 14, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));

        //fw.writeDocument(System.out, ProvFormat.PROVN, exportBind);
        //printDocument(export, "Exoirt");
        return expandDocument(template, exportBind);
    }
}
