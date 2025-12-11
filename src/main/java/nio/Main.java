package nio;

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
        for (Instance instance : instances) {
            //HeapUtils.dump(instance);
            final var capacity = (int) instance.getValueOfField("capacity");
            System.out.println(new DirectByteBuffer(capacity));
        }
    }

}
