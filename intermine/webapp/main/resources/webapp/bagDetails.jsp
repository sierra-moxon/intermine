<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>

<!-- bagDetails.jsp -->
<html:xhtml/>
<link rel="stylesheet" href="css/resultstables.css" type="text/css" />
<script type="text/javascript">
  <!--//<![CDATA[
      var modifyDetailsURL = '<html:rewrite action="/modifyDetails"/>';
      var detailsType = 'bag';

	function go(where) {
		switch (where){
			case "query":
				document.forms[1].useBagInQuery.value = 'true';
				break;
		}		
    	document.forms[1].submit();
   	}


      //]]>-->
</script>
<script type="text/javascript" src="js/inlinetemplate.js">
  var modifyDetailsURL = '<html:rewrite action="/modifyDetails"/>';
</script>


<c:if test="${!empty lookupResults}">
<div class="lookupReport">
   <tiles:insert name="bagRunnerMsg.tile">
      <tiles:put name="lookupResults" beanName="lookupResults" />
    </tiles:insert>
</div> <%-- lookupReport --%>
</c:if>

<div class="heading">
   	<b>${bag.name}</b> (${bag.size} ${bag.type}s)
</div>


<table border=0 cellpadding=0 cellspacing=5>
<tr>
	<td width="50%" valign="top">        
	
	<table>
	<tr>
	<td colspan=2>
	<html:form action="/modifyBagDetailsAction" styleId="bagDetailsForm">
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
            <!-- show dotted lines if there are more than 5 items in bag -->            
            <c:if test="${pagedColl.pageSize < pagedColl.size}">
              <tr>
                <c:forEach var="column" items="${pagedColl.columns}" varStatus="status2">
                  <td style="border-right: dotted 1px #666; border-bottom: dotted 1px #666;">&nbsp;</td>
                  </c:forEach>
               </tr>
            </c:if>
            
          </table>
         </td></tr>
         <tr><td>
          
<c:if test="${!empty bag.dateCreated}">
    <i><b>Created:</b> <im:dateDisplay date="${bag.dateCreated}" /></i>
</c:if>

</td><td align="right">          

         <html:submit property="showInResultsTable">
			View all ${bag.size} records >>
        </html:submit>
</td>
</tr>
</table>
	
		
  </td>
  <td valign="top" width="50%" align="center">
  
  <table width="90%" border=0 align="center">
  <tr>
  
	  <c:choose>
	  <c:when test="${myBag == 'true'}">
	  <td>
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

<table cellpadding="0" cellspacing="30"><tr>
<td valign="top">  	
  <span style="font-size:+2em;">Use</span><br/>		
  	<a href="javascript:go('query');">in a query</a><br/>
	<input type="hidden" name="useBagInQuery" />
	<html:link action="/templates">in a template</html:link>
</td><td valign="top">
  <span style="font-size:+2em;">Export</span><br/>
  		<a href="exportAction.do?table=${bag.name}&type=tab&tableType=bag">tab-separated</a><br/>
		<a href="exportAction.do?table=${bag.name}&type=csv&tableType=bag">comma-separated</a><br/>
		<a href="exportAction.do?table=${bag.name}&type=excel&tableType=bag">excel</a>
</td><td valign="top">
  <span style="font-size:+2em;">View</span><br/>
  	<html:link anchor="relatedTemplates" action="bagDetails?bagName=${bag.name}">related templates</html:link><br/>
  	<html:link anchor="widgets" action="bagDetails?bagName=${bag.name}">related widgets</html:link>
</td><td valign="top">
  <span style="font-size:+2em;">Upload</span><br/>
  <html:link action="/bag?subtab=upload">your data</html:link>
</td>
</tr>
</table>


</td>
</tr>
</table>
	
  </td>  
  </tr>
  </table>
  </html:form>


<br/>

<!-- widget table -->
<c:set var="widgetTotal" value="10"/>

  <c:if test="${(!empty graphDisplayerArray) || (! empty tableDisplayerArray) 
											 || (!empty enrichmentWidgetDisplayerArray)}">
    <div class="heading">
      <a id="widgets">Widgets - displaying properties of '${bag.name}'</a>&nbsp;&nbsp;<span style="font-size:0.8em;">
		(<a href="javascript:toggleAll(${widgetTotal}, 'widget', 'expand', null);">expand all <img src="images/disclosed.gif"/></a> / <a href="javascript:toggleAll(${widgetTotal}, 'widget', 'collapse', null);">collapse all <img src="images/undisclosed.gif"/></a>)</span>
    </div>
    <div class="body">
      <fmt:message key="bagDetails.widgetHelp">
         <c:choose>
           <c:when test="${(!empty graphDisplayerArray) && (! empty tableDisplayerArray)}">
              <fmt:param>
                <fmt:message key="bagDetails.widgetHelpBoth"/>
              </fmt:param>
           </c:when>
           <c:when test="${(!empty graphDisplayerArray) && (empty tableDisplayerArray)}">
              <fmt:param>
                <fmt:message key="bagDetails.widgetHelpGraph"/>
              </fmt:param>
           </c:when>
           <c:when test="${(empty graphDisplayerArray) && (! empty tableDisplayerArray)}">
              <fmt:param>
                <fmt:message key="bagDetails.widgetHelpTable"/>
              </fmt:param>
           </c:when>
         </c:choose>
      </fmt:message>
      
      <c:set var="widgetCount" value="0"/>
      
      <%-- graphs --%>
      <c:forEach items="${graphDisplayerArray}" var="htmlContent">
      <div class="body">
		<im:heading id="widget${widgetCount}">    
			<a href="javascript:toggleHidden('widget${widgetCount}');">${htmlContent[1]}</a>
		</im:heading>
        <div class="tog" id="widget${widgetCount}">
        <div class="widget">
          <c:out value="${htmlContent[0]}" escapeXml="false"/>
          <p><c:out value="${htmlContent[2]}" escapeXml="false"/></p>
        </div>
        </div>      
        <c:set var="widgetCount" value="${widgetCount+1}" />
         </div>
      </c:forEach>

	<%-- tables --%>
      <c:forEach items="${tableDisplayerArray}" var="bagTableDisplayerResults">
      	<div class="body">
			<im:heading id="widget${widgetCount}"> 
      			<a href="javascript:toggleHidden('widget${widgetCount}');"><c:out value="${bagTableDisplayerResults.title}"/></a>
    		</im:heading>
    	  <div id="widget${widgetCount}" class="tog">
          <div><strong><font size="+1"><c:out value="${bagTableDisplayerResults.title}"/></font></strong></div>
          <c:choose>
            <c:when test="${!empty bagTableDisplayerResults.flattenedResults}">
                <div class="widget_slide">
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
        <c:set var="widgetCount" value="${widgetCount+1}" />
        </div>
      </c:forEach>

    
	 <%-- enrichment --%>
	<c:forEach items="${enrichmentWidgetDisplayerArray}" var="enrichmentWidgetResults">
	<div class="body">
		<im:heading id="widget${widgetCount}"> 
			<a href="javascript:toggleHidden('widget${widgetCount}');"><c:out value="${enrichmentWidgetResults.title}"/></a>
		</im:heading>
		<div id="widget${widgetCount}" class="tog">
			<iframe src="enrichmentWidget.do?bagName=${bag.name}&controller=${enrichmentWidgetResults.controller}&title=${enrichmentWidgetResults.title}&description=${enrichmentWidgetResults.description}&max=${enrichmentWidgetResults.max}&link=${enrichmentWidgetResults.link}&filters=${enrichmentWidgetResults.filters}&filterLabel=${enrichmentWidgetResults.filterLabel}&externalLink=${enrichmentWidgetResults.externalLink}&append=${enrichmentWidgetResults.append}" id="window" frameborder="0" width="475" height="500" scrollbars="auto"></iframe>
		</div>
	 	<c:set var="widgetCount" value="${widgetCount+1}" />
	 	 </div>
	</c:forEach>
	</div>
</c:if>

<!-- /widgets -->


<c:set value="${fn:length(CATEGORIES)}" var="aspectCount"/>
<div class="heading">
   <a id="relatedTemplates">Template results for '${bag.name}' &nbsp;</a>&nbsp;&nbsp;<span style="font-size:0.8em;"> 
  (<a href="javascript:toggleAll(${aspectCount}, 'template', 'expand', null);">expand all <img src="images/disclosed.gif"/></a> / <a href="javascript:toggleAll(${aspectCount}, 'template', 'collapse', null);">collapse all <img src="images/undisclosed.gif"/></a>)</span></div>
</div>

<div class="body">
  <fmt:message key="bagDetails.templatesHelp">
    <fmt:param>
      <img border="0" src="images/plus.gif" height="11" width="11"/>
    </fmt:param>
  </fmt:message>
  <br/>
  <br/>
  <%-- Each aspect --%>
  <c:forEach items="${CATEGORIES}" var="aspect" varStatus="status">
  <div class="body">
    <tiles:insert name="objectDetailsAspect.tile">
      <tiles:put name="placement" value="aspect:${aspect}"/>
      <tiles:put name="trail" value="|bag.${bag.name}"/>
      <tiles:put name="interMineIdBag" beanName="bag"/>
      <tiles:put name="index" value="${status.index}" />
    </tiles:insert>
    </div>
  </c:forEach>
</div>

<script type="text/javascript">
  <!--//<![CDATA[
	toggleAll(${widgetCount}, 'widget', 'collapse', null);
	toggleAll(${aspectCount}, 'template', 'collapse', null);
      //]]>-->
</script>

<!-- /bagDetails.jsp -->
