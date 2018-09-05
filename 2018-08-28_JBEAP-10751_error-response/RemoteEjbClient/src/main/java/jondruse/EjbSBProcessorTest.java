package jondruse;

import jondruse.smartFrog.StatefulSBProcessorFactoryImpl;
import junit.framework.TestCase;
import org.junit.Test;


public class EjbSBProcessorTest extends TestCase {

    @Test
    public void test() throws Exception{
        //write
        StatefulSBProcessorFactoryImpl.test("8380", "ForwardingStatefulSBImpl", 1, true);
//        //max 5 reads
//        StatefulSBProcessorFactoryImpl.test("8380", "ForwardingStatefulSBImpl", (int)(Math.random()*5), false);
//        //write
//        StatefulSBProcessorFactoryImpl.test("8380", "ForwardingStatefulSBImpl", 1, true);
//        //max 5 reads
//        StatefulSBProcessorFactoryImpl.test("8380", "ForwardingStatefulSBImpl", (int)(Math.random()*5), false);
//        //write
//        StatefulSBProcessorFactoryImpl.test("8380", "ForwardingStatefulSBImpl", 1, true);
    }
}
