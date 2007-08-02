<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>

<!-- header.jsp -->

<div id="topnav">

<a href="#" onClick="return clickreturnvalue()" onMouseover="dropdownmenu(this, event, helpMenu, '150px')" onMouseout="delayhidemenu()">Help</a> | 
<a href="#" onClick="return clickreturnvalue()" onMouseover="dropdownmenu(this, event, aboutMenu, '150px')" onMouseout="delayhidemenu()">About</a> | 
<a href="#" onClick="return clickreturnvalue()" onMouseover="dropdownmenu(this, event, dataMenu, '150px')" onMouseout="delayhidemenu()">Data</a> | 
<a href="#" onClick="return clickreturnvalue()" onMouseover="dropdownmenu(this, event, softwareMenu, '150px')" onMouseout="delayhidemenu()">Software</a>

</div>
<div id="loginbar">
    <c:if test="${!empty PROFILE.username}">
        ${PROFILE.username}&nbsp;|&nbsp;
    </c:if>
    <im:login/>
</div>
<tr>
<td width="88px">
   <!-- <html:link href="${WEB_PROPERTIES['project.sitePrefix']}/"> -->
     <img src="model/logo_grad.png" border="0" height="88px" width="88px"/>
   <!-- </html:link> -->
</td>
<td colspan="2" width="100%">
   <div id="header">
      <html:link href="${WEB_PROPERTIES['project.sitePrefix']}/">
        <h1><c:out value="${WEB_PROPERTIES['project.title']}" escapeXml="false"/>
        <span class="version"><fmt:message key="header.version"/> <c:out value="${WEB_PROPERTIES['project.releaseVersion']}" escapeXml="false"/></span></h1>
      </html:link>

    <p>
      <c:out value="${WEB_PROPERTIES['project.subTitle']}" escapeXml="false"/>
    </p>
   </div>
</td>
</tr>


<!-- /header.jsp -->
