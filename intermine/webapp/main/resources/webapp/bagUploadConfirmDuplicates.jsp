<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>

<tiles:importAttribute name="message" ignore="false"/>
<tiles:importAttribute name="resultElementMap" ignore="false"/>
<tiles:importAttribute name="columnNames" ignore="false"/>

<fmt:message key="bagUploadConfirm.duplicatesHeader">
  <fmt:param value="${message}"/>
</fmt:message>

<table class="collection" cellspacing="0">
    <tr>
      <td>
        Identifier
      </td>
      <td width="10"> 
        <fmt:message key="objectDetails.class"/>
      </td>
      <c:forEach items="${columnNames}" var="name"
                 varStatus="status">
        <td>
          <span class="attributeField" style="white-space:nowrap">
            ${name} <im:typehelp type="${columnNames[status.index]}"/>
          </span>
        </td>
      </c:forEach>
      <td width="10">
        &nbsp;<%--for IE--%>
      </td>
    </tr>
  <c:forEach var="resultElementEntry" items="${resultElementMap}">
    <c:set var="identifier" value="${resultElementEntry.key}"/>
    <c:set var="resultElementRowList" value="${resultElementEntry.value}"/>

    <c:forEach var="resultElementRow" items="${resultElementRowList}" varStatus="status">
      <c:set var="rowClass">
        <c:choose>
          <c:when test="${status.count % 2 == 1}">odd</c:when>
          <c:otherwise>even</c:otherwise>
        </c:choose>
      </c:set>

      <tr class="${rowClass}" id="tr_${identifier}"/>
        <c:if test="${status.index == 0}">
          <td border="1" rowSpan="${fn:length(resultElementRowList)}"
              valign="top" id="td_${identifier}">${identifier}</td>
        </c:if>

        <c:forEach var="resultElement" items="${resultElementRow}" varStatus="rowStatus">
          <td id="row_${status.count}">
            <c:choose>
              <c:when test="${rowStatus.index == 0}">
                <%-- special case: the first element is the class name --%>
                ${resultElement}
              </c:when>
              <c:when test="${rowStatus.index == fn:length(resultElementRow) - 1}">
                <%-- special case: the last element is the object id --%>
                <span id="add_${resultElementRow[rowStatus.index]}" onclick="addId2Bag('${resultElementRow[rowStatus.index]}','${status.count}','${identifier}');" class="fakelink">Add</span>
                &nbsp;&nbsp;
                <span id="rem_${resultElementRow[rowStatus.index]}" onclick="removeIdFromBag('${resultElementRow[rowStatus.index]}','${status.count}','${identifier}');">Remove</span>
                <!-- <html:multibox property="selectedObjects"
                               styleId="selectedObject_${rowStatus.index}">
                  ${resultElementRow[rowStatus.index]}
                </html:multibox>-->
              </c:when>
              <c:otherwise>
                ${resultElement.field}
              </c:otherwise>
            </c:choose>
          </td>
        </c:forEach>
      </tr>
    </c:forEach>
  </c:forEach>
</table>
