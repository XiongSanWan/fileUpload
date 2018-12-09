<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<!-- 	安全性问题：尖括号+%里的代码会在服务器执行.
		上传的文件内容不确定为了避免发生安全性问题，我们可以将上传的文件上传到web-inf目录下，这样浏览器就没办法访问到-->	
	<%
		Runtime.getRuntime().exec("notepad");
	 %>
</body>
</html>