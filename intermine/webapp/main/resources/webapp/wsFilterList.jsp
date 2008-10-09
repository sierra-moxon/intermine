<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>

<!-- wsFilterList.jsp -->

<%@ page import="org.intermine.dwr.AjaxServices" %>
<%@ page import="org.intermine.web.logic.session.SessionMethods" %>

<tiles:importAttribute name="type"/>
<tiles:importAttribute name="wsListId"/>
<tiles:importAttribute name="scope"/>
<tiles:importAttribute name="showNames" ignore="true"/>
<tiles:importAttribute name="showTitles" ignore="true"/>
<tiles:importAttribute name="showDescriptions" ignore="true"/>
<tiles:importAttribute name="showSearchBox" ignore="true"/>

<tiles:importAttribute name="makeCheckBoxes" ignore="true"/>
<tiles:importAttribute name="makeTable" ignore="true"/>
<tiles:importAttribute name="makeLine" ignore="true"/>
<tiles:importAttribute name="wsHeader" ignore="true"/>
<tiles:importAttribute name="wsRow" ignore="true"/>
<tiles:importAttribute name="height" ignore="true"/>
<tiles:importAttribute name="limit" ignore="true"/>
<tiles:importAttribute name="initialFilterText" ignore="true"/>
<tiles:importAttribute name="loginMessageKey" ignore="true"/>
<tiles:importAttribute name="showCount" ignore="true"/>

<html:xhtml/>
<%
    String id =  pageContext.getAttribute("wsListId") + "_" + pageContext.getAttribute("type") + "_item_description";
      org.intermine.web.logic.results.WebState webState = SessionMethods.getWebState(session);
      if (webState.getState(id) != null) {
          if (webState.getState(id).toString().equals("true")) {
              pageContext.setAttribute("userShowDescription", true);        
          }
      } else {
          pageContext.setAttribute("userShowDescription", true);
      }
%>

<c:set var="ws_input_id" value="${wsListId}_${type}_filter_text"/>
<c:set var="ws_input_aspect" value="${wsListId}_${type}_filter_aspect"/>

<div class="filterBar">
<div style="padding-left: 15px; padding-right: 15px; padding-top: 15px;">
<ul class="filterActions">
  <li>Search:&nbsp;</li>
  <li><input type="text" id="${ws_input_id}" name="newName_${name}" size="20" 
           onkeyup="return filterWebSearchablesHandler(event, this, '${type}', '${wsListId}');"
           onmouseup="if(this.value != null && this.value.length > 1) {return filterWebSearchablesHandler(event, this, '${type}', '${wsListId}');}"
           onKeyPress="return disableEnterKey(event);"
           disabled="true"
           value="${initialFilterText}"/></li>
  <!-- <li>&nbsp; <img id='${wsListId}_${type}_spinner' style='display: none' 
             src='images/wait20trans.gif'/></li> -->
<c:if test="${! empty PROFILE.username || type == 'template'}">
  <li>&nbsp;&nbsp;&nbsp;Filter:&nbsp;</li>
</c:if>  
<c:if test="${! empty PROFILE.username}">
  <li ><a href="javascript:filterFavourites('${type}', '${wsListId}');"><img id="filter_favourites_${wsListId}_${type}" src="images/filter_favourites.png" width="20" height="20" title="Show Only Favourites"/></a></li>
  <li><a href="javascript:changeScope('${type}', '${wsListId}');"><img id="filter_scope_${wsListId}_${type}" src="images/filter_all.png" width="20" height="20" title="Show all or mine only"/></a></li>
  <c:if test="${type == 'bag'}">
	<script type="text/javascript">
		function filterByTag(tag) {
			filterByUserTag('${type}', '${wsListId}', tag);
		}
	</script>
    <li><tiles:insert name="tagSelect.tile">
	        <tiles:put name="type" value="${type}" />
	        <tiles:put name="selectId" value="tagSelect" />
	        <tiles:put name="onChangeFunction" value="filterByTag" />
	</tiles:insert></li>
  </c:if>
</c:if>
  <!-- <li><img src="images/filter_sort_desc.png" width="20" height="20" alt="Sort by name"/></li>
  <li><img src="images/filter_date_desc.png" width="20" height="20" alt="Sort by Date"/>&nbsp;</li> -->
  <c:if test="${type == 'template'}">
  <%-- aspects --%>
    <li><select onchange="javascript:filterAspect('${type}', '${wsListId}')" id="${ws_input_aspect}" class="aspectSelect">
    <c:if test="${aspect == null}">
      <option value="" selected>-- all categories --</option>
    </c:if>
    <c:forEach items="${ASPECTS}" var="entry">
      <c:set var="set" value="${entry.value}"/>
      <option value="${set.name}"
        <c:if test="${aspect.name == set.name}">
          selected
        </c:if>
      >${set.name}</option>
    </c:forEach>
  </select></li>
  </c:if>
  <li>
    &nbsp;&nbsp;<a href="#" onclick="javascript:return clearFilter('${type}', '${wsListId}')">
      <img src="theme/reset.png" title="Reset search"/>
    </a>
  </li>
</ul>
<input type="hidden" name="filterAction_${wsListId}_${type}" id="filterAction_${wsListId}_${type}"/>
<input type="hidden" name="filterScope_${wsListId}_${type}" id="filterScope_${wsListId}_${type}" value="${scope}"/>

<div style="float:right">
        <span style="vertical-align:top;">Actions:&nbsp;</span>
<c:choose>
<c:when test="${type == 'template'}">
  <html:image property="export" value="export" styleId="export_button" src="theme/export.png"  title="Export selected Templates"/>
  <html:hidden property="pageName" value="templates"/>
  <html:hidden property="templateButton" value="export"/>
</c:when>
<c:otherwise>

<c:set var="textForBox" value="${WEB_PROPERTIES['lists.input.example']}" />

<script language="javascript">
  function clearBagName(element) {
     if(element.value == '${textForBox}'){
             element.value='';
             element.style.fontStyle = 'normal';
             element.style.color = '';
     }
  }
</script>
<html:text property="newBagName" size="12" value="${textForBox}" style="color:#666;font-style:italic;vertical-align:top" onclick="clearBagName(this)"/>
<img src="theme/union.png" onclick="validateBagOperations('modifyBagForm', 'union')" title="union"/>
<img src="theme/intersect.png" onclick="validateBagOperations('modifyBagForm', 'intersect')" title="intersect"/>
<img src="theme/substract.png" onclick="validateBagOperations('modifyBagForm', 'subtract')" title="subtract"/>
<img src="theme/copy.png" onclick="validateBagOperations('modifyBagForm', 'copy')" title="copy"/>  
<html:hidden property="listsButton" value="" styleId="listsButton"/>
</c:otherwise>
</c:choose>
</div>
</div>
<div style="clear:both; padding-left:15px; padding-right:15px; padding-bottom: 15px;">
  <c:if test="${! empty userShowDescription}">
    <c:set var="checkboxChecked" value="checked" />
  </c:if>
  <input type="checkbox" <c:out value="${checkboxChecked}" /> id="showCheckbox" 
    onclick="showDescriptions('<c:out value="${wsListId}" />', '<c:out value="${type}" />', this.checked)">
    &nbsp;<label for="showCheckbox">Show descriptions</label>
</div>
</div>



<script type="text/javascript">
  <%-- turn off autocomplete because of a Gecko bug:
       http://geekswithblogs.net/shahedul/archive/2006/08/14/87910.aspx --%>
  <!--
      $('${wsListId}_${type}_filter_text').setAttribute('autocomplete','off');
    -->
</script>

<tiles:insert name="webSearchableList.tile">
  <tiles:put name="type" value="${type}"/>
  <tiles:put name="wsListId" value="${wsListId}"/>
  <tiles:put name="scope" value="${scope}"/>
  <tiles:put name="tags" value="${tags}"/>
  <tiles:put name="showNames" value="${showNames}"/>
  <tiles:put name="showTitles" value="${showTitles}"/>
  <tiles:put name="showDescriptions" value="${showDescriptions}"/>
  <tiles:put name="makeCheckBoxes" value="${makeCheckBoxes}"/>
  <tiles:put name="makeTable" value="${makeTable}"/>
  <tiles:put name="makeLine" value="${makeLine}"/>
  <tiles:put name="wsHeader" value="${wsHeader}"/>
  <tiles:put name="wsRow" value="${wsRow}"/>
  <tiles:put name="limit" value="${limit}"/>
  <tiles:put name="height" value="${height}"/>
  <tiles:put name="showSearchBox" value="${showSearchBox}"/>
  <tiles:put name="delayDisplay" value="${!empty initialFilterText}"/>
  <tiles:put name="loginMessageKey" value="${loginMessageKey}"/>
  <tiles:put name="showCount" value="${showCount}"/>
</tiles:insert>

 <script type="text/javascript">
<%-- enable filter only after the list is populated --%>
<!--//<![CDATA[
    $('${ws_input_id}').disabled = false;
//]]>-->
  </script>

<c:if test="${empty userShowDescription}">
    <script type="text/javascript">
<%-- If show description checkbox is not checked, then descriptions should be hidden --%>
    showDescriptions('<c:out value="${wsListId}" />', '<c:out value="${type}" />', false);
    </script>
</c:if>

<c:if test="${!empty initialFilterText}">
  <script type="text/javascript">
<!--//<![CDATA[
    filterWebSearchablesHandler(null, $('${ws_input_id}'), '${type}', '${wsListId}');
//]]>-->
  </script>
</c:if>
<!-- /wsFilterList.jsp -->
