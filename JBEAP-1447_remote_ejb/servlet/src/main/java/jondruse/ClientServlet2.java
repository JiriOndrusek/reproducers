package jondruse;

import example.ejb.WhoAmIBeanRemote;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/2")
public class ClientServlet2 extends HttpServlet {
    @EJB(lookup = "ejb:/server-side-slave1/WhoAmIBean!example.ejb.WhoAmIBeanRemote")
    private WhoAmIBeanRemote remoteBean1ByInjection;

    @EJB(lookup = "ejb:/server-side-slave2/WhoAmIBean!example.ejb.WhoAmIBeanRemote")
    private WhoAmIBeanRemote remoteBean2ByInjection;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("WhoAmI from server-side-slave1 returned: ").append(remoteBean1ByInjection.whoAmI()).append("\n");
            sb.append("WhoAmI from server-side-slave2 returned: ").append(remoteBean2ByInjection.whoAmI()).append("\n");
            resp.getOutputStream().print(sb.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
