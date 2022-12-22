<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page errorPage="error.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<style>
body {
	font-family: Arial, Helvetica, sans-serif;
}

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

* {
	box-sizing: border-box;
}

input[type=text] {
	width: 100%;
	padding: 12px;
	border: 1px solid #ccc;
	border-radius: 4px;
	box-sizing: border-box;
	margin-top: 6px;
	margin-bottom: 16px;
	resize: vertical;
}

input[type=submit] {
	background-color: #04AA6D;
	color: white;
	padding: 12px 20px;
	border: none;
	border-radius: 4px;
	cursor: pointer;
}

input[type=submit]:hover {
	background-color: #45a049;
}

.container {
	border-radius: 5px;
	margin-left: 40px;
	margin-right: 40px;
	background-color: #f2f2f2;
	padding: 20px;
}

.container1 {
	border-radius: 5px;
	margin-left: 40px;
	margin-right: 40px;
	padding: 20px;
}

table {
	font-family: arial, sans-serif;
	border-collapse: collapse;
	width: 100%;
	word-break: break-all;
	font-size: small;
}

td, th {
	border: 1px solid #dddddd;
	text-align: left;
	padding: 8px;
}
</style>
</head>
<body>

	<div class="navbar">
		<a class="active" href="/boeingCloudViewSearch/index"><i class="fa fa-fw fa-home"></i> Home</a>

	</div>

	<h3 style="text-align: center;">Find Multi-Occurrence for Work
		Plan</h3>
		
		<!-- Instance Title -->

	<div class="container">
		<form action="result" method="post">
			<label for="fname">Enter the Instance Title</label> <input
				type="text" id="fname" name="externalid"
				placeholder="Enter External title"> <label for="lname">Enter Reference Name </label> <input
				type="text" id="lname" name="reference"
				placeholder="Enter Reference name"> <input type="submit"
				value="Submit">
		</form>
	</div>

	<div class="container1">

		<table>
			<c:choose>
				<c:when test="${FinalData.size() > 0}">
					<tr>
						<th rowspan="2" style="text-align: center">Instance Title</th>
						<th rowspan="2" style="text-align: center">Reference Name</th>
						<th rowspan="2" style="text-align: center">Occurrence</th>
						<th colspan="2" style="text-align: center">Provided Instance Details</th>

					</tr>
					<tr>
						<th style="text-align: center">Physical Id</th>
						<th style="text-align: center">Instance Title</th>
					</tr>

					<tr>
						<td rowspan="${FinalData.size()}">${InstanceValue}</td>
						<td rowspan="${FinalData.size()}">${ReferenceValue}</td>
						<c:forEach items="${FinalData}" var="datafinal1">
							<c:set var="var1" value="${datafinal1.key}" />
							<c:set var="var2" value="<span style='background-color: yellow;'>${ReferenceValue}</span>" />
							<td>${fn:replace(var1, ReferenceValue,var2)}</td>
							<c:forEach items="${datafinal1.value}" var="datafinallist">
								<c:forEach items="${datafinallist}" var="datafinalmap">
									<c:choose>
										<c:when test="${datafinalmap.key=='physicalid'}">
											<td><c:out value="${datafinalmap.value}"></c:out></td>
										</c:when>
										<c:when test="${datafinalmap.key=='instance_externalid'}">
											<td><c:out value="${datafinalmap.value}"></c:out></td>
										</c:when>
									</c:choose>
								</c:forEach>
							</c:forEach>
					</tr>
					<tr>
						</c:forEach>
					</tr>
				</c:when>
			</c:choose>


		</table>

	</div>

</body>
</html>