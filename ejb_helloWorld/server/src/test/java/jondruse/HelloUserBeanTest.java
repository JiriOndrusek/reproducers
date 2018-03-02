package jondruse;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class HelloUserBeanTest {

    @EJB
    private HelloUserBean userBean;

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "foo.jar")
                .addClasses(HelloUserBean.class);
    }

    @Test
    public void testSayHello() {
        String helloMsg = userBean.sayHello("test");
        Assert.assertEquals("Hello test!", helloMsg);
    }


}
