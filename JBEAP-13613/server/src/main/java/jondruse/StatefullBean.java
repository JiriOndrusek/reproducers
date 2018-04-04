package jondruse;

import javax.ejb.Singleton;

@Singleton
public class StatefullBean {

    int counter = 0;

    public int add() {
        return counter++;
    }
}
