<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<tiles:useAttribute name="tab" id="tab"/>
<tiles:useAttribute name="loggedin" id="loggedin"/>
<tiles:useAttribute name="itemList" id="itemList"/>

<!-- subMenu.jsp -->
<c:choose>
  <c:when test="${tab == 'mymine'}">
    <c:set var="styleClass" value="submenu_mymine" />
  </c:when>
  <c:otherwise>
    <c:set var="styleClass" value="submenu" />
  </c:otherwise>
</c:choose>
<div id="submenu" class="${styleClass}">
<div id="quicksearch">
  <tiles:insert name="quickSearch.tile">
    <tiles:put name="menuItem" value="true"/>
  </tiles:insert>
</div>

<ul id="submenulist">
<c:set var="count" value="0"/>
<c:set var="subtabName" value="subtab${pageName}" scope="request" />
<c:forTokens items="${itemList}" delims=" " var="item" varStatus="counter">
  <c:set var="tabArray" value="${fn:split(item, ':')}" />
  <c:if test="${tabArray[0] == tab}">
  <c:if test="${count>0}">
    <li>&nbsp;|&nbsp;</li>
  </c:if>
  <c:choose>
    <c:when test="${((empty userOptionMap[subtabName] && count == 0)||(userOptionMap[subtabName] == tabArray[2])) && (tab == pageName)}">
      <li id="subactive_${tab}"><fmt:message key="${tabArray[1]}" /></li>
    </c:when>
    <c:when test="${(tabArray[3] == '1') && (loggedin == false)}">
      <li>
        <span onclick="alert('You need to log in'); return false;"><fmt:message key="${tabArray[1]}"/></span>
      </li>
    </c:when>
    <c:otherwise>
      <li><a href="/${WEB_PROPERTIES['webapp.path']}/${tab}.do?subtab=${tabArray[2]}"><fmt:message key="${tabArray[1]}"/></a></li>
    </c:otherwise>
  </c:choose>
  <c:set var="count" value="${count+1}"/>
  </c:if>
</c:forTokens>
<c:if test="${pageName == 'begin'}">
  <li>
    <a href="${WEB_PROPERTIES['project.sitePrefix']}/what.shtml">What is ${WEB_PROPERTIES['project.title']}?</a>
  </li>
</c:if>
</ul>
</div>
<!-- /subMenu.jsp -->
