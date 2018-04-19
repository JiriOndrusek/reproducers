package example.ejb;

import org.jboss.ejb3.annotation.Clustered;

import javax.ejb.Remote;

@Remote
@Clustered
public interface WhoAmIBeanRemote {

    String whoAmI();
}
