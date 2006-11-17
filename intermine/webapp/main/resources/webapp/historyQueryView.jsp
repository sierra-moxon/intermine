<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>

<!-- historyQueryView.jsp -->
<html:xhtml/>

<tiles:useAttribute id="type" name="type"/>

<c:choose>
  <c:when test="${type == 'saved'}">
    <c:set var="queryMap" value="${PROFILE.savedQueries}"/>
    <c:set var="messageKey" value="history.savedQueries.help"/>
  </c:when>
  <c:otherwise>
    <c:set var="queryMap" value="${PROFILE.history}"/>
    <c:set var="messageKey" value="history.history.help"/>
  </c:otherwise>
</c:choose>

<im:body id="${type}">
  <script LANGUAGE="JavaScript">
    <!--//<![CDATA[
    function confirmAction() {
      return confirm("Do you really want to delete the selected queries?")
    }
    //]]>-->
  </script>

  <p>
    <fmt:message key="${messageKey}"/>
  </p>

    <%-- Choose the queries to display --%>
    <c:choose>
      <c:when test="${empty queryMap}">
        <div class="altmessage">
          None
        </div>
      </c:when>
      <c:otherwise>

        <html:form action="/modifyQuery">
        <input type="hidden" name="type" value="${type}"/>
        <table class="results history" cellspacing="0">
          <tr>
            <th>
              <input type="checkbox" id="selected_${type}"
                     onclick="selectColumnCheckbox(this.form, '${type}')">
            </th>
            <th align="left" colspan="2" nowrap>
              <fmt:message key="history.namecolumnheader"/>
            </th>
            <th align="center" nowrap>
              <fmt:message key="history.datecreatedcolumnheader"/>
            </th>
            <th align="center" nowrap>
              <fmt:message key="history.countcolumnheader"/>
            </th>
            <th align="center" nowrap>
              <fmt:message key="history.startcolumnheader"/>
            </th>
            <th align="center" nowrap>
              <fmt:message key="history.summarycolumnheader"/>
            </th>
            <th align="center" nowrap>
              <fmt:message key="history.actionscolumnheader"/>
            </th>
          </tr>
          <c:forEach items="${queryMap}" var="savedQuery" varStatus="status">
            <c:if test="${!empty savedQuery.key && !empty savedQuery.value}">
              <tr>
                <td>
                  <html:multibox property="selectedQueries"
                                 styleId="selected_${type}_${status.index}"
                                 onclick="setDeleteDisabledness(this.form, '${type}')">
                    <c:out value="${savedQuery.key}" escapeXml="false"/>
                  </html:multibox>
                </td>
                <c:choose>
                  <c:when test="${!savedQuery.value.pathQuery.valid}">
                    <td align="left" colspan="2" nowrap>
                      <html:link action="/templateProblems?name=${savedQuery.key}&amp;type=saved" styleClass="brokenTmplLink">
                      <strike>${savedQuery.value.name}</strike>
                      </html:link>
                    </td>
                  </c:when>
                  <c:otherwise>
                    <tiles:insert name="historyElementName.jsp">
                      <tiles:put name="name" value="${savedQuery.key}"/>
                      <tiles:put name="type" value="${type}"/>
                    </tiles:insert>
                  </c:otherwise>
                </c:choose>
                <td align="center" nowrap>
                  <c:choose>
                    <c:when test="${savedQuery.value.dateCreated != null}">
                      <fmt:formatDate value="${savedQuery.value.dateCreated}" type="both" pattern="dd/M/yy K:mm a"/>
                    </c:when>
                    <c:otherwise>
                      n/a
                    </c:otherwise>
                  </c:choose>
                </td>
                <td align="right">
                  <c:choose>
                    <c:when test="${savedQuery.value.pathQuery.info != null}">
                      <c:out value="${savedQuery.value.pathQuery.info.rows}"/>
                    </c:when>
                    <c:otherwise>
                      n/a
                    </c:otherwise>
                  </c:choose>
                </td>
                <td align="left" nowrap>
                  <c:forEach items="${savedQuery.value.pathQuery.view}" var="item" varStatus="status">
                    <c:if test="${status.first}">
                      <c:choose>
                        <c:when test="${fn:indexOf(item, '.') > 0}">
                          <span class="historySummaryRoot">${fn:substringBefore(item, '.')}</span>
                        </c:when>
                        <c:otherwise>
                          <span class="historySummaryRoot">${item}</span>
                        </c:otherwise>
                      </c:choose>
                    </c:if>
                  </c:forEach>
                </td>
                <td align="left" nowrap>
                  <c:forEach items="${savedQuery.value.pathQuery.view}" var="item">
                    <im:unqualify className="${item}" var="text"/>
                    <span class="historySummaryShowing">${text}</span>
                  </c:forEach>
                </td>
                <td align="center" nowrap>
                  <html:link action="/modifyQueryChange?method=run&amp;name=${savedQuery.key}&amp;type=${type}"
                             titleKey="history.action.execute.hover">
                    <fmt:message key="history.action.execute"/>
                  </html:link>
                  |
                          <html:link action="/modifyQueryChange?method=load&amp;name=${savedQuery.key}&type=${type}"
                             titleKey="history.action.edit.hover">
                    <fmt:message key="history.action.edit"/>
                  </html:link>
                  |
                  <c:if test="${type == 'history'}">
                    <c:if test="${!empty PROFILE.username}">
                      <html:link action="/modifyQueryChange?method=save&amp;name=${savedQuery.key}"
                                 titleKey="history.action.save.hover">
                        <fmt:message key="history.action.save"/>
                      </html:link>
                      |
                    </c:if>
                  </c:if>
                  <html:link action="/exportQuery?name=${savedQuery.key}&amp;type=${type}"
                             titleKey="history.action.export.hover">
                    <fmt:message key="history.action.export"/>
                  </html:link>
                </td>
              </tr>
            </c:if>
          </c:forEach>
        </table>
        <br/>
        <html:submit property="delete" disabled="true" styleId="delete_button"
                     onclick="return confirmAction()">
          <fmt:message key="history.delete"/>
        </html:submit>
        <html:submit property="export" disabled="true" styleId="export_button">
          <fmt:message key="history.exportSelected"/>
        </html:submit>
        </html:form>
        <br/>

      </c:otherwise>
    </c:choose>

    <c:if test="${type == 'saved'}">
      <span class="smallnote">
        <html:link action="/importQueries" titleKey="begin.import.query">
          <fmt:message key="begin.import.query"/>
        </html:link>
      </span>
    </c:if>
  </im:body>

<!-- /historyQueryView.jsp -->
