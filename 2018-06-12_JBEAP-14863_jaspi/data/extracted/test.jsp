<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="javax.naming.*,javax.sql.*,java.sql.*,java.io.*,java.util.*"
    session="false"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Datasource Test</title>
</head>
<body>

 <body>
<%=request.getParameter("jndiName")%>
  <%

    InputStream is = request.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line = null;
    System.out.println("-B---");
    while ((line = reader.readLine()) != null) {
      System.out.println("-r---");
      out.write(line + "</br>");
      System.out.println(line);
    }
    System.out.println("-E---");
    
    /**
    byte b[]=new byte[2048];
    int a = 0;
    int i = 0;
    while( (a = is.read() ) > 0) {
      b[i++] = (byte)a;
    }
    
    String s = new String(b, 0, i);
    out.write(s);
    System.out.println(s);
   **/

  %>
  <h1>Test an EAP Datasource</h1>
  <form method="post">
    <table>
      <tr>
        <td>JNDI Name of Datasource:</td>
        <td><input type="text" width="60" name="jndiName"/></td>
      </tr>
      <tr>
        <td>Table Name to Query (optional):</td>
        <td><input type="text" width="60" name="tableName"/></td>
      </tr>
      <tr>
        <td colspan="2" align="center"><input type="submit" value="Submit" name="submit"/></td>
      </tr>
    </table>
  </form>
  </body>  
</html>
