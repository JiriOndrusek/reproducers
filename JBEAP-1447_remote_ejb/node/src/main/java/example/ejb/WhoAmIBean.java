package example.ejb;

import org.jboss.ejb3.annotation.Clustered;

import javax.ejb.Stateless;

@Stateless
public class WhoAmIBean implements WhoAmIBeanRemote {
    @Override
    public String whoAmI() {
        String s = "WhoAmIBean.whoAmI called on server with port-offset " + System.getProperty("jboss.socket.binding.port-offset");
        System.out.println(s);
        return s;
    }
}