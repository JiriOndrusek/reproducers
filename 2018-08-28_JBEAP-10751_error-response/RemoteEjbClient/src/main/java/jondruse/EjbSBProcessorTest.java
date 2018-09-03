package jondruse;

import jondruse.smartFrog.StatefulSBProcessorFactoryImpl;
import junit.framework.TestCase;
import org.junit.Test;


public class EjbSBProcessorTest extends TestCase {

    @Test
    public void test() throws Exception{
        //write
        StatefulSBProcessorFactoryImpl.test("8180", "RemoteStatefulSBImpl", 1, true);
        //max 5 reads
        StatefulSBProcessorFactoryImpl.test("8180", "RemoteStatefulSBImpl", (int)(Math.random()*5), false);
        //write
        StatefulSBProcessorFactoryImpl.test("8180", "RemoteStatefulSBImpl", 1, true);
        //max 5 reads
        StatefulSBProcessorFactoryImpl.test("8180", "RemoteStatefulSBImpl", (int)(Math.random()*5), false);
        //write
        StatefulSBProcessorFactoryImpl.test("8180", "RemoteStatefulSBImpl", 1, true);
    }
}
