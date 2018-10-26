package jondruse.jbeap_13730;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import java.util.concurrent.atomic.AtomicInteger;

@Stateful
public class TargetBeanStateful implements TargetBeanRemote {

    private AtomicInteger counter;

    @PostConstruct
    public void init() {
        counter = new AtomicInteger(0);
    }

    @Override
    public Integer incrementAndGet() {
        final int result = counter.incrementAndGet();
        System.out.println("*** TargetBeanStateful invoked on node " + System.getProperty("jboss.node.name")
                + ", counter = " + result);
        return result;
    }

}
