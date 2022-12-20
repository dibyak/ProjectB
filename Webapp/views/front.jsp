<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<style>
body {font-family: Arial, Helvetica, sans-serif;}

.navbar {
  width: 100%;
  background-color: #555;
  overflow: auto;
}

.navbar a {
  float: left;
  padding: 12px;
  color: white;
  text-decoration: none;
  font-size: 17px;
}

.navbar a:hover {
  background-color: #000;
}

.active {
  background-color: #04AA6D;
}

</style>
</head>
<body>

<div class="navbar">
  <a class="active" href="/"><i class="fa fa-fw fa-home"></i> Home</a> 
  <a href="/index"><i class="fa fa-fw fa-info-circle"></i> Occurrence</a> 
 <!--  <a href="/view"><i class="fa fa-fw fa-cubes"></i> Search value</a>  -->
 
</div>

</body>
</html>