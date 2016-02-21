<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    request.setAttribute("user", user);
%>

${user.email}<br/>
${user.nickname}<br/>
${user.userId}<br/>