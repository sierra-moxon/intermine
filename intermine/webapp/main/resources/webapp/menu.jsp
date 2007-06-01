<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>

<!-- menu.jsp -->
<html:xhtml/>

<div class="links">
  <c:if test="${!empty PROFILE.username}">
    <span class="menu-logged-in-item">
      <html:link action="/changePassword.do" title="Change Password">${PROFILE.username}</html:link>
    </span>
    &nbsp;  <%-- for IE 6 - setting padding in CSS doesn't work --%>
  </c:if>
  <span class="menu-item">
    <html:link href="${WEB_PROPERTIES['project.sitePrefix']}/">
      <fmt:message key="menu.home"/>
    </html:link>
    &nbsp;
  </span>
  <c:if test="${WEB_PROPERTIES['project.standalone']}">
    <span class="menu-item">
      <html:link action="/begin.do">
        <fmt:message key="menu.newquery"/>
      </html:link>
      &nbsp;
    </span>
  </c:if>
  <span class="menu-item">
    <c:choose>
      <c:when test="${!empty QUERY}">
        <html:link action="/query.do?showTemplate=true">
          <fmt:message key="menu.currentquery"/>
        </html:link>
      </c:when>
      <c:otherwise>
        <fmt:message key="menu.currentquery"/>
      </c:otherwise>
    </c:choose>
    &nbsp;
  </span>
  <span class="menu-item">
    <html:link action="/mymine.do">
      <fmt:message key="menu.mymine"/>
    </html:link>
    &nbsp;
  </span>
  <span class="menu-item">
    <html:link action="/mymine.do?page=bags">
      <fmt:message key="menu.bags"/>
    </html:link>
    &nbsp;
  </span>
  <span class="menu-item">
    <html:link action="/search.do?type=template">
      <fmt:message key="menu.searchTemplates"/>
    </html:link>
    <img src="images/inspect.gif" width="12" height="11" alt="-&gt;"/>
    &nbsp;
  </span>
  <span class="menu-item">
    <html:link action="/search.do?type=bag">
      Search bags
    </html:link>
    &nbsp;
  </span>
  <span class="menu-item">
    <html:link action="/history.do">
      <fmt:message key="menu.history"/>
    </html:link>
    &nbsp;
  </span>
  <span class="menu-item">
    <html:link action="/feedback.do">
      <fmt:message key="menu.feedback"/>
    </html:link>
    &nbsp;
  </span>
  <span class="menu-item">
    <im:login/>
    &nbsp;
  </span>
  <span class="menu-item">
    <html:link href="${WEB_PROPERTIES['project.sitePrefix']}/doc/manual/">
      <fmt:message key="menu.help"/>
    </html:link>
    &nbsp;
  </span>
  <span class="menu-item">
    <tiles:insert name="browse.tile">
      <tiles:put name="menuItem" value="true"/>
    </tiles:insert>
    &nbsp;
  </span>
</div>
<!-- /menu.jsp -->
