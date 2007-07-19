<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>

<!-- wsTemplateTable.jsp -->
<tiles:importAttribute name="scope"/>
<tiles:importAttribute name="showNames" ignore="true"/>
<tiles:importAttribute name="showTitles" ignore="true"/>
<tiles:importAttribute name="showDescriptions" ignore="true"/>
<tiles:importAttribute name="limit" ignore="true"/>
<tiles:importAttribute name="height" ignore="true"/>
<tiles:importAttribute name="tags" ignore="true"/>
<tiles:importAttribute name="makeCheckBoxes" ignore="true"/>
<tiles:importAttribute name="showSearchBox" ignore="true"/>

<html:xhtml/>
<div class="webSearchable">
<h2>
 <c:choose>
  <c:when test="${scope == 'global'}">
   <fmt:message key="wsTemplateTable.global"/>
  </c:when>
  <c:otherwise>
   <fmt:message key="wsTemplateTable.mine"/>
  </c:otherwise>
 </c:choose>
</h2>
<tiles:insert name="wsFilterList.tile">
  <tiles:put name="type" value="template"/>
  <tiles:put name="scope" value="${scope}"/>
  <tiles:put name="tags" value="${tags}"/>
  <tiles:put name="showNames" value="${showNames}"/>
  <tiles:put name="showTitles" value="${showTitles}"/>
  <tiles:put name="showDescriptions" value="${showDescriptions}"/>
  <tiles:put name="makeCheckBoxes" value="${makeCheckBoxes}"/>
  <tiles:put name="makeLine" value="true"/>
  <!-- <tiles:put name="wsHeader" value="wsTemplateHeader.tile"/> -->
  <tiles:put name="wsRow" value="wsTemplateLine.tile"/>
  <tiles:put name="limit" value="${limit}"/>
  <tiles:put name="height" value="${height}"/>
  <tiles:put name="showSearchBox" value="${showSearchBox}"/>
</tiles:insert>
</div>
<!-- /wsTemplateTable.jsp -->
