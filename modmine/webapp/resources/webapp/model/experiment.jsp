<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>
<%@ taglib uri="http://flymine.org/imutil" prefix="imutil"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/string-1.1"
    prefix="str"%>

<!-- experiment.jsp -->

<html:xhtml />
<script type="text/javascript" src="<html:rewrite page='/js/jquery.qtip-1.0.0-rc3.min.js'/>"></script>
<script type="text/javascript" src="js/tablesort.js"></script>
<link rel="stylesheet" type="text/css" href="css/sorting_experiments.css"/>     
            
<style type="text/css">

.dbsources table.features {
  clear:left;
  font-size: small;
  border: none;
  background-color: green;
}

.dbsources table.features td {
  white-space:nowrap;
  padding: 3px; 
  border-left:1px solid;
  border-right: none;
  border-bottom: none;
  border-top:none;
  background-color: #DFA;
}

.dbsources table.features .firstrow {
  white-space:nowrap;
  padding: 3px; 
  border-top:none;
}

.dbsources table.features .firstcolumn {
  white-space: nowrap;
  padding: 3px;
  border-left: none;
}

div#experimentFeatures {
  color: black;
  margin: 20px;
  border: 1px;
  border-style: solid;
  border-color: green;
  background-color: #DFA;
  padding: 5px;
}

.submissionFeatures {
  color: black;
  margin-bottom: 20px;
  border: 1px;
  border-style: solid;
  border-color: green;
  background-color: #DFA;
  padding: 5px;
}

.submissions div {
  clear: both;
}

.tinylink {
  line-height:1em;
  font-size: 8px;
}

.tinylink a {
  color:black;
}

</style>


<tiles:importAttribute />




<div class="body">

<c:set var="urlPrefix" value="http://submit.modencode.org/submit/public/get_file/"/>


<c:forEach items="${experiments}" var="exp"  varStatus="status">
<%-- for gbrowse: to modify and take the organism from the submission --%>
<c:set var="fly" value=""/>
<c:set var="worm" value=""/>

  <im:boxarea title="${exp.name}" stylename="gradientbox">

  <table cellpadding="0" cellspacing="0" border="0" class="dbsources">
  <tr><td>
    <c:forEach items="${exp.organisms}" var="organism" varStatus="orgStatus">
      <c:if test="${organism eq 'D. melanogaster'}"> 
        <img border="0" class="arrow" src="model/images/f_vvs.png" title="fly"/><br>
						<c:set var="fly" value="1" />
					</c:if>
      <c:if test="${organism eq 'C. elegans'}"> 
        <img border="0" class="arrow" src="model/images/w_vvs.png" title="worm"/><br>
						<c:set var="worm" value="1" />
					</c:if>
    </c:forEach> 
  </td>
  
  <td>experiment: <b><c:out value="${exp.name}"/></b></td>
  <td>project: <b><c:out value="${exp.projectName}"></c:out></b></td>
  <td>PI: <b><c:out value="${exp.pi}"></c:out></b></td>
  </tr>
  
  
  <tr>
  <td colspan="4"><c:out value="${exp.description}"></c:out></td>
  </tr>
  </table>


  <c:if test="${! empty exp.featureCounts || !empty tracks[exp.name]}">
  <div id="experimentFeatures">
  
  <br/>
     <table>
     <tr>
     <td style="width: 45%" align="top">
      <c:choose>
      <c:when test="${!empty exp.featureCounts}">
      
      ALL FEATURES GENERATED BY THIS EXPERIMENT:
      <table cellpadding="0" cellspacing="0" border="0" class="dbsources">
      <tr>
        <th>Feature type</th>
        <th>Count</th>
        <th>View data</th>
        <th colspan="4">Export</th>
      </tr>
      <c:forEach items="${exp.featureCounts}" var="fc" varStatus="status">
          <tr>
            <td>${fc.key}</td>
            <td>${fc.value}</td>                     
            <td align="center">
              <html:link
        href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=experiment&action=results&experiment=${exp.name}&feature=${fc.key}">VIEW RESULTS</html:link>
            </td>
            <td align="center">
               <html:link
        href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=experiment&action=export&experiment=${exp.name}&feature=${fc.key}&format=tab">TAB
               </html:link>
            
            </td>
            <td align="center">
              <html:link
        href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=experiment&action=export&experiment=${exp.name}&feature=${fc.key}&format=csv">CSV</html:link>
           
            </td>
            <td align="center">
             <html:link
        href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=experiment&action=export&experiment=${exp.name}&feature=${fc.key}&format=gff3">GFF3</html:link>
             <html:link
        href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=experiment&action=export&experiment=${exp.name}&feature=${fc.key}&format=sequence">SEQ</html:link>
           
            </td>
          </tr>
      </c:forEach>
      <!-- end submission loop -->
    </table>
    </c:when>
    <c:otherwise>
     NO FEATURES GENERATED FOR THIS SUBMISSION
    </c:otherwise>
    </c:choose>
   </td>
   <td style="width: 40%; align: top;">
     <c:choose>
     <c:when test="${!empty tracks[exp.name]}">
     <c:set var="urlabels" value=""/>
     <c:set var="baseUrl" value="http://modencode.oicr.on.ca/cgi-bin/gb2/gbrowse/"/>

      <table cellpadding="0" cellspacing="0" border="0" class="dbsources">
      <tr>
        <th>
       <c:choose>
         <c:when test="${fn:length(tracks[exp.name]) == 1}">
           <c:out value="${fn:length(tracks[exp.name])}"/> GBrowse track
         </c:when>
         <c:otherwise>
           <c:out value=" "/> GBrowse tracks:
         </c:otherwise>
       </c:choose>
     </th>
     <th>by chromosome</th>
     <c:forEach items="${tracks[exp.name]}" var="etrack"  varStatus="status">
     
<%-- build the url for getting all the labels in this experiment --%> 
<c:choose>
<c:when test="${status.first}">
     <c:set var="urlabels" value="${etrack.track}" /> 
</c:when>
<c:otherwise>
     <c:set var="urlabels" value="${urlabels}-${etrack.track}" /> 
</c:otherwise>
</c:choose>

     <tr><td>
<html:link
     href="${baseUrl}${etrack.organism}/?label=${etrack.track}" target="_blank">${etrack.track}
</html:link>
</td>
<td>
<c:if test="${etrack.organism eq 'fly'}">
<html:link href="${baseUrl}${etrack.organism}/?ref=X;label=${etrack.track}" target="_blank">X</html:link>
 | 
<html:link href="${baseUrl}${etrack.organism}/?ref=2L;label=${etrack.track}" target="_blank">2L</html:link>
 | 
<html:link href="${baseUrl}${etrack.organism}/?ref=2R;label=${etrack.track}" target="_blank">2R</html:link>
 | 
<html:link href="${baseUrl}${etrack.organism}/?ref=3L;label=${etrack.track}" target="_blank">3L</html:link>
 | 
<html:link href="${baseUrl}${etrack.organism}/?ref=3R;label=${etrack.track}" target="_blank">3R</html:link>
 | 
<html:link href="${baseUrl}${etrack.organism}/?ref=4;label=${etrack.track}" target="_blank">4</html:link>
 | 
<html:link href="${baseUrl}${etrack.organism}/?ref=U;label=${etrack.track}" target="_blank">U</html:link>
</c:if>
<c:if test="${etrack.organism eq 'worm'}">
<html:link href="${baseUrl}${etrack.organism}/?ref=I;label=${etrack.track}" target="_blank">I</html:link>
 | 
<html:link href="${baseUrl}${etrack.organism}/?ref=II;label=${etrack.track}" target="_blank">II</html:link>
 | 
<html:link href="${baseUrl}${etrack.organism}/?ref=III;label=${etrack.track}" target="_blank">III</html:link>
 | 
<html:link href="${baseUrl}${etrack.organism}/?ref=IV;label=${etrack.track}" target="_blank">IV</html:link>
 | 
<html:link href="${baseUrl}${etrack.organism}/?ref=V;label=${etrack.track}" target="_blank">V</html:link>
 | 
<html:link href="${baseUrl}${etrack.organism}/?ref=X;label=${etrack.track}" target="_blank">X</html:link>
</c:if>

</td>
</tr>
</c:forEach>
    
    
<c:if test="${fn:length(tracks[exp.name]) > 1 }">
 <c:if test="${!empty fly}">
<tr>
<td><b><html:link 
     href="${baseUrl}fly/?label=${urlabels}" target="_blank">All ${fn:length(tracks[exp.name])} tracks
</html:link></b></td>
<td>
<html:link href="${baseUrl}fly/?ref=X;label=${urlabels}" target="_blank">X</html:link>
 | 
<html:link href="${baseUrl}fly/?ref=2L;label=${urlabels}" target="_blank">2L</html:link>
 | 
<html:link href="${baseUrl}fly/?ref=2R;label=${urlabels}" target="_blank">2R</html:link>
 | 
<html:link href="${baseUrl}fly/?ref=3L;label=${urlabels}" target="_blank">3L</html:link>
 | 
<html:link href="${baseUrl}fly/?ref=3R;label=${urlabels}" target="_blank">3R</html:link>
 | 
<html:link href="${baseUrl}fly/?ref=4;label=${urlabels}" target="_blank">4</html:link>
 | 
<html:link href="${baseUrl}fly/?ref=U;label=${urlabels}" target="_blank">U</html:link>
</td>
</tr>
</c:if>

<c:if test="${!empty worm}">
<tr>
<td><b><html:link
     href="${baseUrl}worm/?label=${urlabels}" target="_blank">All ${fn:length(tracks[exp.name])} tracks
</html:link></b></td>
<td>
<html:link href="${baseUrl}worm/?ref=I;label=${urlabels}" target="_blank">I</html:link>
 | 
<html:link href="${baseUrl}worm/?ref=II;label=${urlabels}" target="_blank">II</html:link>
 | 
<html:link href="${baseUrl}worm/?ref=III;label=${urlabels}" target="_blank">III</html:link>
 | 
<html:link href="${baseUrl}worm/?ref=IV;label=${urlabels}" target="_blank">IV</html:link>
 | 
<html:link href="${baseUrl}worm/?ref=V;label=${urlabels}" target="_blank">V</html:link>
 | 
<html:link href="${baseUrl}worm/?ref=X;label=${urlabels}" target="_blank">X</html:link>
</td>
</tr>
</c:if>

    </c:if>
    
    
</table>    
     </c:when>
     <c:otherwise>
        NO GBROWSE TRACKS FOR THIS SUBMISSION
     </c:otherwise>
     </c:choose>
   </td>
   </tr>
</table>
    </div>
    </c:if>

<div class="submissions">
  <em>
  <c:choose>
    <c:when test="${exp.submissionCount == 0}">
      There are no submissions for this experiment:
    </c:when>
    <c:when test="${exp.submissionCount == 1}">
      There is <c:out value="${exp.submissionCount}"></c:out> <b><c:out value="${exp.experimentType}"></c:out></b> submission for this experiment:
    </c:when>
    <c:otherwise>
      There are <c:out value="${exp.submissionCount}"></c:out> <b><c:out value="${exp.experimentType}"></c:out></b> submissions for this experiment:   
    </c:otherwise>
    
  </c:choose>
  </em>

<table cellpadding="0" cellspacing="0" border="0" class="sortable-onload-2 rowstyle-alt no-arrow">
<tr>
    <th class="sortable">DCC id</th>
    <th class="sortable">name</th>
    <th>date</th>
      <c:forEach items="${exp.factorTypes}" var="factor">
        <th class="sortable"><c:out value="${factor}"></c:out></th>
      </c:forEach>
    <th>features</th>
    <th>GBrowse and Data Files</th>
<%--    <th>Data Files</th>
--%>
  </tr>

<c:forEach items="${exp.submissionsAndFeatureCounts}" var="subCounts">
	<c:set var="sub" value="${subCounts.key}"></c:set>
    <tr>
      <td class="sorting"><html:link href="/${WEB_PROPERTIES['webapp.path']}/objectDetails.do?id=${subCounts.key.id}"><c:out value="${sub.dCCid}"></c:out></html:link></td>
      <td class="sorting"><html:link href="/${WEB_PROPERTIES['webapp.path']}/objectDetails.do?id=${subCounts.key.id}"><c:out value="${sub.title}"></c:out></html:link></td>
      <td class="sorting"><fmt:formatDate value="${sub.publicReleaseDate}" type="date"/></td>
	
	  <c:forEach items="${exp.factorTypes}" var="factorType">
       <td class="sorting">
      		<c:forEach items="${sub.experimentalFactors}" var="factor" varStatus="status">
        		<c:if test="${factor.type == factorType}">
            		<c:choose>
               		<c:when test="${factor.property != null}">
               			<html:link href="/${WEB_PROPERTIES['webapp.path']}/objectDetails.do?id=${factor.property.id}"><c:out value="${factor.name}"/></html:link>
                        <span class="tinylink">
                        <im:querylink text="ALL" skipBuilder="true">
                         <query name="" model="genomic"
                           view="Submission.DCCid Submission.project.surnamePI Submission.title Submission.experimentType Submission.properties.type Submission.properties.name"
                           sortOrder="Submission.experimentType asc">
                      <node path="Submission.properties.type" type="String">
                        <constraint op="=" value="${factor.type}" description=""
                                    identifier="" code="A">
                        </constraint>
                      </node>  
                      <node path="Submission.properties.name" type="String">
                        <constraint op="=" value="${factor.name}" description=""
                                    identifier="" code="B">
                        </constraint>
                      </node>
                      <node path="Submission.organism.taxonId" type="Integer">
                        <constraint op="=" value="${sub.organism.taxonId}" description=""
                                    identifier="" code="C">
                        </constraint>
                      </node>
                    </query>
                  </im:querylink>
                  </span>
               		</c:when>
               		<c:otherwise>
                 		<c:out value="${factor.name}"/><c:if test="${!status.last}">,</c:if>          
               		</c:otherwise>
           			</c:choose>
           		</c:if>      
       		</c:forEach>
      </td>
	  </c:forEach>
      <td class="sorting">
      	<c:if test="${!empty subCounts.value}">
      		<div class="submissionFeatures">
      		<table cellpadding="0" cellspacing="0" border="0" class="features">
      		<c:forEach items="${subCounts.value}" var="fc" varStatus="rowNumber">
            	<c:set var="class" value=""/>
        		<c:if test="${rowNumber.first}">
          			<c:set var="class" value="firstrow"/>
        		</c:if>
				<tr>                 
          			<td class="firstcolumn ${class}">${fc.key}:<html:link href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=submission&action=results&submission=${sub.dCCid}&feature=${fc.key}">${fc.value}</html:link></td>
			        <td class="${class}" align="right">export: 
               <html:link href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=submission&action=export&submission=${sub.dCCid}&feature=${fc.key}&format=tab">TAB</html:link>
               <html:link href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=submission&action=export&submission=${sub.dCCid}&feature=${fc.key}&format=csv">CSV</html:link>
               <html:link href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=submission&action=export&submission=${sub.dCCid}&feature=${fc.key}&format=gff3">GFF3</html:link>
          			</td>
          			<td class="${class}" align="right">
				<html:link href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=submission&action=list&submission=${sub.dCCid}&feature=${fc.key}"> CREATE LIST</html:link>
          			</td>
      			</tr>
    		</c:forEach>
    		</table>
    		</div>
    	</c:if>
	</td>

        <td class="sorting">
        <c:if test="${!empty fly}">
						<html:link
							href="http://modencode2.oicr.on.ca/gb2/gbrowse/fly/?ds=${sub.dCCid}"
							target="_blank">
							<html:img src="model/images/dgb_vs.png" title="View in GBrowse" />
						</html:link></c:if>
          
        <c:if test="${!empty worm}">
						<html:link
							href="http://modencode2.oicr.on.ca/gb2/gbrowse/worm/?ds=${sub.dCCid}"
							target="_blank">
							<html:img src="model/images/wgb_vs.png" title="View in GBrowse" />
						</html:link>
					</c:if>          

<%--
          </td>         
          <td class="sorting">
--%>
          <c:forEach items="${files}" var="subFiles" varStatus="sub_status">
						<c:if test="${subFiles.key == sub.dCCid}">

							<c:forEach items="${subFiles.value}" var="fileName"
								varStatus="file_status">
								<br>
								<a href="${urlPrefix}${sub.dCCid}/extracted/${fileName}"
									 title="Download ${fileName}" class="value extlink"> <c:out value="${fileName}"/> </a>
							</c:forEach>
						</c:if>
					</c:forEach>          
          </td>

  </tr>
  </c:forEach>
  </table>
</div>
</im:boxarea>
</c:forEach>
</div>
<!-- /experiment.jsp -->
