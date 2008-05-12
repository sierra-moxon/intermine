<%@ tag body-content="scriptless" %>
<%@ attribute name="src" required="true" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="link" required="true" %>
<%@ attribute name="height" required="true" %>
<%@ attribute name="width" required="true" %>
<%@ attribute name="marginTop" required="false" %>
<%@ attribute name="floatValue" required="false" %>
<%@ attribute name="breakFloat" required="false" %>
<%@ attribute name="title" required="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="iePre7" value='<%= new Boolean(request.getHeader("user-agent").matches(".*MSIE [123456].*")) %>'/>

<c:choose>
  <c:when test="${iePre7}">
    <style type="text/css">
        div.${id} {
        background:none;
        height:${height};
        width:${width};
        margin-top:${marginTop};
        cursor:pointer;
        filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='<html:rewrite page="${src}"/>' ,sizingMethod='');
      }
    </style>
  </c:when>
  <c:otherwise>
    <style type="text/css">
      div.${id} {
        background:url('<html:rewrite page="${src}"/>') no-repeat;
        height:${height};
        margin-top:${marginTop};
        width:${width};
        cursor:pointer;
      }
    </style>
  </c:otherwise>
</c:choose>

<a href="${link}" rel="NOFOLLOW">
  <div class="${id}" style="float:${floatValue}" title="${title}">&nbsp;</div>
</a>
<c:if test="${! empty breakFloat && breakFloat == 'true'}">
  <div style="clear:${floatValue};width:${width}"></div>
</c:if>
