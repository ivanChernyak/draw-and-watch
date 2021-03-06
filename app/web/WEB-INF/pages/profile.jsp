<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="services.SimpleUser" %>
<%@ page import="services.SimplePicture" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User loggedUser = (User) request.getAttribute("loggedUser");
    SimpleUser userProfile = (SimpleUser) request.getAttribute("userProfile");
%>
<html>
<head>
    <meta charset="utf-8">
    <title>Profile</title>
    <style><%@include file="styles/commonStyle.css"%></style>
    <style><%@include file="styles/profileStyle.css"%></style>
</head>
<body>
<div class="header">
    <h1>Draw and watch</h1>
    <table>
        <tr>
            <td class="path">
                <a href="/app/home">home</a>/
                <a href="/app/profile?login=<%=userProfile.getLogin()%>">profile</a>/
            </td>
            <td class="account">
                <% if (loggedUser != null) { %>
                <a href="/app/profile?login=<%=loggedUser.getLogin()%>">
                    <%=loggedUser.getName()%>
                </a>
                <% } %>
                <a href="/app/canvas">Paint now</a>
                <% if (loggedUser != null) { %>
                <a href="/app/logout">Logout</a>
                <% } else { %>
                <a href="/app/signin">Login</a>
                <% } %>
            </td>
        </tr>
    </table>
</div>
<div class="content">
    <h3>Profile</h3>
    <hr>
    <div class="profile noBorder">
        <div>
            <table>
                <tr>
                    <td><img src="static/profilePhotos/photo.png"></td>
                    <% if (loggedUser == null || !userProfile.getLogin().equals(loggedUser.getLogin())) { %>
                    <td>
                        <a href="addToFavourites?login=<%=userProfile.getLogin()%>">
                            <% if ((boolean) request.getAttribute("isProfileInFavourites")) { %>
                            <input type="button" value="Delete from favourites">
                            <% } else { %>
                            <input type="button" value="Add to favourites">
                            <% } %>
                        </a>
                    </td>
                    <% } %>
                </tr>
                <tr class="username"><td><%=userProfile.getName()%></td></tr>
            </table>
        </div>
    </div>
    <% List<SimplePicture> pictures = (List<SimplePicture>) request.getAttribute("userPictures");
    if (pictures.size() != 0) { %>
    <h3>Pictures</h3>
    <hr>
    <div class="profile">
        <% for (SimplePicture picture : pictures) { %>
        <div>
            <a href="/app/picture?id=<%=picture.getId()%>">
                <p><img src="<%=picture.getContent()%>"><p>
            </a>
        </div>
        <% } %>
    </div>
    <% } %>
</div>
</body>
</html>
</html>
