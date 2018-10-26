package jondruse.jbeap_13730;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/call")
public class ClientServlet extends HttpServlet {
    @EJB(lookup = "ejb:s01/IntermediaryBeanStateless!jondruse.jbeap_13730.IntermediaryBeanRemote")
    private IntermediaryBeanRemote intermediaryBeanRemote;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        try {
            intermediaryBeanRemote.call();
            resp.getOutputStream().print("executed, see log");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
