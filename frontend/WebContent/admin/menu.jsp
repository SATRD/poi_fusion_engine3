<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel='stylesheet' type='text/css' href="../css/main.css">
  <link rel='STYLESHEET' type='text/css' href="../css/admin.css">
</head>
<body bgcolor="white" style="{ margin:0;}" background="../img/centerf2.png" style="{ background-repeat: no-repeat;}" >

<center>
<!-- <div class='main_content'> -->
  <table cellspacing="14"  >

   <tr height="160">
		<td>&nbsp;</td>
   </tr>
    
    
	
	
	<tr>
	  <td>
        <button type='button' class='td_button' onclick="location.href='list_ocds.jsp'">
          <table>
		    <tr>
		      <td width='150px' align='center'>
			    <img width='110px' src="../img/admin/ocd.png"/>
			  </td>
		      <td align='center'>
		        <h1>OCDs</h1>
			    <h2>List and define new OCDs</h2>
		      </td>
		    </tr>
	      </table>
	    </button>
	  </td>
      <td>
        <button type='button' class='td_button' onclick="location.href='configuration.jsp'"> 
        
          <table>
		    <tr>
		  	  <td width='150px' align='center'>
			    <img src="../img/admin/config_ico.png"/>
			  </td>
			  <td align='center'>
			    <h1>Configuration</h1>
			    <h2>Change system properties</h2>
			  </td>
		    </tr>
		  </table>
		</button>
	  </td>
    </tr>
    <tr  valign="bottom" align="right">
		<td colspan="2" align="center"> 
			<input type="button"  value="Return" OnClick="location.href='../menu.jsp'" class="boton" style="width: 160px;">
			
		</td>
	</tr>


   

   
  </table>

</center>

</body>
</html>