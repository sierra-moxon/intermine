<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>

<!-- bagBuild.jsp -->
<html:xhtml/>

<script language="javascript">
<!--//<![CDATA[
  
   function switchInputs(open, close) {
      document.getElementById(open + 'Input').disabled = false;
      document.getElementById(open + 'Submit').disabled = false;
      document.getElementById(open + 'Toggle').src = 'images/disclosed.gif';
      
      document.getElementById(close + 'Input').disabled = true;
      document.getElementById(close + 'Submit').disabled = true;
      document.getElementById(close + 'Toggle').src = 'images/undisclosed.gif';
   }
//]]>-->
</script>

<h2>Create a new bag <im:manualLink section="manualMakingBags.shtml#manualNewBag"/> </h2>
<div class="bagBuild">
  <html:form action="/buildBag" focus="text" method="post" enctype="multipart/form-data">
    <p>
      <fmt:message key="bagBuild.bagFromText1"/>
      <p/>
      <fmt:message key="bagBuild.bagFromText2"/>
      <br/>
      <fmt:message key="bagBuild.bagType"/>
      <html:select property="type">
    	<c:forEach items="${preferedTypeList}" var="type">
         <html:option value="${type}" style="font-weight:bold">${type}</html:option>
    	</c:forEach>
        <html:option value="" style="text-align:center">----------------</html:option>
      	<c:forEach items="${typeList}" var="type">
           <html:option value="${type}">${type}</html:option>
      	</c:forEach>
      </html:select>
      </p>
      <h4>     
        <a href="javascript:switchInputs('paste', 'file');">
          <img id='pasteToggle' src="images/disclosed.gif"/>
          <fmt:message key="bagBuild.bagPaste"/>
        </a>
      </h4>
      <table>
        <tr><td>
          <html:textarea styleId="pasteInput" disabled="false" property="text" rows="10" cols="40"/>
        </td></tr>
        <tr>
          <td align="right">
            <html:submit styleId="pasteSubmit" property="paste">
            <fmt:message key="bagBuild.makeBag"/>
          </html:submit>      
       </tr>
       <tr>
       <td>
       <p>
         <fmt:message key="bagBuild.or"/>
       </p> 
            
       <h4>   
         <a href="javascript:switchInputs('file', 'paste');">
          <img id='fileToggle' src="images/undisclosed.gif"/>
          <fmt:message key="bagBuild.bagFromFile"/>
         </a>
       </h4>
       </td>
       </tr>
        <tr><td>  
          <fmt:message key="bagBuild.bagFromFile"/>:
          <br/>
          <html:file styleId="fileInput" property="formFile" disabled="true"/>
        </td></tr>
        <tr>
          <td align="right">
          <html:submit styleId="fileSubmit" property="file" disabled="true">
            <fmt:message key="bagBuild.makeBag"/>
          </html:submit>
          </td>      
       </tr></table>
  </html:form>
</div>
<!-- /bagBuild.jsp -->
