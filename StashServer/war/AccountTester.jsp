<%@ page import="java.util.List, java.util.Iterator, java.util.UUID" %>
<%@ page import="com.gnaughty.stash.server.Error, com.gnaughty.stash.server.Account,com.gnaughty.stash.server.refdata.FirstName,com.gnaughty.stash.server.refdata.MiddleName,com.gnaughty.stash.server.refdata.LastName" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<script>
		function registerAccount()
		{
			var xmlhttp;
			var jsonRequest;
			var jsonResponse;
			var authKey;
			
			try{
			    document.getElementById("<%=Account.ID%>").innerHTML="";
			    document.getElementById("<%=Account.COINS%>").innerHTML="";
				if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
					xmlhttp=new XMLHttpRequest();
				}
				else{// code for IE6, IE5
					xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
				}
				xmlhttp.onreadystatechange=function(){
					if (xmlhttp.readyState==4 && xmlhttp.status==200){
						document.getElementById("jsonResponseString").innerHTML=xmlhttp.responseText;
						jsonResponse = JSON.parse(xmlhttp.responseText);
						if(jsonResponse != null && !jsonResponse.error){
						    document.getElementById("<%=Account.ID%>").innerHTML=jsonResponse.<%=Account.ID%>;
						    document.getElementById("<%=Account.COINS%>").innerHTML=jsonResponse.<%=Account.COINS%>;
						}
						document.getElementById("processingMsg").innerHTML="";
				    }
				}
				
				xmlhttp.open("POST","/account/register",true);
				xmlhttp.setRequestHeader("Content-type","application/json");
				authKey="<%=UUID.randomUUID().toString().replaceAll("-", "").toLowerCase()%>";
				document.getElementById("<%=Account.AUTHENTICATION_KEY%>").innerHTML=authKey;
				jsonRequest = "{\"<%=Account.AUTHENTICATION_KEY%>\":\""+authKey+"\","+
								"\"<%=Account.FIRST_NAME%>\":"+document.getElementById("<%=Account.FIRST_NAME%>").value+","+
								"\"<%=Account.MIDDLE_NAME%>\":"+document.getElementById("<%=Account.MIDDLE_NAME%>").value+","+
								"\"<%=Account.LAST_NAME%>\":"+document.getElementById("<%=Account.LAST_NAME%>").value+","+
								"\"<%=Account.EMAIL%>\":\""+document.getElementById("<%=Account.EMAIL%>").value+"\"}";
				document.getElementById("jsonRequestString").innerHTML=jsonRequest;
				document.getElementById("processingMsg").innerHTML="Processing ... please wait for response";
				xmlhttp.send(jsonRequest);
			}catch(e){
				alert(e);
			}
		}
	</script>
		<title>Account Tester</title>
	</head>
	<body>
		Mandatory attributes for creating a new Account are:<br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- First Name<br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Middle Name<br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Last Name<br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Email<br/>
		Authentication Key is generated automatically by the client<br/>
		<table border="1">
			<tr><td>Account ID: </td><td style="background-color:LightGray"><div id="<%=Account.ID%>"></div></td></tr>
			<tr><td>Auth Key: </td><td style="background-color:LightGray"><div id="<%=Account.AUTHENTICATION_KEY%>"></div></td></tr>
			<tr><td>First Name: </td><td><select id="<%=Account.FIRST_NAME%>" style="width:300px">
			<%
				List<FirstName> firstNames = FirstName.list();
				Iterator<FirstName> firstNamesIterator = firstNames.iterator();
				while(firstNamesIterator.hasNext()){
					FirstName currentFirstName = firstNamesIterator.next();
					%>
										 	<option value="<%=currentFirstName.getId().toString()%>"><%=currentFirstName.getText()%></option><%		
				}
			%>	 										</select></td></tr>
			<tr><td>Middle Name: </td><td><select id="<%=Account.MIDDLE_NAME%>" style="width:300px">
			<%
				List<MiddleName> middleNames = MiddleName.list();
				Iterator<MiddleName> middleNamesIterator = middleNames.iterator();
				
				while(middleNamesIterator.hasNext()){
					MiddleName currentMiddleName = middleNamesIterator.next();
					%>
										 	<option value="<%=currentMiddleName.getId()%>"><%=currentMiddleName.getText()%></option><%		
				}
			%>  										</select></td></tr>
			<tr><td>Last Name: </td><td><select id="<%=Account.LAST_NAME%>" style="width:300px">
			<%
				List<LastName> lastNames = LastName.list();
				Iterator<LastName> lastNamesIterator = lastNames.iterator();
				
				while(lastNamesIterator.hasNext()){
					LastName currentLastName = lastNamesIterator.next();
					%>
										 	<option value="<%=currentLastName.getId()%>"><%=currentLastName.getText()%></option><%		
				}
			%>  										</select></td></tr>
			<tr><td>Email: </td><td style="width:300px"><input type="text" id="<%=Account.EMAIL%>" style="width:300px"></td></tr>
			<tr><td>Coins: </td><td style="background-color:LightGray"><div id="<%=Account.COINS%>"></div></td></tr>
		</table>
		<br/>
		<table>
			<tr><td><button type="button" onclick="registerAccount()">Register New Account</button></td><td><div id="processingMsg" style="color:#0000FF"></div></td></tr>
		</table>
		<br/>
		<table border="1">
			<tr><td>JSON Request</td><td style="width:800px; background-color:LightGray"><div id="jsonRequestString"></div>
			<tr><td>JSON Response</td><td style="width:800px; background-color:LightGray"><div id="jsonResponseString"></div>
		</table>
	</body>
</html>