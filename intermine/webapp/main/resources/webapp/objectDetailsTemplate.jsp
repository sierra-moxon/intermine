<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>

<!-- objectDetailsTemplate.jsp -->

<html:xhtml/>

<tiles:importAttribute name="displayObject" ignore="true"/>
<tiles:importAttribute name="interMineIdBag" ignore="true"/>
<tiles:importAttribute name="templateQuery"/>
<tiles:importAttribute name="placement"/>
<tiles:importAttribute name="type"/>

<c:set var="templateName" value="${templateQuery.name}"/>
<c:set var="uid" value="${fn:replace(placement, ' ', '_')}_${templateName}"/>

<c:set var="placementAndField" value="${placement}_${templateName}"/>
<c:if test="${!empty displayObject}">
  <c:set var="verbose" value="${!empty displayObject.verbosity[placementAndField]}"/>
  <c:set var="interMineObject" value="${displayObject.object}"/>
</c:if>

<div class="templateLine">
  <c:choose>
    <c:when test="${empty displayObject && empty interMineIdBag}">
      <%-- no icon at all --%>
    </c:when>
    <%--
      <c:when test="${!empty templateCounts[templateName] &&
                  templateCounts[templateName] == 0}">
      <img border="0" src="images/blank.gif" alt=" " width="11" height="11"/>
      <c:set var="cssClass" value="nullStrike"/>
    </c:when>
    <c:when test="${empty table}">
      <img border="0" src="images/blank.gif" alt=" " width="11" height="11"/>
    </c:when>
    --%>
    <c:when test="${verbose}">
      <div class="templateIcon">
        <html:link action="/modifyDetails?method=unverbosify&amp;field=${templateName}&amp;placement=${placement}&amp;id=${object.id}&amp;trail=${param.trail}"
          onclick="return toggleTemplateList('${fn:replace(placement, ' ', '_')}', '${templateName}')">
          <img border="0" src="images/minus.gif" alt="-" id="img_${uid}" height="11" width="11"/>
        </html:link>
      </div>
    </c:when>
    <c:otherwise>
      <div class="templateIcon">
        <html:link action="/modifyDetails?method=verbosify&amp;field=${templateName}&amp;placement=${placement}&amp;id=${object.id}&amp;trail=${param.trail}"
          onclick="return toggleTemplateList('${fn:replace(placement, ' ', '_')}', '${templateName}')">
          <img border="0" src="images/plus.gif" alt="+" id="img_${uid}" height="11" width="11"/>
        </html:link>
      </div>
    </c:otherwise>
  </c:choose>
  
  <div class="${displayObject == null ? '' : 'templateDetails'}">
    <span class="${cssClass}" id="label_${uid}">
      <im:templateLine type="${type}" templateQuery="${templateQuery}"
                       className="${className}" interMineObject="${interMineObject}" bagName="${interMineIdBag.name}"/>
      <span id="count_${uid}" class="templateResCount"></span><br/>
    </span>
  </div>

  <div id="table_${uid}" style="${verbose?'':'display: none'}">
    <div id="table_${uid}_int">
      <c:if test="${verbose}">
        <tiles:insert name="objectDetailsTemplateTable.tile">
          <tiles:put name="displayObject" beanName="displayObject"/>
          <tiles:put name="interMineIdBag" beanName="interMineIdBag"/>
          <tiles:put name="templateQuery" beanName="templateQuery"/>
          <tiles:put name="placement" value="${placement}"/>
        </tiles:insert>
      </c:if>
    </div>
  </div>
  
  <c:if test="${IS_SUPERUSER}">
    <tiles:insert name="inlineTagEditor.tile">
      <tiles:put name="taggable" beanName="templateQuery"/>
      <tiles:put name="vertical" value="true"/>
      <tiles:put name="show" value="true"/>
    </tiles:insert>
  </c:if>
  
  <c:choose>
    <c:when test="${!verbose && displayObject != null}">
      <script type="text/javascript">
        <!--//<![CDATA[
          $('img_${uid}').src='images/spinner.gif';
          queueInlineTemplateQuery('${placement}', '${templateName}', '${displayObject.object.id}');
        //]]>-->
      </script>
    </c:when>
    <c:when test="${!verbose && interMineIdBag != null}">
      <script type="text/javascript">
        <!--//<![CDATA[
          $('img_${uid}').src='images/spinner.gif';
          queueInlineTemplateQuery('${placement}', '${templateName}', '${interMineIdBag.name}');
        //]]>-->
      </script>
    </c:when>
  </c:choose>
  
</div>
<!-- /objectDetailsTemplate.jsp -->
