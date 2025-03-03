import net.cuprak.demo.heap.HeapUtils;
import org.ehcache.CopiedOnHeapValueHolder;
import org.hibernate.CacheItem;
import org.hibernate.QueryKey;
import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.HeapFactory;
import org.netbeans.lib.profiler.heap.Instance;
import org.netbeans.lib.profiler.heap.JavaClass;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EhCacheAnalysis {

    public static void main(String[] args) throws IOException {
        Heap heap = HeapFactory.createHeap(new File("heap_dump.hprof"));

        JavaClass ehcacheManagerClass = heap.getJavaClassByName("org.ehcache.core.EhcacheManager");

        List<Instance> ehcacheManagerClassInstances = ehcacheManagerClass.getInstances();

        for (Instance ehcacheManagerClassInstance : ehcacheManagerClassInstances) {
            Instance caches = (Instance) ehcacheManagerClassInstance.getValueOfField("caches");
            Map<Instance, Instance> instanceInstanceMap = HeapUtils.processConcurrentHashMap(caches);
            for (Map.Entry<Instance, Instance> instanceInstanceEntry : instanceInstanceMap.entrySet()) {
                String key = HeapUtils.processString(instanceInstanceEntry.getKey());
                System.out.println(key);
                processCacheHolder(instanceInstanceEntry.getValue());
            }
        }
    }

    private static void processCacheHolder(Instance cacheHolder){
        // org.ehcache.core.EhcacheManager$CacheHolder
        Objects.requireNonNull(cacheHolder);

        Instance cache = (Instance) cacheHolder.getValueOfField("cache");
        Instance store = (Instance) cache.getValueOfField("store");
        Instance map = (Instance) store.getValueOfField("map");
        Instance realMapInstance = (Instance) map.getValueOfField("realMap");
        Map<Instance, Instance> realMap = HeapUtils.processConcurrentHashMap(realMapInstance);
        System.out.println(realMap.size());
        Map<QueryKey, Integer> queryKeyIntegerMap = new HashMap<>();
        Map<QueryKey, CopiedOnHeapValueHolder<CacheItem>> dtoMap = new HashMap<>();
        Map<String, Integer> keyTypesCounter = new HashMap<>();
        Map<String, Integer> valuesTypesCounter = new HashMap<>();
        for (Map.Entry<Instance, Instance> entry : realMap.entrySet()) {
            if (entry.getKey() != null){

                keyTypesCounter.merge(entry.getKey().getJavaClass().getName(), 1, Integer::sum);
                valuesTypesCounter.merge(entry.getValue().getJavaClass().getName(), 1, Integer::sum);

                if (entry.getKey().getJavaClass().getName().equals("org.hibernate.cache.spi.QueryKey")) {
                    final var queryKey = processQueryKey(entry.getKey());
                    queryKeyIntegerMap.merge(queryKey, 1, Integer::sum);

                    if (entry.getValue() != null) {
                        final var copiedOnHeapValueHolder = processQueryValue(entry.getValue());
                        dtoMap.put(queryKey, copiedOnHeapValueHolder);
                    }
                }
            }
        }
        System.out.println(keyTypesCounter);
        System.out.println(valuesTypesCounter);
        System.out.println(queryKeyIntegerMap);
        System.out.println(dtoMap);
    }

    private static QueryKey processQueryKey(Instance queryKey){
        // org.hibernate.cache.spi.QueryKey
        Objects.requireNonNull(queryKey);

        final var sqlQueryStringInstance = (Instance) queryKey.getValueOfField("sqlQueryString");
        String sqlQueryString = HeapUtils.processString(sqlQueryStringInstance);
        final var hashCode = (int) queryKey.getValueOfField("hashCode");

        return new QueryKey(sqlQueryString, hashCode);
    }

    private static CopiedOnHeapValueHolder<CacheItem> processQueryValue(Instance queryKey){
        // org.ehcache.impl.internal.store.heap.holders.CopiedOnHeapValueHolder
        Objects.requireNonNull(queryKey);

        final var copiedValue = (Instance) queryKey.getValueOfField("copiedValue");
        final var cacheItem = processCacheItem(copiedValue);
        final var creationTime = (long) queryKey.getValueOfField("creationTime");
        final var lastAccessTime = (long) queryKey.getValueOfField("lastAccessTime");
        final var expirationTime = (long) queryKey.getValueOfField("expirationTime");

        return new CopiedOnHeapValueHolder<>(cacheItem, creationTime, lastAccessTime, expirationTime);
    }

    private static CacheItem processCacheItem(Instance instance){
        // org.hibernate.cache.internal.QueryResultsCacheImpl.CacheItem
        Objects.requireNonNull(instance);

        final var timestamp = (long) instance.getValueOfField("timestamp");
        final var resultsInstance = (Instance) instance.getValueOfField("results");
        List<Instance> results = HeapUtils.processArrayList(resultsInstance);

        return new CacheItem(timestamp, results);
    }
}
