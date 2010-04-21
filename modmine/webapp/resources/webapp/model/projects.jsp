<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>
<%@ taglib uri="http://flymine.org/imutil" prefix="imutil"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/string-1.1"
	prefix="str"%>

<tiles:importAttribute />
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
  padding: 5px;
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

.filelink a {
  font-size: 11px;
}

.submission_table {
  background-color: white;
}
</style>



<div class="body">

<table cellpadding="0" cellspacing="0" border="0" class="sortable-onload-2 rowstyle-alt no-arrow submission_table">
	<tr>
		<th class="sortable">Project</th>
		<th class="sortable">PI</th>
    <th class="sortable">Labs</th>
    <th>Experiments</th>
    <th>Submissions</th>
	</tr>
	<c:forEach items="${labs}" var="project">
		<tr>
			<td class="sorting"><html:link
				href="/${WEB_PROPERTIES['webapp.path']}/objectDetails.do?id=${project.key.id}" 
				title="${project.key.title}">
 ${project.key.name}
    </html:link>

			<td class="sorting">${project.key.surnamePI} </td>

<td class="sorting">
			<c:forEach items="${project.value}" var="lab">
				<html:link
					href="/${WEB_PROPERTIES['webapp.path']}/objectDetails.do?id=${lab.id}">
 ${lab.surname}
    </html:link><br></br>
			</c:forEach>

</td>
      <td class="sorting">
<table>      
<c:forEach items="${project.key.experiments }" var="expn">
<c:forEach items="${experiments }" var="exp" varStatus="exp_status">
<c:if test="${expn.name eq exp.name}">
<tr>

  <td>
    <c:forEach items="${exp.organisms}" var="organism" varStatus="orgStatus">
      <c:if test="${organism eq 'D. melanogaster'}"> 
        <img border="0" class="arrow" src="model/images/f_vvs.png" title="fly"/>
            <c:set var="fly" value="1" />
          </c:if>
      <c:if test="${organism eq 'C. elegans'}">  
        <img border="0" class="arrow" src="model/images/w_vvs.png" title="worm"/>
            <c:set var="worm" value="1" />
          </c:if>
    </c:forEach> 
  </td>

<td>
     <html:link
        href="/${WEB_PROPERTIES['webapp.path']}/experiment.do?experiment=${exp.name}"
        title="View ${exp.name}">${exp.name}</html:link>
</td>
<td>

    <c:forEach items="${expCats}" var="ecat" varStatus="ecatStatus">
      <c:if test="${ecat.key eq exp.name}">
       <c:forEach items="${ecat.value}" var="cat">
    ${cat}
       </c:forEach>
      </c:if>


    </c:forEach> 
</td>
<td>

<%-- SUBMISSIONS --%>


  <%--
    <c:when test="${exp.submissionCount == 0}">    </c:when>
    --%>
    <c:if test="${exp.submissionCount == 1}">
      <c:set var="submissions" value="${exp.submissionCount} Data submission."/>
    </c:if>
    <c:if test="${exp.submissionCount > 1}">
      <c:set var="submissions" value="${exp.submissionCount} Data submissions."/>
    </c:if>


<im:querylink text="${submissions}" skipBuilder="true">
<query name="" model="genomic" view="Submission.DCCid Submission.title Submission.experimentType Submission.description Submission.embargoDate" sortOrder="Submission.DCCid asc">
  <node path="Submission" type="Submission">
  </node>
  <node path="Submission.experiment" type="Experiment">
  </node>
  <node path="Submission.experiment.name" type="String">
    <constraint op="=" value="${exp.name}" description="" identifier="" code="A">
    </constraint>
  </node>
</query>
</im:querylink>


<%-- EXPERIMENTAL FACTORS --%>
     <c:if test="${fn:length(exp.factorTypes) > 0 }"><br></br> 
       <c:choose>
         <c:when test="${ fn:length(exp.factorTypes) == 1}">
           <c:out value="Experimental factor: "/>
         </c:when>
         <c:otherwise>
           <c:out value="Experimental factors: "/>
         </c:otherwise>
       </c:choose>  
       <c:forEach items="${exp.factorTypes}" var="ft" varStatus="ft_status">
       <c:if test="${ft_status.count > 1 && !ft_status.last }">, </c:if>
       <c:if test="${ft_status.count > 1 && ft_status.last }"> and </c:if><b>${ft}</b>
       </c:forEach>.
     </c:if>
<%-- FEATURES --%>
      <c:forEach items="${exp.featureCounts}" var="fc" varStatus="fc_status">
     <c:if test="${fc_status.count == 1 }"><br>Features: </c:if> 
     
                   <html:link
        href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=experiment&action=results&experiment=${exp.name}&feature=${fc.key}"
        title="View all ${fc.key}s"><b>${fc.value}&nbsp;${fc.key}</b>
            </html:link>
            
               <html:link
        href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=experiment&action=export&experiment=${exp.name}&feature=${fc.key}&format=tab"
        title="Download in tab separated value format">TAB</html:link>
            
              <html:link
        href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=experiment&action=export&experiment=${exp.name}&feature=${fc.key}&format=csv"
        title="Download in comma separated value format">CSV</html:link>
           
            
       <%--     <c:if test="${!empty exp.unlocated }"> --%>
<c:choose>
<c:when test="${!empty exp.unlocated && fn:contains(exp.unlocated, fc.key)}">
</c:when>
<c:otherwise>
             <html:link
        href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=experiment&action=export&experiment=${exp.name}&feature=${fc.key}&format=gff3"
        title="Download in GFF3 format">GFF3</html:link>
             <html:link
        href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=experiment&action=export&experiment=${exp.name}&feature=${fc.key}&format=sequence"
        title="Download the sequences">SEQ</html:link>
           
</c:otherwise>
</c:choose>
&nbsp;

<c:if test="${fc_status.last }">
<br> 
</c:if>
     

      </c:forEach>
<p/>
<%-- TRACKS --%>

     <c:set var="urlabels" value=""/>
     <c:set var="flylabels" value=""/>
     <c:set var="wormlabels" value=""/>
<c:set var="wormTracksCounter" value="" />
<c:set var="flyTracksCounter" value="" />


     <c:forEach items="${tracks[exp.name]}" var="etrack"  varStatus="status">
     <%-- build the url for getting all the labels in this experiment --%> 
     <c:set var="organism" value="${etrack.organism}"/>

<c:choose>
<c:when test="${fn:startsWith(organism,'worm')}">
<c:set var="wormTracksCounter" value="${wormTracksCounter +1 }" />
  <c:choose>
  <c:when test="${empty wormlabels}">
     <c:set var="wormlabels" value="${etrack.track}" /> 
  </c:when>
  <c:otherwise>
     <c:if test="${!fn:contains(wormlabels, etrack.track)}">
       <c:set var="wormlabels" value="${wormlabels}-${etrack.track}" /> 
     </c:if>
  </c:otherwise>
  </c:choose>
</c:when>
<c:when test="${fn:startsWith(organism,'fly')}">
<c:set var="flyTracksCounter" value="${flyTracksCounter +1}" />
  <c:choose>
  <c:when test="${empty flylabels}">
     <c:set var="flylabels" value="${etrack.track}" /> 
  </c:when>
  <c:otherwise>
     <c:if test="${!fn:contains(flylabels, etrack.track)}">
       <c:set var="flylabels" value="${flylabels}-${etrack.track}" />
     </c:if>
  </c:otherwise>
  </c:choose>
</c:when>
</c:choose>

</c:forEach>


<c:if test="${flyTracksCounter > 1 }">
<html:link 
     href="${WEB_PROPERTIES['gbrowse.prefix']}/fly/?label=${flylabels}" target="_blank" title="View all the tracks for this experiment">
     ${flyTracksCounter} GBrowse tracks
        <img border="0" class="arrow" src="model/images/fly_gb.png" title="fly"/>
</html:link>    
</c:if>

<c:if test="${ flyTracksCounter== 1}">
<html:link 
     href="${WEB_PROPERTIES['gbrowse.prefix']}/fly/?label=${flylabels}" target="_blank" title="View the track generated for this experiment">
     ${flyTracksCounter} GBrowse track
        <img border="0" class="arrow" src="model/images/fly_gb.png" title="fly"/>
</html:link>
</c:if>

<c:if test="${ flyTracksCounter > 0 && wormTracksCounter > 0}">
<br></br>
</c:if>
<c:if test="${wormTracksCounter > 1 }">
<html:link 
     href="${WEB_PROPERTIES['gbrowse.prefix']}/worm/?label=${wormlabels}" target="_blank" title="View all the tracks for this experiment">
     ${wormTracksCounter} GBrowse tracks
</html:link>
        <img border="0" class="arrow" src="model/images/worm_gb.png" title="worm"/>
</c:if>

<c:if test="${ wormTracksCounter== 1}">
<html:link 
     href="${WEB_PROPERTIES['gbrowse.prefix']}/worm/?label=${wormlabels}" target="_blank" title="View the track generated for this experiment">
     ${wormTracksCounter} GBrowse track
</html:link>
        <img border="0" class="arrow" src="model/images/worm_gb.png" title="worm"/>
</c:if>


<%-- REPOSITORY ENTRIES --%>
     <c:if test="${exp.repositedCount > 0}">

      <c:forEach items="${exp.reposited}" var="rep" varStatus="rep_status">
      <c:choose>
        <c:when test="${rep.value == 1}">
<c:set var="repo" value="${rep.value} entry"> </c:set>
        </c:when>
        <c:otherwise>
<c:set var="repo" value="${rep.value} entries"> </c:set>
        </c:otherwise>
      </c:choose>


<b>${rep.key}</b>:
<im:querylink text="${repo}" skipBuilder="true">
<query name="" model="genomic" view="DatabaseRecord.database DatabaseRecord.accession DatabaseRecord.description DatabaseRecord.url DatabaseRecord.submissions.DCCid" sortOrder="DatabaseRecord.database asc" constraintLogic="A and B">
  <node path="DatabaseRecord" type="DatabaseRecord">
  </node>
  <node path="DatabaseRecord.submissions" type="Submission">
  </node>
  <node path="DatabaseRecord.submissions.experiment" type="Experiment">
  </node>
  <node path="DatabaseRecord.submissions.experiment.name" type="String">
    <constraint op="=" value="${exp.name}" description="" identifier="" code="A">
    </constraint>
  </node>
  <node path="DatabaseRecord.database" type="String">
    <constraint op="=" value="${rep.key}" description="" identifier="" code="B">
    </constraint>
  </node>
</query>
</im:querylink>

      <br></br>
      </c:forEach>
     </c:if>


<td>
<%-- GET DATA 
<html:link
        href="/${WEB_PROPERTIES['webapp.path']}/experiment.do?experiment=${exp.name}">
        <img src="model/images/get_data_button.png" alt="Get Data" style="align:middle">
        </html:link>
</td>
--%>
</tr>



</c:if>
</c:forEach>
			</c:forEach>
</table>
			
			</td>
      <td class="sorting">
			
			<c:forEach items="${counts}" var="nr">
				<c:if test="${nr.key.surnamePI eq project.key.surnamePI}">
					<c:set var="nrSubs" value="${nr.value}" />
				</c:if>
			</c:forEach> 

			<c:choose>
				<c:when test="${nrSubs eq 0}">
        -
        </c:when>
				<c:when test="${nrSubs gt 0}">
					<im:querylink text="${nrSubs} submissions in the project" skipBuilder="true">
						<query name="" model="genomic"
							view="Project.labs.submissions.title 
							Project.labs.submissions.design 
							Project.labs.submissions.experimentalFactors.type 
							Project.labs.submissions.experimentalFactors.name"
							sortOrder="Project.labs.submissions.title">
						<node path="Project" type="Project">
						</node>
						<node path="Project.surnamePI" type="String">
						<constraint op="=" value="${project.key.surnamePI}" description=""
							identifier="" code="A">
						</constraint>
						</node>
						</query>
					</im:querylink>
				</c:when>
			</c:choose>
	</c:forEach>
	</tr>

</table>





</div>
