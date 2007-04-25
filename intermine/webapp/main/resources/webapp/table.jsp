<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>

<!-- table.jsp -->

<tiles:importAttribute/>
<html:xhtml/>

<tiles:get name="objectTrail.tile"/><im:vspacer height="3"/>

<c:set var="isPagedResults"
       value="${resultsTable.class.name == 'org.intermine.web.logic.results.PagedResults'}"/>

<script type="text/javascript" src="js/table.js" ></script>
<script type="text/javascript">
<!--//<![CDATA[
  function changePageSize() {
    var url = '${requestScope['javax.servlet.include.context_path']}/results.do?';
    var pagesize = document.changeTableSizeForm.pageSize.options[document.changeTableSizeForm.pageSize.selectedIndex].value;
    var page = ${resultsTable.startRow}/pagesize;
    url += 'table=${param.table}' + '&page=' + Math.floor(page) + '&size=' + pagesize;
    if ('${param.trail}' != '') {
        url += '&trail=${param.trail}';
    }
    document.location.href=url;
  }
//]]>-->
</script>
<c:choose>
  <c:when test="${resultsTable.size == 0}">
    <div class="body">
      <fmt:message key="results.pageinfo.empty"/><br/>
    </div>
  </c:when>
  <c:otherwise>

    <c:if test="${!empty templateQuery}">

      <%-- show the description only if we've run a query (rather than viewing
           a bag) - see #1031 --%>
      <c:if test="${isPagedResults
                  && (templateQuery.name != WEB_PROPERTIES['begin.browse.template'])}">
        <div class="body">
          <div class="resultsTableTemplateHeader">
            <div>
              <fmt:message key="results.templateTitle"/>:
              <span class="templateTitleBold">
                ${templateQuery.title}
              </span>
            </div>
            <div class="templateDescription">
              ${templateQuery.description}
            </div>
          </div>
        </div>
      </c:if>
    </c:if>

    <div class="body">
      <table cellpadding="0" cellspacing="0" >
        <tr>
          <td><img src="model/res_bar_left.gif"></td>
          <td class="resBar"><tiles:insert page="/tablePageLinks.jsp"/></td>
          <td class="resBar"><img src="images/blank.gif" width="10px">|<img src="images/blank.gif" width="10px"></td>
          <td class="resBar">
            <html:form action="/changeTableSize">
              <%-- Page size controls --%>
              <fmt:message key="results.changepagesize"/>
              <html:select property="pageSize" onchange="changePageSize()">
                <html:option value="10">10</html:option>
                <html:option value="25">25</html:option>
                <html:option value="50">50</html:option>
                <html:option value="100">100</html:option>
              </html:select>
              <input type="hidden" name="table" value="${param.table}"/>
              <input type="hidden" name="trail" value="${param.trail}"/>
              <noscript>
                <html:submit>
                  <fmt:message key="button.change"/>
                </html:submit>
              </noscript>
            </html:form>
          </td>
          <td><img src="model/res_bar_right.gif"></td>
          <td><img src="images/blank.gif" width="20px"></td>
          <td style="padding:5px;font-size:10px;background-color:#EEEEEE;">
            <strong>Legend: </strong>
            <img style="vertical-align:text-bottom;" border="0"
                 width="13" height="13" src="images/left_arrow.png"
                 alt="${moveLeftString}"/>
            &nbsp;&amp;&nbsp;<img style="vertical-align:text-bottom;" border="0"
                                  width="13" height="13" src="images/right_arrow.png"
                                  alt="${moveLeftString}"/>
            Move columns - <img style="vertical-align:text-bottom;" border="0"
                                src="images/close.png" title="${hideColumnTitle}" />
            Close column -
            <img src="images/summary_maths.png" style="vertical-align:text-bottom;" alt="Column Summary">
              Get summary statistics for column
          </td>
        </tr>
      </table>
    </div>


    <html:form action="/saveBag">
      <div class="body">

        <table class="results" cellspacing="0">

          <%-- The headers --%>
          <tr>
            <c:forEach var="column" items="${resultsTable.columns}" varStatus="status">

              <c:choose>
                <c:when test="${column.visible}">
                  <c:if test="${column.selectable}">
                    <th align="center" class="checkbox">
                      <html:multibox property="selectedObjects" styleId="selectedObjects_${status.index}"
                                     onclick="selectColumnCheckbox(${status.index}, '${column.columnId}')"
                                     disabled="${resultsTable.maxRetrievableIndex > resultsTable.size ? 'false' : 'true'}">
                        <c:out value="${status.index},${column.columnId}"/>
                      </html:multibox>
                    </th>
                  </c:if>

                  <th align="center" valign="top" >

                    <%-- put in left, right, hide and show buttons --%>
                    <div align="right" style="margin-right:0px;margin-top:0px;white-space:nowrap;">
                      <%-- left --%>
                      <c:if test="${not status.first}">
                        <fmt:message key="results.moveLeftHelp" var="moveLeftTitle">
                          <fmt:param value="${column.name}"/>
                        </fmt:message>
                        <fmt:message key="results.moveLeftSymbol" var="moveLeftString"/>
                        <html:link action="/changeTable?table=${param.table}&amp;method=moveColumnLeft&amp;index=${status.index}&amp;trail=${param.trail}"
                                   title="${moveLeftTitle}">
                          <img style="vertical-align:top;" border="0"
                               width="13" height="13" src="images/left_arrow.png"
                               alt="${moveLeftString}"/>
                        </html:link>
                      </c:if>

					<fmt:message key="columnsummary.getsummary" var="summaryTitle" />
                    <a href="javascript:getColumnSummary('${column.name}')" title="${summaryTitle}"><img src="images/summary_maths.png" alt="${summaryTitle}"></a>

                      <%-- right --%>
                      <c:if test="${not status.last}">
                        <fmt:message key="results.moveRightHelp" var="moveRightTitle">
                          <fmt:param value="${column.name}"/>
                        </fmt:message>
                        <fmt:message key="results.moveRightSymbol" var="moveRightString"/>
                        <html:link action="/changeTable?table=${param.table}&amp;method=moveColumnRight&amp;index=${status.index}&amp;trail=${param.trail}"
                                   title="${moveRightTitle}">
                          <img style="vertical-align:top;" border="0"
                               width="13" height="13"
                               src="images/right_arrow.png" alt="${moveRightString}"/>
                        </html:link>
                      </c:if>

                      <%-- show/hide --%>
                      <c:if test="${fn:length(resultsTable.columns) > 1}">
                        <c:if test="${resultsTable.visibleColumnCount > 1}">
                          <fmt:message key="results.hideColumnHelp" var="hideColumnTitle">
                            <fmt:param value="${column.name}"/>
                          </fmt:message>
                          <html:link action="/changeTable?table=${param.table}&amp;method=hideColumn&amp;index=${status.index}&amp;trail=${param.trail}"
                                     title="${hideColumnTitle}">
                            <img style="vertical-align:top;" border="0"
                                 src="images/close.png" title="${hideColumnTitle}" />
                          </html:link>
                        </c:if>
                      </c:if>

                    </div>
                    <div>
                      <c:set var="displayPath" value="${fn:replace(column.name, '.', '&nbsp;> ')}"/>
                      <im:unqualify className="${column.name}" var="pathEnd"/>
                      <im:prefixSubstring str="${column.name}" outVar="pathPrefix" delimiter="."/>
                      <c:choose>
                        <c:when test="${!empty QUERY
                                      && !empty QUERY.pathStringDescriptions[pathPrefix]}">
                          <span class="viewPathDescription"
                                title="${displayPath}">${QUERY.pathStringDescriptions[pathPrefix]}</span>
                          &gt; ${pathEnd}
                        </c:when>
                        <c:otherwise>
                          <c:out value="${displayPath}" escapeXml="false"/>
                        </c:otherwise>
                      </c:choose>
                    </div>
                  </th>
                </c:when>
                <c:otherwise>
                  <th>
                    <%-- <fmt:message key="results.showColumnHelp" var="showColumnTitle">
                         <fmt:param value="${column.name}"/>
                         </fmt:message> --%>
                    <html:link action="/changeTable?table=${param.table}&amp;method=showColumn&amp;index=${status.index}&amp;trail=${param.trail}"
                               title="${showColumnTitle}">
                      <img src="images/show-column.gif" title="${fn:replace(column.name, '.', '&nbsp;> ')}"/>
                    </html:link>
                  </th>
                </c:otherwise>
              </c:choose>
            </c:forEach>
          </tr>

          <%-- The data --%>

          <%-- Row --%>
          <c:if test="${resultsTable.size > 0}">
            <c:forEach var="row" items="${resultsTable.rows}" varStatus="status">

              <c:set var="rowClass">
                <c:choose>
                  <c:when test="${status.count % 2 == 1}">odd</c:when>
                  <c:otherwise>even</c:otherwise>
                </c:choose>
              </c:set>

              <tr class="<c:out value="${rowClass}"/>">
                <c:forEach var="column" items="${resultsTable.columns}" varStatus="status2">

                  <c:choose>
                    <c:when test="${column.visible}">
                      <%-- the checkbox to select this object --%>
                      <c:if test="${column.selectable}">
                        <td align="center" class="checkbox" id="cell_checkbox,${status2.index},${status.index},${row[column.index].htmlId}">
                          <html:multibox property="selectedObjects"
                                         styleId="selectedObjects_${status2.index}_${status.index}_${row[column.index].htmlId}"
                                         onclick="itemChecked(${status.index},${status2.index}, '${row[column.index].htmlId}',this)">
                            <c:out value="${status2.index},${status.index},${row[column.index].htmlId}"/>
                          </html:multibox>
                        </td>
                      </c:if>
                      <td id="cell,${status2.index},${status.index},${row[column.index].htmlId}">
                        <c:set var="resultElement" value="${row[column.index]}" scope="request"/>
                        <c:set var="columnType" value="${column.type}" scope="request"/>
                        <tiles:insert name="objectView.tile" />
                      </td>
                    </c:when>
                    <c:otherwise>
                      <%-- add a space so that IE renders the borders --%>
                      <td style="background:#eee;">&nbsp;</td>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </tr>
            </c:forEach>
          </c:if>
        </table>

        <%--  The Summary table --%>
        <div id="summary" style="display:none;" >
        <div>
          <div align="right" ><img onclick="javascript:Effect.Fade('summary', { duration: 0.30 });" src="images/close.png" title="Close" 
          onmouseout="this.style.cursor='normal';" onmouseover="this.style.cursor='pointer';"></div>
        <div id="summary_loading">Loading...</div>
        <div id="summary_loaded" style="display:none;" >
          <p style="margin-top:5px;"><strong><fmt:message key="columnsummary.name" /></strong></p>
          <p><fmt:message key="columnsummary.message" /></p>
          <table class="results" cellpadding="0" cellspacing="0">
            <thead id="summary_head"></thead>
            <tbody id="summary_table"></tbody>
          </table>
        </div>
       </div></div>

        <c:if test="${resultsTable.size > 1}">
          <br/>
          <table cellpadding="0" cellspacing="0" >
            <tr>
              <td><img src="model/res_bar_left.gif"></td>
              <%-- Paging controls --%>
              <td class="resBar"><tiles:insert page="/tablePageLinks.jsp"/></td>
              <td class="resBar">&nbsp;|&nbsp;</td>
              <td class="resBar">
                <%-- "Displaying xxx to xxx of xxx rows" messages --%>
                <c:choose>
                  <c:when test="${resultsTable.sizeEstimate}">
                    <tiles:insert name="tableCountEstimate.tile">
                      <tiles:put name="pagedTable" beanName="resultsTable"/>
                    </tiles:insert>
                  </c:when>
                  <c:otherwise>
                    <c:choose>
                      <c:when test="${resultsTable.startRow == 0 &&
                                    resultsTable.endRow == resultsTable.size - 1}">
                        <fmt:message key="results.pageinfo.allrows">
                          <fmt:param value="${resultsTable.size}"/>
                        </fmt:message>
                      </c:when>
                      <c:otherwise>
                        <fmt:message key="results.pageinfo.rowrange">
                          <fmt:param value="${resultsTable.startRow+1}"/>
                          <fmt:param value="${resultsTable.endRow+1}"/>
                        </fmt:message>
                        <span class="resBar">&nbsp;|&nbsp;</span>
                        <fmt:message key="results.pageinfo.exact"/>
                        ${resultsTable.size}
                      </c:otherwise>
                    </c:choose>
                  </c:otherwise>
                </c:choose>
              </td>
              <td><img src="model/res_bar_right.gif"/></td>
            </tr>
          </table>
        </c:if>

        <%-- Return to main results link
             <c:if test="${!isPagedResults
                         && QUERY_RESULTS != null && !fn:startsWith(param.table, 'bag')}">
               <p>
                 <html:link action="/results?table=results">
                   <fmt:message key="results.return"/>
                 </html:link>
               </p>
             </c:if>
             --%>

      </div> <%-- end of main results table body div --%>

      <%-- Save bag controls --%>
      <c:if test="${resultsTable.size > 0}">
        <div class="heading">
          <fmt:message key="results.save"/><im:manualLink section="manualResults.shtml"/>
        </div>
        <div class="body">
          <fmt:message key="bag.save.msg"/>
          <ul>
            <li>
              <fmt:message key="bag.new"/>
              <html:text property="newBagName"/>
              <input type="hidden" name="__intermine_forward_params__" value="${pageContext.request.queryString}"/>
              <input type="hidden" name="table" value="${param.table}"/>
              <html:submit property="saveNewBag">
                <fmt:message key="button.save"/>
              </html:submit>
            </li>
            <c:if test="${!empty PROFILE.savedBags}">
              <li>
                <fmt:message key="bag.existing"/>
                <html:select property="existingBagName">
                  <c:forEach items="${PROFILE.savedBags}" var="entry">
                    <html:option value="${entry.key}"/>
                  </c:forEach>
                </html:select>
                <html:submit property="addToExistingBag">
                  <fmt:message key="button.add"/>
                </html:submit>
              </li>
            </c:if>
          </ul>
        </div>
      </c:if>
    </html:form>

    <c:set var="tableName" value="${param.table}" scope="request"/>
    <c:set var="tableType" value="results" scope="request"/>
    <c:set var="pagedTable" value="${resultsTable}" scope="request"/>
    <tiles:get name="export.tile"/>
  </c:otherwise>
</c:choose>
<!-- /table.jsp -->
