package libs.oi.agroal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.HeapFactory;
import org.netbeans.lib.profiler.heap.Instance;
import org.netbeans.lib.profiler.heap.JavaClass;

public class Main {

    public static void main(String[] args) throws IOException {
        Heap heap = HeapFactory.createHeap(new File("dumps/agroal.hprof"));

        final JavaClass javaClassByName = heap.getJavaClassByName("io.agroal.pool.ConnectionHandler");
        System.out.println(javaClassByName.getInstancesCount());
        final List<Instance> instances = javaClassByName.getInstances();

        final List<ConnectionHandler> bytebuffers = instances.stream().map(ConnectionHandler::fromInstance).toList();

        bytebuffers.forEach(System.out::println);
    }
}

