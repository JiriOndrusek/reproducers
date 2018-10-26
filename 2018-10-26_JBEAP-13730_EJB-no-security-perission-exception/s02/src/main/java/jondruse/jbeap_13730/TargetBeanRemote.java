package jondruse.jbeap_13730;

import javax.ejb.Remote;

@Remote
public interface TargetBeanRemote {

    Integer incrementAndGet();

}
