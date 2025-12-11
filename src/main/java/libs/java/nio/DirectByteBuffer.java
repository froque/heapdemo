package libs.java.nio;

import org.netbeans.lib.profiler.heap.Instance;

public record DirectByteBuffer(int capacity) {

    static DirectByteBuffer fromInstance(Instance instance){
        final var capacity = (int) instance.getValueOfField("capacity");
        return new DirectByteBuffer(capacity);
    }

}
