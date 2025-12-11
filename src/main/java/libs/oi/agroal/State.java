package libs.oi.agroal;

import net.cuprak.demo.heap.HeapUtils;
import org.netbeans.lib.profiler.heap.Instance;

public record State(String name, int ordinal) {

    static State fromInstance(Instance instance){
        final var name = HeapUtils.processString((Instance) instance.getValueOfField("name"));
        final var ordinal = (int) instance.getValueOfField("ordinal");
        return new State(name, ordinal);
    }
}
