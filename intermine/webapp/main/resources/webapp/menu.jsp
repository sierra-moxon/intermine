<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>

<!-- menu.jsp -->
<html:xhtml/>
<div class="links">
  <c:if test="${!empty PROFILE.username}">
    <span class="menu-logged-in-item">
      <html:link action="/changePassword.do" title="Change Password">${PROFILE.username}</html:link>
    </span>
  </c:if>
  <span class="menu-item">
    <html:link href="${WEB_PROPERTIES['project.sitePrefix']}/">
      <fmt:message key="menu.home"/>
    </html:link>
  </span>
  <c:if test="${WEB_PROPERTIES['project.standalone']}">
    <span class="menu-item">
      <html:link action="/begin.do">
        <fmt:message key="menu.newquery"/>
      </html:link>
    </span>
  </c:if>
  <span class="menu-item">
    <html:link action="/mymine.do">
      <fmt:message key="menu.mymine"/>
    </html:link>
  </span>
  <span class="menu-item">
    <html:link action="/templateSearch.do">
      <fmt:message key="menu.searchTemplates"/>
    </html:link>
    <img src="images/inspect.gif" width="12" height="11" alt="-&gt;"/>
  </span>
  <span class="menu-item">
    <html:link action="/mymine.do?page=bags">
      <fmt:message key="menu.buildBag"/>
    </html:link>
  </span>
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
  </span>
  <span class="menu-item">
    <html:link action="/feedback.do">
      <fmt:message key="menu.feedback"/>
    </html:link>
  </span>
  <span class="menu-item">
    <c:choose>
      <c:when test="${!empty PROFILE_MANAGER && empty PROFILE.username}">
        <html:link action="/login.do">
          <fmt:message key="menu.login"/>
        </html:link>
      </c:when>
      <c:otherwise>
        <html:link action="/logout.do">
          <fmt:message key="menu.logout"/>
        </html:link>
      </c:otherwise>
    </c:choose>
  </span>
  <span class="menu-item">
    <html:link href="${WEB_PROPERTIES['project.sitePrefix']}/doc/manual/">
      <fmt:message key="menu.help"/>
    </html:link>
  </span>
  <span class="menu-item">
    <tiles:insert name="browse.tile">
      <tiles:put name="menuItem" value="true"/>
    </tiles:insert>
  </span>
</div>
<!-- /menu.jsp -->
