<%@ page import="java.util.*" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8" %>
<%
out.print("{ \"result\": \"" + request.getAttribute("result") + "\" }");
%>
