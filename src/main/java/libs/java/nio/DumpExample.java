package libs.java.nio;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;

import javax.management.MBeanServer;

import com.sun.management.HotSpotDiagnosticMXBean;

public class DumpExample {

    public static void main(String[] args) throws IOException, InterruptedException {
        final ByteBuffer ignored = ByteBuffer.allocateDirect(123_456);

        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
                server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
        mxBean.dumpHeap("dumps/bytebuffer.hprof", true);

        Thread.sleep(1_000);
    }
}
