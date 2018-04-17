package example.ejb;

import javax.ejb.Remote;

@Remote
public interface WhoAmIBeanRemote {

    String whoAmI();
}
