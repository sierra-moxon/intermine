<%@ tag body-content="scriptless"  %>
<%@ attribute name="title" required="false" %>
<%@ attribute name="titleKey" required="false" %>
<%@ attribute name="helpUrl" required="false" %>
<%@ attribute name="topRightTile" required="false" %>
<%@ attribute name="topLeftTile" required="false" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${!empty titleKey}">
  <fmt:message key="${titleKey}" var="title"/>
</c:if>

<c:if test="${!empty title || !empty topRightTile || !empty topLeftTile}">
  <div class="box">
    <div class="title">
      <div class="boxTopRight" float="right" nowrap="nowrap">
        <c:choose>
          <c:when test="${!empty topRightTile}">
            <tiles:insert name="${topRightTile}"/>
          </c:when>
          <c:when test="${!shownAspectsPopup}">
            <tiles:insert page="/aspectPopup.jsp"/>
            <c:set scope="request" var="shownAspectsPopup" value="${true}"/>
          </c:when>
          <c:otherwise>
            &nbsp;
          </c:otherwise>
        </c:choose>
      </div>
      <div align="left">
        <c:if test="${empty title && !empty topLeftTile}">
          <tiles:insert name="${topLeftTile}"/>
        </c:if>
        ${title}
        <c:if test="${!empty helpUrl}">
          <span class="help">
            [<html:link href="${helpUrl}">
              <fmt:message key="begin.link.help"/>
            </html:link>]
          </span>
        </c:if>
      </div>
    </div>
    <div style="clear:both;" class="boxbody">
      <jsp:doBody/>
    </div>
  </div>
</c:if>

<%-- or just process body if no title --%>
<c:if test="${empty title && empty topRightTile && empty topLeftTile}">
  <jsp:doBody/>
</c:if>
