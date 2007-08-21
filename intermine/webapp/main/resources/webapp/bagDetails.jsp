<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>

<!-- bagDetails.jsp -->
<html:xhtml/>

<script type="text/javascript">
  <!--//<![CDATA[
      var modifyDetailsURL = '<html:rewrite action="/modifyDetails"/>';
      var detailsType = 'bag';
      function jump() {
      	window.location.hash = "templates";
      	return false;
      }

      //]]>-->
</script>
<script type="text/javascript" src="js/inlinetemplate.js">
  var modifyDetailsURL = '<html:rewrite action="/modifyDetails"/>';
</script>

<div class="heading">
	<html:link action="bag.page">All Lists</html:link> > <fmt:message key="bagDetails.title"/>
</div>

<div class="body" >

	    <table border=0 cellpadding=0 cellspacing=5>
    	  <tr>
        	<td width="50%">
        	
        	
        	<table width="100%">
        	<tr>
        	<td>
        	<b>${bag.name}</b> (${bag.size} ${bag.type}s)
        	</td>
        	<td align="right">
              <%-- Page size controls --%>
			</td>
		</tr>
		</table>
          
        	
        	</td>
        	<td width="50%" align="right" valign="middle">
			  <c:if test="${!empty bag.dateCreated}">
			    <i><b>Created:</b> <im:dateDisplay date="${bag.dateCreated}" /></i>
			  </c:if>
						</td>
</tr>
<tr>
	<td width="50%" valign="top">        
	
	<html:form action="/modifyBagDetailsAction">
    	<html:hidden property="bagName" value="${bag.name}"/>
	
          <table class="results" cellspacing="0">
            <tr>
              <c:forEach var="column" items="${pagedColl.columns}" varStatus="status">
                <th align="center" valign="top">
                  <div>
                    <c:out value="${fn:replace(column.name, '.', '&nbsp;> ')}" escapeXml="false"/>
                  </div>
                </th>
              </c:forEach>
            </tr>

            <c:forEach items="${pagedColl.resultElementRows}" var="row" varStatus="status">
              <c:set var="object" value="${row[0]}" scope="request"/>
              <c:set var="rowClass">
                <c:choose>
                  <c:when test="${status.count % 2 == 1}">odd</c:when>
                  <c:otherwise>even</c:otherwise>
                </c:choose>
              </c:set>
              <tr class="${rowClass}">
                <c:forEach var="column" items="${pagedColl.columns}" varStatus="status2">
                  <td>
                    <c:set var="resultElement" value="${row[column.index]}" scope="request"/>
                    <c:choose>
                      <c:when test="${pagedColl.columnNames[column.index] == 'Gene.chromosomeLocation'}">
                      </c:when>
                      <c:when test="${resultElement.keyField}">
                        <html:link action="/objectDetails?id=${resultElement.id}&amp;trail=|bag.${bag.name}|${resultElement.id}">
                          <c:out value="${resultElement.field}" />
                        </html:link>
                      </c:when>
                      <c:otherwise>
                        <c:out value="${resultElement.field}" />
                      </c:otherwise>
                    </c:choose>
                  </td>
                </c:forEach>
              </tr>
            </c:forEach>
          </table>
          <br/>
          <div align="right">
         <html:submit property="showInResultsTable">
			View all ${bag.size} records >>
        </html:submit>
        </div>
	
		
  </td>
  <td valign="top" width="50%" align="center">
  
  <table width="90%" border=0 align="center">
  <tr>
  
	  <c:choose>
	  <c:when test="${myBag == 'true'}">
	  <td align="center">
	  <div id="clear-both"/>
	    <div id="bagDescriptionDiv" onclick="swapDivs('bagDescriptionDiv','bagDescriptionTextarea')">
	      <c:choose>
	        <c:when test="${! empty bag.description}">
	          <c:out value="${bag.description}" escapeXml="false" />
	        </c:when>
	        <c:otherwise>
	          <div id="emptyDesc">Click here to enter a description for your list.</div>
	        </c:otherwise>
	      </c:choose>
	    </div>
	    <div id="bagDescriptionTextarea">
	      <textarea id="textarea"><c:if test="${! empty bag.description}"><c:out value="${fn:replace(bag.description,'<br/>','')}" /></c:if></textarea>
	      <div align="right">
	        <button onclick="swapDivs('bagDescriptionTextarea','bagDescriptionDiv'); return false;">Cancel</button>
	        <button onclick="saveBagDescription('${bag.name}'); return false;">Save</button>
	      </div>
	    </div>

	</c:when>
	<c:otherwise>	
	<td>
		 <b>Description:</b> ${bag.description}
	</c:otherwise>
	</c:choose>
  </td>
  </tr>
  <tr>
  <td>
  
  <br/>  <br/>  <br/>

  <span style="font-size:+2em;">View</span>
  	<li>all records in this list
  	   <!--  <html:submit property="showInResultsTable">
			all records in this list
        </html:submit>  -->
  	</li>
  	<li><a href="#templates">related templates</a></li>
  	<li><html:link action="/bag">all lists</html:link></li>
  	
  <span style="font-size:+2em;">Use</span>		
	<li>in a query
  	    <!-- <html:submit property="useBagInQuery">
			in a query
        </html:submit> -->
  	</li>
		<li><html:link action="/templates">in a template</html:link></li>
		
  <span style="font-size:+2em;">Export</span>

		<li><a href="exportAction.do?table=${bag.name}&type=tab&tableType=bag">tab-separated</a></li>
		<li><a href="exportAction.do?table=${bag.name}&type=csv&tableType=bag">comma-separated</a></li>
		<li><a href="exportAction.do?table=${bag.name}&type=excel&tableType=bag">excel</a></li>
		<li><a href="exportAction.do?table=${bag.name}&type=gff3Exporter&tableType=bag">gff3</a></li>
		
  <span style="font-size:+2em;">Upload</span>
		<li><html:link action="/bag">your data</html:link></li>
		

</td>
</tr>
</table>
	
  </td>
  
  
  </tr>
  </table>
  </html:form>
</div>

<br/>

<!-- widget table -->
<table border=0 cellpadding="0" cellspacing="10">

  <c:if test="${(!empty graphDisplayerArray) || (! empty tableDisplayerArray)}">
    <c:set var="widgetCount" value="0" />
    <div class="heading">
      Viewers
    </div>
    <div class="body">
      <c:forEach items="${graphDisplayerArray}" var="htmlContent">
        <c:choose>
          <c:when test="${widgetCount % 2 == 0}">
            <tr valign="top"><td>
          </c:when>
          <c:otherwise>
            <td>
          </c:otherwise>
        </c:choose>
        <div class="widget">
          <c:out value="${htmlContent[0]}" escapeXml="false"/>
          <p><c:out value="${htmlContent[1]}" escapeXml="false"/></p>
        </div>
        <c:choose>
          <c:when test="${widgetCount % 2 == 0}">
        </td>
        </c:when>
        <c:otherwise>
        </td></tr>
        </c:otherwise>
        </c:choose>
        <c:set var="widgetCount" value="${widgetCount+1}" />
      </c:forEach>

      <c:forEach items="${tableDisplayerArray}" var="bagTableDisplayerResults">

        <c:choose>
          <c:when test="${widgetCount % 2 == 0}">
            <tr valign="top"><td>
          </c:when>
          <c:otherwise>
            <td>
          </c:otherwise>
        </c:choose>

        <div class="widget">
          <div><strong><font size="+1"><c:out value="${bagTableDisplayerResults.title}"/></font></strong></div>
          <c:choose>
            <c:when test="${!empty bagTableDisplayerResults.flattenedResults}">
             
              <div class="widget_slide" align="center">
                <table class="results" cellspacing="0">
                  <tr>
                    <c:forEach var="column" items="${bagTableDisplayerResults.columns}" varStatus="status">
                      <th align="center" valign="top">
                        <div>
                          <c:out value="${fn:replace(column, '.', '&nbsp;> ')}" escapeXml="false"/>
                        </div>
                      </th>
                    </c:forEach>
                  </tr>

                  <c:forEach items="${bagTableDisplayerResults.flattenedResults}" var="row" varStatus="status">
                    <c:set var="rowClass">
                      <c:choose>
                        <c:when test="${status.count % 2 == 1}">odd</c:when>
                        <c:otherwise>even</c:otherwise>
                      </c:choose>
                    </c:set>
                    <tr class="${rowClass}">
                      <c:forEach var="cell" items="${row}" varStatus="status2">
                        <td>
                          <c:choose>
                            <c:when test="${cell.keyField}">
                              <html:link action="/objectDetails?id=${cell.id}&amp;trail=|bag.${bag.name}|${cell.id}">
                                <c:out value="${cell.field}" />
                              </html:link>
                            </c:when>
                            <c:when test="${! empty cell.otherLink}">
                              <html:link action="/bagTableWidgetResults?${cell.otherLink}">
                                <c:out value="${cell.field}" />
                              </html:link>
                            </c:when>
                            <c:otherwise>
                              <c:out value="${cell.field}" />
                            </c:otherwise>
                          </c:choose>
                        </td>
                      </c:forEach>
                    </tr>
                  </c:forEach>
                </table>
                <p><c:out value="${bagTableDisplayerResults.description}" escapeXml="false"/></p>
              </div>
            </c:when>
            <c:otherwise><i>No results for ${bagTableDisplayerResults.title}</i></c:otherwise>
          </c:choose>
        </div>
        <c:choose>
          <c:when test="${widgetCount % 2 == 0}">
        </td>
        </c:when>
        <c:otherwise>
        </td></tr>
        </c:otherwise>
        </c:choose>
        <c:set var="widgetCount" value="${widgetCount+1}" />

      </c:forEach>

    </div>
  </c:if>

  <c:if test="${bag.type == 'Gene'}">

<tr>
	<td>

    <%-- go stats --%>
 	<iframe src="goStatDisplayer.do?bagName=${bag.name}" id="window" frameborder="0" width="475" height="500" scrollbars="auto"></iframe>
 	
	</td><td>
  
    <%-- protein domains --%>
 	<iframe src="proteinDomainDisplayer.do?bagName=${bag.name}" id="window" frameborder="0" width="475" height="500" scrollbars="auto"></iframe>
 	
    </td>
</tr>

    <c:set var="widgetCount" value="${widgetCount+2}" />
  </c:if>

</table>
<!-- /widget table -->


<div class="heading">
  <a name="templates">Templates</a>&nbsp;&nbsp;&nbsp;<span style="font-size:0.8em;">(collapse all - / expand all +)</span>
</div>

<div class="body">

  <%-- Each aspect --%>
  <c:forEach items="${CATEGORIES}" var="aspect">
    <tiles:insert name="objectDetailsAspect.tile">
      <tiles:put name="placement" value="aspect:${aspect}"/>
      <tiles:put name="trail" value="|bag.${bag.name}"/>
      <tiles:put name="interMineIdBag" beanName="bag"/>
    </tiles:insert>
  </c:forEach>
</div>

<!-- /bagDetails.jsp -->
