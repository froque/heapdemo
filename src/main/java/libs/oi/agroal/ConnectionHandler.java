package libs.oi.agroal;

import org.netbeans.lib.profiler.heap.Instance;

public record ConnectionHandler(long lastAccess, boolean enlisted, State state) {

    static ConnectionHandler fromInstance(Instance instance){
        final var lastAccess = (long) instance.getValueOfField("lastAccess");
        final var enlisted = (boolean) instance.getValueOfField("enlisted");
        final var state = State.fromInstance((Instance) instance.getValueOfField("state"));
        return new ConnectionHandler(lastAccess, enlisted, state);
    }
}
