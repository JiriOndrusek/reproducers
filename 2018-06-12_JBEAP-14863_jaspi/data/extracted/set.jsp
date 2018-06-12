<%
java.util.Map map = new java.util.HashMap();
java.util.Map smap = java.util.Collections.synchronizedMap(map);
smap.put("fuga", "moga");
session.setAttribute("hoge", smap);
%>

