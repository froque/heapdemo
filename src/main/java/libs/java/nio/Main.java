package libs.java.nio;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.HeapFactory;
import org.netbeans.lib.profiler.heap.Instance;
import org.netbeans.lib.profiler.heap.JavaClass;

public class Main {

    public static void main(String[] args) throws IOException {
        Heap heap = HeapFactory.createHeap(new File("dumps/bytebuffer.hprof"));

        final JavaClass javaClassByName = heap.getJavaClassByName("java.nio.DirectByteBuffer");
        System.out.println(javaClassByName.getInstancesCount());
        final List<Instance> instances = javaClassByName.getInstances();

        final List<DirectByteBuffer> bytebuffers = instances.stream().map(DirectByteBuffer::fromInstance).toList();

        final Integer sum = bytebuffers.stream().map(DirectByteBuffer::capacity).reduce(0, Integer::sum);
        System.out.println(sum);
        // 185 238 350
    }
}
