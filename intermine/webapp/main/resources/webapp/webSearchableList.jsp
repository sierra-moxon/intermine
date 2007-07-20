<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!-- webSearchableList.jsp -->

<tiles:importAttribute name="type"/>
<tiles:importAttribute name="scope"/>
<tiles:importAttribute name="showNames" ignore="true"/>
<tiles:importAttribute name="showTitles" ignore="true"/>
<tiles:importAttribute name="showDescriptions" ignore="true"/>

<link rel="stylesheet" type="text/css" href="css/webSearchableList.css"/>

<%-- if true this tile must be inside a <form> otherwise this tomcat error
     will appear in the log:
     Cannot find bean under name org.apache.struts.taglib.html.BEAN
  --%>
<tiles:importAttribute name="makeCheckBoxes" ignore="true"/>

<%-- if true, show the WebSearchables in a table - wsRow must be set too --%>
<tiles:importAttribute name="makeTable" ignore="true"/>

<%-- if true, show the WebSearchables in a line - wsRow must be set too --%>
<tiles:importAttribute name="makeLine" ignore="true"/>

<%-- the tile to use for the header of a table - should contain <th> elements --%>
<tiles:importAttribute name="wsHeader" ignore="true"/>

<%-- the tile to use for showing a single row of a table - should contain <td>
     elements as it will be wrapped in a <tr> if makeTable is true--%>
<tiles:importAttribute name="wsRow" ignore="true"/>

<%-- setting height causes the tile to be wrapped in div of the given height
     and with overflow: auto set --%>
<tiles:importAttribute name="height" ignore="true"/>


<html:xhtml/>

<%-- set default to true --%>
<c:if test="${empty showNames}">
  <c:set var="showNames" value="true" scope="request"/>
</c:if>
<c:if test="${empty showTitles}">
  <c:set var="showTitles" value="true" scope="request"/>
</c:if>
<c:if test="${empty showDescriptions}">
  <c:set var="showDescriptions" value="true" scope="request"/>
</c:if>

<c:if test="${!empty height && fn:length(filteredWebSearchables) > 2}">
  <div style="height: ${height}px; overflow: auto;">
</c:if>


<c:choose>
  <c:when test="${empty filteredWebSearchables}">
    <div class="altmessage">
      [nothing to display]
    </div>
  </c:when>
  <c:otherwise>
    <div id='${scope}_${type}_ws_list' class="wsList">
      <c:choose>
        <c:when test="${!empty makeTable && makeTable}">
          <%-- make a table --%>
          <table>
            <c:if test="${!empty wsHeader}">
              <thead>
                <tr>
                  <tiles:insert name="${wsHeader}">
                    <tiles:put name="scope" value="${scope}"/>
                    <tiles:put name="tags" value="${tags}"/>
                    <tiles:put name="makeCheckBoxes" value="${makeCheckBoxes}"/>
                    <tiles:put name="showNames" value="false"/>
                    <tiles:put name="showTitles" value="false"/>
                    <tiles:put name="showNames" value="${showNames}"/>
                    <tiles:put name="showTitles" value="${showTitles}"/>
                    <tiles:put name="showDescriptions" value="${showDescriptions}"/>
                  </tiles:insert>
                </tr>
              </thead>
            </c:if>
            <tbody>
              <c:forEach items="${filteredWebSearchables}" var="entry" varStatus="status">
                <c:set var="webSearchable" value="${entry.value}" scope="request"/>
                <tr id='${scope}_${type}_item_${entry.value.name}' class="${ageClasses[entry.key]}">
                  <tiles:insert name="${wsRow}">
                    <tiles:put name="wsName" value="${entry.key}"/>
                    <tiles:put name="webSearchable" beanName="webSearchable"/>
                    <tiles:put name="statusIndex" value="${status.index}"/>
                    <tiles:put name="wsCheckBoxId"
                               value="selected_${scope}_${type}_${wsName}"/>
                    <tiles:put name="makeCheckBoxes" value="${makeCheckBoxes}"/>
                    <tiles:put name="scope" value="${scope}"/>
                    <tiles:put name="tags" value="${tags}"/>
                    <tiles:put name="showNames" value="${showNames}"/>
                    <tiles:put name="showTitles" value="${showTitles}"/>
                    <tiles:put name="showDescriptions" value="${showDescriptions}"/>
                  </tiles:insert>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:when>
        <c:when test="${!empty makeLine && makeLine}">
        <%-- make a line --%>
        <c:forEach items="${filteredWebSearchables}" var="entry" varStatus="status">
          <c:set var="webSearchable" value="${entry.value}" scope="request"/>
            <tiles:insert name="${wsRow}">
              <tiles:put name="wsName" value="${entry.key}"/>
              <tiles:put name="webSearchable" beanName="webSearchable"/>
              <tiles:put name="statusIndex" value="${status.index}"/>
              <tiles:put name="wsCheckBoxId"
                         value="selected_${scope}_${type}_${status.index}"/>
              <tiles:put name="makeCheckBoxes" value="${makeCheckBoxes}"/>
              <tiles:put name="scope" value="${scope}"/>
              <tiles:put name="tags" value="${tags}"/>
              <tiles:put name="showNames" value="${showNames}"/>
              <tiles:put name="showTitles" value="${showTitles}"/>
              <tiles:put name="showDescriptions" value="${showDescriptions}"/>
            </tiles:insert>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <%-- make a list --%>
          <ul>
            <c:forEach items="${filteredWebSearchables}" var="entry">
              <li id='${scope}_${type}_item_${entry.value.name}'>
                <div class="wsListElement">
                  <html:link action="/gotows?type=${type}&amp;scope=${scope}&amp;name=${entry.key}">${entry.value.title}
                    <c:choose>
                      <c:when test="${type=='template'}">
                        <img border="0" class="arrow" src="images/template_t.gif" alt="-&gt;"/>
                      </c:when>
                      <c:otherwise>
                        <img border="0" class="arrow" src="images/bag_ico.gif" alt="-&gt;"/>
	              </c:otherwise>
	            </c:choose>
                  </html:link>
                  <tiles:insert name="starTemplate.tile" flush="false">
                    <tiles:put name="templateName" value="${entry.value.title}"/>
                  </tiles:insert>
                </div>
                <c:if test="${showDescriptions}">
                  <div class="wsListDescription">
                    ${entry.value.description}
                  </div>
                </c:if>
              </li>
            </c:forEach>
          </ul>
        </c:otherwise>
      </c:choose>
      <c:if test="${! empty bagCount || ! empty templateCount}">
      <c:choose>
        <c:when test="${type == 'bag'}">
          <p><u><html:link action="/bag">View all ${bagCount} bags...</html:link></u></p>
        </c:when>
        <c:otherwise>
          <p><u><html:link action="/templates">View all ${templateCount} templates...</html:link></u></p><br>
        </c:otherwise>
      </c:choose>
      </c:if>
    </div>
  </c:otherwise>
</c:choose>

<c:if test="${!empty height}">
</div>
</c:if>

<!-- /webSearchableList.jsp -->
