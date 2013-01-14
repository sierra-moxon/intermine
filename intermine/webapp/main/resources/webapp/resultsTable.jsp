<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>
<%@ taglib uri="/WEB-INF/functions.tld" prefix="imf" %>

<html:xhtml/>

<tiles:importAttribute name="invalid" ignore="true"/>
<tiles:importAttribute name="bag" ignore="true"/>
<tiles:importAttribute name="cssClass" ignore="true"/>
<tiles:importAttribute name="pageSize" ignore="true"/>
<tiles:importAttribute name="query" ignore="true"/>

<c:if test="${empty query}">
    <c:set var="query" value="${QUERY}"/>
</c:if>

<c:if test="${empty pageSize}">
    <c:set var="pageSize" value="25"/>
</c:if>
  

<c:set var="initValue" value="0"/>

<c:if test="${empty currentUniqueId}">
    <c:set var="currentUniqueId" value="${initValue}" scope="application"/>
</c:if>

<c:set var="tableContainerId" value="_unique_id_${currentUniqueId}" scope="request"/>

<c:set var="currentUniqueId" value="${currentUniqueId + 1}" scope="application"/>

<script>
(function() {
    intermine.css.headerIcon = "fm-header-icon";
    <c:choose>
        <c:when test="${not empty query.json}">
            var query = ${query.json};
        </c:when>
        <c:otherwise>
            var query = {}; // QUERY.json is empty
        </c:otherwise>
    </c:choose>

    jQuery(function() {
        var $container = jQuery('#${tableContainerId}');

        if (query && query.select && query.select.length > 0) {
            $container.empty().imWidget({
                type: 'table',
                service: $SERVICE,
                query: query,
                events: LIST_EVENTS,
                properties: {pageSize: ${pageSize} },
                error: FailureNotification.notify
            });
        } else {
            $container.html('<p>Query has not been specified, failing...</p>');
        }
    });
})();
</script>

<c:if test="${! empty query.title}">
    <c:set var="templateQuery" value="${query}"/>
    <tiles:insert template="templateTitle.jsp"/>
</c:if>

<div id="${tableContainerId}" class="${cssClass}"></div>
