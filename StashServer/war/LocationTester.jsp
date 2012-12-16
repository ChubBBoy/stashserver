<%@ page import="com.gnaughty.stash.server.Account, com.gnaughty.stash.server.Location" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<script>
		function updateLocation()
		{
			var xmlhttp;
			var jsonRequest;
			var jsonResponse;
			
			try{
			    document.getElementById("<%=Location.COINS%>").innerHTML="";
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
						    document.getElementById("<%=Location.COINS%>").innerHTML=jsonResponse.<%=Location.COINS%>;
						}
						document.getElementById("processingMsg").innerHTML="";
				    }
				}
				
				xmlhttp.open("POST","/account/updateLocation",true);
				xmlhttp.setRequestHeader("Content-type","application/json");
				jsonRequest = "{\"<%=Account.ID%>\":\""+document.getElementById("<%=Account.ID%>").value+"\","+
								"\"<%=Account.AUTHENTICATION_KEY%>\":\""+document.getElementById("<%=Account.AUTHENTICATION_KEY%>").value+"\","+
								"\"<%=Location.LONGITUDE%>\":"+document.getElementById("<%=Location.LONGITUDE%>").value+","+
								"\"<%=Location.LATITUDE%>\":"+document.getElementById("<%=Location.LATITUDE%>").value+","+
								"\"<%=Location.RADIUS%>\":"+document.getElementById("<%=Location.RADIUS%>").value+","+
								"\"<%=Location.TIMEOUT%>\":"+document.getElementById("<%=Location.TIMEOUT%>").value+"}";
				document.getElementById("jsonRequestString").innerHTML=jsonRequest;
				document.getElementById("processingMsg").innerHTML="Processing ... please wait for response";
				xmlhttp.send(jsonRequest);
			}catch(e){
				alert(e);
			}
		}
	</script>
		<title>Location Tester</title>
	</head>
	<body>
		Mandatory attributes for creating a new Location are:<br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Account ID<br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Auth Key<br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Longitude<br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Latitude<br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Radius<br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Timeout (in milliseconds)<br/>
		<table border="1">
			<tr><td>Account ID: </td><td style="width:300px"><input type="text" id="<%=Account.ID%>" style="width:300px"></td></tr>
			<tr><td>Auth Key: </td><td style="width:300px"><input type="text" id="<%=Account.AUTHENTICATION_KEY%>" style="width:300px"></td></tr>
			<tr><td>Longitude: </td><td style="width:300px"><input type="text" id="<%=Location.LONGITUDE%>" style="width:300px"></td></tr>
			<tr><td>Latitude: </td><td style="width:300px"><input type="text" id="<%=Location.LATITUDE%>" style="width:300px"></td></tr>
			<tr><td>Radius: </td><td style="width:300px"><input type="text" id="<%=Location.RADIUS%>" style="width:300px"></td></tr>
			<tr><td>Timeout (millis): </td><td style="width:300px"><input type="text" id="<%=Location.TIMEOUT%>" style="width:300px"></td></tr>
			<tr><td>Coins: </td><td style="background-color:LightGray"><div id="<%=Location.COINS%>"></div></td></tr>
		</table>
		<br/>
		<table>
			<tr><td><button type="button" onclick="updateLocation()">Update Location</button></td><td><div id="processingMsg" style="color:#0000FF"></div></td></tr>
		</table>
		<br/>
		<table border="1">
			<tr><td>JSON Request</td><td style="width:800px; background-color:LightGray"><div id="jsonRequestString"></div>
			<tr><td>JSON Response</td><td style="width:800px; background-color:LightGray"><div id="jsonResponseString"></div>
		</table>
	</body>
</html>