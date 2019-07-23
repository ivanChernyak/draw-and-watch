<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Home</title>
    <style><%@include file="styles/commonStyle.css"%></style>
    <style><%@include file="styles/homeStyle.css"%></style>
</head>
<body>
<div class="header">
    <h1>Header</h1>
    <table>
        <tr>
            <td class="path"><a href="#">home</a>/</td>
            <td class="account">
                <a href="#">
                    <% if (request.getSession().getAttribute("loggedUser") != null) { %>
                    <%=((User) request.getSession().getAttribute("loggedUser")).getName()%>
                    <% } %>
                </a>
                <a href="/app/<%= request.getSession().getAttribute("loggedUser") != null ? "canvas" : "signin"%>">Paint now</a>
                <% if (request.getSession().getAttribute("loggedUser") != null) { %>
                <a href="/app/logout">Logout</a>
                <% } else { %>
                <a href="/app/signin">Login</a>
                <% } %>
            </td>
        </tr>
    </table>
</div>
<div class="content">
    <h3>Favourite profiles</h3>
    <hr>
    <div class="profile">
        <div>
            <a href="#">
                <p><img src="static/profilePhotos/photo.png"/><p>
                Username
            </a>
        </div>
        <div>
            <a href="#">
                <p><img src="static/profilePhotos/photo.png"><p>
                Username
            </a>
        </div>
        <div>
            <a href="#">
                <p><img src="static/profilePhotos/photo.png"><p>
                Username
            </a>
        </div>
    </div>
    <h3>All profiles</h3>
    <hr>
    <div class="profile">
        <%  List<User> users = (List<User>) request.getAttribute("users");
            if (users != null) {
                for (User user: (List<User>) request.getAttribute("users")) { %>
        <div>
            <a href="#">
                <p><img src="static/profilePhotos/photo.png"><p>
                <%= user.getName() %>
            </a>
        </div>
        <% }} %>
    </div>
</div>
</body>
</html>