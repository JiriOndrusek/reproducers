package jondruse.jbeap_13730;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class IntermediaryBeanStateless implements IntermediaryBeanRemote {
    @EJB(lookup = "ejb:/bean-target/TargetBeanStateful!org.jboss.qa.ejb.tests.jbeap10217.beans.TargetBeanRemote?stateful")
    private TargetBeanRemote remote;

    @Override
    public void call() {
        System.out.println("IntermediaryBeanStateless invoked on node " + System.getProperty("jboss.node.name"));
        remote.incrementAndGet();
    }

}
