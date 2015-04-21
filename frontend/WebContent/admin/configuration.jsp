<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


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
        <button type='button' class='td_button' onclick="location.href='list_licenses.jsp'">
	      <table>
		    <tr>
		      <td width='150px' align='center'>
			    <img src="../img/admin/license_icon.png"/>
			  </td>
			  <td align='center'>
			    <h1>Licenses</h1>
			    <h2>Add new licenses</h2>
			  </td>
		    </tr>
		  </table>
		</button>
      </td>
	  <td>
        <button type='button' class='td_button' onclick="location.href='list_cities.jsp'">
          <table>
		    <tr>
		      <td width='150px' align='center'>
			    <img width='75px' src="../img/admin/city_icon.png"/>
			  </td>
			  <td align='center'>
			    <h1>City</h1>
			    <h2>Add new cities</h2>
			  </td>
		    </tr>
		  </table>
		</button>
	  </td>
	</tr>
	<tr>
	  <td>
        <button type='button' class='td_button' onclick="location.href='list_apitypes.jsp'">
          <table>
		    <tr>
		      <td width='150px' align='center'>
			    <img width='85px' src="../img/admin/api_icon.png"/>
			  </td>
		      <td align='center'>
		        <h1>API</h1>
			    <h2>Add new API connectors</h2>
		      </td>
		    </tr>
	      </table>
	    </button>
	  </td>
      <td>
        <button type='button' class='td_button' onclick="location.href='persistence.jsp'">
          <table>
		    <tr>
		  	  <td width='150px' align='center'>
			    <img width='65px' src="../img/admin/persistence.png"/>
			  </td>
			  <td align='center'>
			    <h1>Persistence</h1>
			    <h2>Configure database settings</h2>
			  </td>
		    </tr>
		  </table>
		</button>
	  </td>
    </tr>
    
    <tr>
	  <td>
        <button type='button' class='td_button' onclick="location.href='list_sources.jsp'">
	      <table>
		    <tr>
		      <td width='150px' align='center'>
			    <img src='../img/admin/sources.png'/>
			  </td>
			  <td align='center'>
			    <h1>Data sources</h1>
			    <h2>Manage POI data sources</h2>
			  </td>
		    </tr>
		  </table>
		</button>
      </td>
      
      <td>
        <button type='button' class='td_button' onclick="location.href='list_categories.jsp'">
          <table>
		    <tr>
		      <td width='150px' align='center'>
			    <img width='85px' src="../img/admin/categories.png"/>
			  </td>
			  <td align='center'>
			    <h1>Categories</h1>
			    <h2>Manage POI categories</h2>
			  </td>
		    </tr>
		  </table>
		</button>
	  </td>
	</tr>
	  
      
    <tr  valign="bottom" align="right">
		<td colspan="2" align="center"> 
			<input type="button"  value="Return" OnClick="location.href='menu.jsp'" class="boton" style="width: 160px;">
			<!--  <img src="../img/back.png" onclick="location.href='../menu.jsp'" title="Back" onmouseover="this.src='../img/back_2.png'" onmouseout="this.src='../img/back.png'"/> -->  
		</td>
	</tr>


   

   
  </table>
<!-- </div> -->
</center>

</body>
</html>