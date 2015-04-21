<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.list_ocds");%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel='stylesheet' type='text/css' href="../css/main.css">
  <link rel='stylesheet' type='text/css' href="../css/table.css"> 
  <script type='text/javascript' language='javascript'>

    function edit(id){
      if (id == 0){
        location.href='new_ocd.jsp';
      }else{
        location.href='edit_ocd.jsp?id_selected='+id;
      }
    }
    
    //same as edit but one cannot change anything. Just for watching purposes
    function view(id){        
          location.href='view_ocd.jsp?id_selected='+id;        
    }
    

    function del(id,name){
      if (confirm('Dou you really want to delete "'+name+'" permanently?')){
    	  location.href='delete_ocd.jsp?id_selected='+id;
	  }
    }
    
    function start_fusion(id){
        if (confirm('Dou you really want to start fusion?')){
        	location.href='start_fusion_ocd.jsp?id_selected='+id;
  	  	}
     }
    
    function initialize(id){
    	if (confirm('Dou you really want to initialize the OCD?')){
        	location.href='initialize_ocd.jsp?id_selected='+id;
  	  	}
    }

  </script>

</head>
<body bgcolor="white" style="{ margin:0;}" background="../img/centerf2.png" style="{ background-repeat: no-repeat;}">

  
  <%    
  	String relativeWebPath = "/config/config.xml";
	String configPath = getServletContext().getRealPath(relativeWebPath);
	Configuration configFile = new Configuration(configPath);
		
	Connection con = OutputDB.connectDB(
			configFile.getConnectionString(),
			configFile.getUser(),
			configFile.getPwd(),
			configFile.getDriverName(),null);
	
	
    
    String state = "";
    
        
    //Get data
    ArrayList<OCD> ocds = OCD.getOCDList(con,null); 
    int num = ocds.size();
    //System.out.println(num); 
  
  %>

  <center>
  <div class='main_content'> 
    <font class='titulo'>OCDs</font>
    
    
    <table class='' style='margin: 20px 0 10px 0;' cellspacing='0' border='1'>
      <thead style='display:block;'>
        <tr>
          <th width='200'>Name</th>
          <th width='300'>Description</th>
          <th width='63'>Status</th>
          <th width='80'>&nbsp;</th>
        </tr>
      </thead>
      <tbody  style='display:block; <% if (num >14) { %> height:500px; overflow-y:scroll;<% } %>'>


<% 
    for(int j = 0; j < num; j = j+1) {
    	OCD ocd = ocds.get(j);
    	
%>
     
        <% if (j%2 !=0 ){ %>
        	<tr class="odd">
        <% }else{ %>
        	<tr> 
        <%} %>                          
          <td width='200'><%=ocd.getName()%></td>
          <%
             String description;
             if (ocd.getDescription() == null) description=""; else description=ocd.getDescription();
          %>
          <td width='300'><%=description%></td>
          
          <td width='63' align='center'>
            <% 
            String status = ocd.getStatus();
               if (status.equalsIgnoreCase(OCD.STATUS_NEW)) {
             %>
              <img class="img" src="../img/status_new.png" OnClick="initialize(<%=ocd.getId().toString()%>)" width="20px" title="OCD newly created. Press Click to initialize" />
            <% 
               } 
               if (status.equalsIgnoreCase(OCD.STATUS_INITIALIZED)) {
            %>
              <img class="img" src="../img/status_init.png" OnClick="start_fusion(<%=ocd.getId().toString()%>)" width="20px" title="OCD initialized. Press Click to start fusion" />
            <% 
               } 
               if ( (status.equalsIgnoreCase(OCD.STATUS_RUNNING)) || (status.equalsIgnoreCase(OCD.STATUS_RUNNING_START))  ) {
            %>
              <img class="img" src="../img/status_run.gif" width="20px" title="Fusion is being executed.." />
            <% 
               } 
               if ( (status.equalsIgnoreCase(OCD.STATUS_RUNNING_END_OK)) || (status.equalsIgnoreCase(OCD.STATUS_FINISHED_OK))  ) {
            %>
              <img class="img" src="../img/status_finished.png" width="20px" title="OCD terminated successfully. You can now test the OCD" />
            <% 
               } 
               if ( (status.equalsIgnoreCase(OCD.STATUS_RUNNING_END_ERR)) || (status.equalsIgnoreCase(OCD.STATUS_FINISHED_ERR))  ) {
            %>
              <img class="img" src="../img/status_error.png" width="20px" title="OCD terminated with error. Please check the log for further details" />
            <% 
               } 
               
            %>
            
          </td>
          
          
          <td width='<% if (num >14) {%>50<%}else{%>80<%}%>>' align='center'>
            
            <% if (status.equalsIgnoreCase(OCD.STATUS_NEW)) { %>
              <img class='img' src='../img/edit.png' OnClick="edit(<%=ocd.getId().toString()%>)" width='20px' title='Edit OCD' />
            <% }else{ %>
            <img class='img' src='../img/edit_no.png' OnClick="view(<%=ocd.getId().toString()%>)" width='20px' title='View OCD' />            
            <% } %>
            
            <% if ( (status.equalsIgnoreCase(OCD.STATUS_FINISHED_ERR)) || (status.equalsIgnoreCase(OCD.STATUS_FINISHED_OK))  ) { %>
              <img class="img" src="../img/restart_fusion.png" OnClick="start_fusion(<%=ocd.getId().toString()%>)" width="20px" title="Press Click to restart fusion" />
            <% } %>
            
            <% if ( (!status.equalsIgnoreCase(OCD.STATUS_RUNNING)) ||  (!status.equalsIgnoreCase(OCD.STATUS_RUNNING_START)) ){ %>
              <img class='img' src='../img/delete.png' OnClick="del('<%=ocd.getId().toString()%>','<%=ocd.getName()%>')" width='20px' title='Delete OCD' />
            <% }else{ %>
            <img class='img' src='../img/delete_no.png' width='20px' title='Delete OCD. Not available' />           
            <% } %>
            
            
          </td>
        </tr>

<%
   }
%> 
      </tbody>
      <tfoot>
        <tr><td colspan='6' align='right'><img class='img' src='../img/new.png' OnClick='edit(0)' width='20px' title='New OCD' />&nbsp;&nbsp;&nbsp;</td></tr>
      </tfoot>
    </table>
    <br>
    <input type="button"  value="Return" OnClick="location.href='menu.jsp'" class="boton" style="width: 160px;">
    <input type="button"  value="Refresh" OnClick="location.reload(true)" class="boton" style="width: 160px;">

  </div> 
  </center>

 <%  OutputDB.disconnectDB(con,null); %>
  

</body>
</html>