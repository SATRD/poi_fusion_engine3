<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel='stylesheet' type='text/css' href="css/main.css">
  <link rel='STYLESHEET' type='text/css' href="css/admin.css">
</head>
<body bgcolor="white" style="{ margin:0;}" background="img/centerf2.png" style="{ background-repeat: no-repeat;}" >

<center>

  <table cellspacing="14"  >

   <tr height="200">
		<td>&nbsp;</td>
   </tr>

    <tr>
	  <td>
        <button type='button' class='td_button' onclick="location.href='admin/menu.jsp'">
	      <table>
		    <tr>
		      <td width='150px' align='center'>
			    <img src="img/admin/admin_ico.png"/>
			  </td>
			  <td align='center'>
			    <h1>Admin</h1>
			    <h2>Manage OCDs</h2>
			  </td>
		    </tr>
		  </table>
		</button>
      </td>
	  <td>
        <button type='button' class='td_button' onclick="location.href='doc/doc.html'">
          <table>
		    <tr>
		      <td width='150px' align='center'>
			    <img width='85px' src="img/admin/doc_icon.png"/>
			  </td>
			  <td align='center'>
			    <h1>Docs</h1>
			    <h2>See documentation</h2>
			  </td>
		    </tr>
		  </table>
		</button>
	  </td>
	</tr>
	
	<tr>
		<td>
        <button type='button' class='td_button' onclick="location.href='citysdk/test/demo1/index.html'">
	      <table>
		    <tr>
		      <td width='150px' align='center'>
			    <img src="img/admin/maps-icon.png"/>
			  </td>
			  <td align='center'>
			    <h1>Demo</h1>
			    <h2>Show FE POIs (direct access)</h2>
			  </td>
		    </tr>
		  </table>
		</button>
      </td>
      
      <td>
        <button type='button' class='td_button' onclick="location.href='citysdk/swagger/'">
          <table>
		    <tr>
		      <td width='150px' align='center'>
			    <img width='85px' src="img/admin/swagger-logo.png"/>
			  </td>
			  <td align='center'>
			    <h1>Swagger</h1>
			    <h2>Show FE inquiry API (citySDK format)</h2>
			  </td>
		    </tr>
		  </table>
		</button>
	  </td>
      
	</tr>
	

   

   
  </table>

</center>

</body>
</html>