import net.cuprak.demo.heap.HeapUtils;
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
        Map<String, Integer> stringIntegerMap = new HashMap<>();
        Map<String, Integer> typesCounter = new HashMap<>();
        for (Map.Entry<Instance, Instance> entry : realMap.entrySet()) {
            if (entry.getKey() != null){

                typesCounter.merge(entry.getKey().getJavaClass().getName(), 1, Integer::sum);

                if (entry.getKey().getJavaClass().getName().equals("org.hibernate.cache.spi.QueryKey")) {
                    String s = processQueryKey(entry.getKey());
                    stringIntegerMap.merge(s, 1, Integer::sum);
                }
            }
        }
        System.out.println(typesCounter);
        System.out.println(stringIntegerMap);
    }
    private static String processQueryKey(Instance queryKey){
        // org.hibernate.cache.spi.QueryKey
        Objects.requireNonNull(queryKey);

        Instance sqlQueryString = (Instance) queryKey.getValueOfField("sqlQueryString");
        return HeapUtils.processString(sqlQueryString);
    }
}
