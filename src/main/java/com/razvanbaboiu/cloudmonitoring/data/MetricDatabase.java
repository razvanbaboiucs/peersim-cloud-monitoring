package com.razvanbaboiu.cloudmonitoring.data;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.common.SortOrder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dizitart.no2.filters.FluentFilter.where;

public class MetricDatabase {

    public static final String PROJECT_ID = "projectId";
    public static final String NODE_ID = "nodeId";
    public static final String CPU = "cpu";
    public static final String MEMORY = "memory";
    public static final String ID = "_id";
    public static final String MODIFIED = "_modified";
    public static final String COLLECTION_NAME = "metrics";
    private static final Object mutex = new Object();
    private static MetricDatabase instance;
    private final Nitrite db;

    private MetricDatabase() {
        db = Nitrite.builder().openOrCreate();
    }

    public static MetricDatabase getInstance() {
        MetricDatabase value = instance;
        if (instance == null) {
            synchronized (mutex) {
                value = instance;
                if (value == null) {
                    instance = value = new MetricDatabase();
                }
            }
        }
        return value;
    }

    private NitriteCollection getCollection() {
        return db.getCollection(COLLECTION_NAME);
    }

    public void insert(Metric metric) {
        Map<String, Object> map = new HashMap<>();
        map.put(PROJECT_ID, metric.projectId());
        map.put(NODE_ID, metric.nodeId());
        map.put(CPU, metric.cpu());
        map.put(MEMORY, metric.memory());
        Document document = Document.createDocument(map);
        getCollection().insert(document);
    }

    public List<Metric> getMetricsForProject(Integer projectId, Integer windowSize) {
        FindOptions findOptions = new FindOptions();
        findOptions.limit(windowSize).thenOrderBy(ID, SortOrder.Descending);

        return getCollection()
                .find(where(PROJECT_ID).eq(projectId), findOptions)
                .toList().stream()
                .map(doc -> Metric.builder()
                        .id((String) doc.get(ID))
                        .modifiedAt((Long) doc.get(MODIFIED))
                        .projectId((Integer) doc.get(PROJECT_ID))
                        .nodeId((Long) doc.get(NODE_ID))
                        .cpu((Double) doc.get(CPU))
                        .memory((Double) doc.get(MEMORY))
                        .build())
                .sorted(Comparator.comparing(Metric::modifiedAt))
                .toList();
    }

    public Long getNumberOfMetricsByNode(Long nodeId) {
        return getCollection().find(where(NODE_ID).eq(nodeId)).size();
    }

    public void cleanup() {
        getCollection().clear();
    }
}
