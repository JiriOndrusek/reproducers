package jondruse.jbeap_13730;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/target")
public class ClientServlet extends HttpServlet {

    @EJB(lookup = "ejb:s02/TargetBeanStateful!jondruse.jbeap_13730.TargetBeanRemote?stateful")
    private TargetBeanRemote targetBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        try {
            Integer i = targetBean.incrementAndGet();
            resp.getOutputStream().print("executed: " + i);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
