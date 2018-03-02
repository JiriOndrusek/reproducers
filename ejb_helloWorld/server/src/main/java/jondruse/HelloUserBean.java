package jondruse;

import javax.ejb.Stateless;

@Stateless
public class HelloUserBean { //implements HelloUser {
    public String sayHello(String name) {
        return String.format("Hello %s!", name);
    }
}
