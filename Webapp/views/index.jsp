<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
		<a class="active" href="/"><i class="fa fa-fw fa-home"></i>
			Exalead Dassault Systemes</a> <a href="/index"><i
			class="fa fa-fw fa-info-circle"></i> Occurrence</a>
		<!-- <a href="/view">
			<i class="fa fa-fw fa-cubes"></i> Search value
		</a> -->

	</div>

	<h3 style="text-align: center;">Find Occurrence for Work Plan</h3>

	<div class="container">
		<form action="index" method="post">
			<label for="fname">Enter Instance Value</label> <input type="text"
				id="fname" name="child" placeholder="enter instance value..."
				value="Loading OP_4_BOE_01.1"> <label for="lname">Enter
				Reference Value</label> <input type="text" id="lname" name="parent"
				placeholder="enter reference value..." value="wpl-17466195-00000007">
			<input type="submit" value="Submit">
		</form>
	</div>

	<div class="container1">

		<table>
			<c:choose>
				<c:when test="${FinalData.size() > 0}">
					<tr>
						<th>Instance Value</th>
						<th>Reference Value</th>
						<th>Occurrence</th>
						<th>Physical ID</th>
						<th>Instance Externalid</th>
					</tr>

					<tr>
						<td rowspan="${FinalData.size()}">${InstanceValue}</td>
						<td rowspan="${FinalData.size()}">${ReferenceValue}</td>

						<c:forEach items="${FinalData}" var="datafinal1">
							<td><c:out value="${datafinal1.key}"></c:out></td>
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