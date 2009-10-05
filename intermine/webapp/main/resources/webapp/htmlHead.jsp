<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!-- htmlHead.jsp -->

<tiles:importAttribute name="htmlPageTitle" ignore="true"/>
<tiles:importAttribute name="pageName" ignore="true"/>


<link rel="stylesheet" type="text/css" href="<html:rewrite page='/css/webapp.css'/>"/>
<link rel="stylesheet" type="text/css" href="<html:rewrite page='/css/inlineTagEditor.css'/>"/>
<link rel="stylesheet" type="text/css" href="<html:rewrite page='/css/ui.datepicker.css'/>"/>
<link rel="stylesheet" type="text/css" href="<html:rewrite page='/css/inlineTagEditor.css'/>"/>

<link href="${WEB_PROPERTIES['project.rss']}" rel="alternate" type="application/rss+xml" title="${WEB_PROPERTIES['project.title']} | News" />

<%
/* In Safari, loading a css that doesnt exist causes weirdness */
String pageName = (String) request.getAttribute("pageName");
if(new java.io.File(application.getRealPath("css")+"/"+pageName+".css").exists()) {
        request.setAttribute("pageCSS","true");
}
%>
<c:if test="${pageCSS == 'true'}">
<link rel="stylesheet" type="text/css" href="<html:rewrite page='/css/${pageName}.css'/>"/>
</c:if>
<c:set var="theme" value="${WEB_PROPERTIES['theme']}"/>
<link rel="stylesheet" type="text/css" href="<html:rewrite page='/themes/${theme}/theme.css'/>"/>

<script type="text/javascript" src="<html:rewrite page='/js/jquery-1.2.6.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/js/jquery-ui-personalized-1.6rc2.min.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/js/jquery.dimensions.min.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/js/jquery.center.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/js/jquery.qtip-1.0.0-rc3.min.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/js/jquery.boxy.js'/>"></script>
<link rel="stylesheet" type="text/css" href="<html:rewrite page='/css/boxy.css'/>"/>
<script type="text/javascript">
  jQuery.noConflict();
</script>

<script type="text/javascript" src="<html:rewrite page='/js/prototype.js'/>"></script>
<!-- <script type="text/javascript" src="<html:rewrite page='/js/scriptaculous.js'/>"></script> -->

<script type="text/javascript" src="<html:rewrite page='/dwr/interface/AjaxServices.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/dwr/engine.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/dwr/util.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/js/imdwr.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/js/imutils.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/js/niftycube.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/js/inlineTagEditor.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/js/date.js'/>"></script>
<script type="text/javascript" src="<html:rewrite page='/js/tagSelect.js'/>"></script>
<!--[if lt IE 7.]>
  <script defer type="text/javascript" src="pngfix.js"></script>
<![endif]-->

<meta content="microarray, bioinformatics, drosophila, genomics" name="keywords"/>
<meta content="Integrated queryable database for Drosophila and Anopheles genomics"
      name="description"/>
<meta content="text/html; charset=iso-8859-1" http-equiv="Content-Type"/>

<title>
  <c:choose>
    <c:when test="${empty pageName}">
      <c:out value="${WEB_PROPERTIES['project.title']}" escapeXml="false"/>
    </c:when>
    <c:otherwise>
      <c:out value="${WEB_PROPERTIES['project.title']}: ${htmlPageTitle}" escapeXml="false"/>
    </c:otherwise>
  </c:choose>
</title>

<script type="text/javascript">
<!--
  function showContactForm()
  {
    document.getElementById('contactFormDiv').style.display='';
    document.getElementById('contactFormDivButton').style.display='none';
    window.scrollTo(0, 99999);
    document.getElementById("fbname").focus();
  }

//-->
</script>
<!-- /htmlHead.jsp -->
