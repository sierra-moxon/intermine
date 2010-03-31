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

<div class="body">

<table cellpadding="0" cellspacing="0" border="0" class="projects" id="projects">
<tr><th>&nbsp;</th><th>Project</th><th>Experiments</th><th>&nbsp;</th></tr>
<c:forEach items="${experiments}" var="proj">
 <c:forEach items="${proj.value}" var="exp"  varStatus="status">
<c:set var="expCount" value="${fn:length(proj.value)}"></c:set>

  <tr>
<c:if test="${status.first}">
  <td rowspan="${expCount}">
    <c:forEach items="${exp.organisms}" var="organism" varStatus="orgStatus">
      <c:if test="${organism eq 'D. melanogaster'}"> 
        <img border="0" class="arrow" src="model/images/f_vvs.png" title="fly"/><br>
      </c:if>
      <c:if test="${organism eq 'C. elegans'}"> 
        <img border="0" class="arrow" src="model/images/w_vvs.png" title="worm"/><br>
      </c:if>
    </c:forEach> 
  </td>
  <td rowspan="${expCount}">
  <b>${proj.key}</b>
  <br></br>PI: <c:out value="${exp.pi}"></c:out>
  </td>
  </c:if>
  
  <td><h4><html:link
        href="/${WEB_PROPERTIES['webapp.path']}/experiment.do?experiment=${exp.name}">${exp.name}</html:link></h4>

<%-- LABS Note: linking with surname only, 2 Green and Kim--%>  

Labs: 
    <c:forEach items="${exp.labs}" var="lab" varStatus="labStatus">
    <c:if test="${!labStatus.first}">, </c:if>
              <b>${lab}</b>
    </c:forEach> 

<%-- with links
    <c:forEach items="${exp.labs}" var="lab" varStatus="labStatus">
    <c:if test="${!labStatus.first}">, </c:if>
              <b><html:link
        href="/${WEB_PROPERTIES['webapp.path']}/portal.do?externalid=*${lab}&class=Lab"
        title="more info on ${lab}'s lab">${lab}</html:link></b>
    </c:forEach> 
    --%>
<br>
<%-- SUBMISSIONS --%>
  <c:choose>
    <c:when test="${exp.submissionCount == 0}">
      This experiment has <b>no data submissions yet</b>.
    </c:when>
    <c:when test="${exp.submissionCount == 1}">
      This experiment has <b><c:out value="${exp.submissionCount}"></c:out> data submission</b>.
    </c:when>
    <c:otherwise>
      This experiment has <b><c:out value="${exp.submissionCount}"></c:out> data submissions</b>. 
    </c:otherwise>
  </c:choose>

<%-- REPOSITORY ENTRIES --%>
    <c:if test="${exp.repositedCount == 1}">
       It has produced <b>${exp.repositedCount} entry in public repositories</b>.
    </c:if>
    <c:if test="${exp.repositedCount > 1}">
       It has produced <b>${exp.repositedCount} entries in public repositories</b>.
    </c:if>

<%-- EXPERIMENTAL FACTORS --%>
     <c:if test="${fn:length(exp.factorTypes) > 0 }"> 
       <c:choose>
         <c:when test="${ fn:length(exp.factorTypes) == 1}">
           <c:out value="The experimental factor is"/>
         </c:when>
         <c:otherwise>
           <c:out value="The experimental factors are"/>
         </c:otherwise>
       </c:choose>  
       <c:forEach items="${exp.factorTypes}" var="ft" varStatus="ft_status"><c:if test="${ft_status.count > 1 && !ft_status.last }">, </c:if><c:if test="${ft_status.count > 1 && ft_status.last }"> and </c:if><b>${ft}</b></c:forEach>.
     </c:if>
</td>

<td>
<%-- FEATURES --%>
      <c:forEach items="${exp.featureCounts}" var="fc" varStatus="fc_status">
     <c:if test="${fc_status.count > 1 }"><br> </c:if> 
      ${fc.key}:&nbsp;${fc.value}
     <c:if test="${fc_status.last }"><br> </c:if> 
      </c:forEach>
<p/>
<%-- TRACKS --%>
     <c:if test="${!empty tracks[exp.name]}">
       <c:choose>
         <c:when test="${fn:length(tracks[exp.name]) == 1}">
           <c:out value="${fn:length(tracks[exp.name])}"/> GBrowse track
         </c:when>
         <c:otherwise>
           <c:out value="${fn:length(tracks[exp.name])}"/> GBrowse tracks
         </c:otherwise>
       </c:choose>
<br></br>
     </c:if>
<%-- REPOSITORY ENTRIES --%>
     <c:if test="${exp.repositedCount > 0}">

      <c:forEach items="${exp.reposited}" var="rep" varStatus="rep_status">
      ${rep.value} 
      <c:choose>
        <c:when test="${rep.value == 1}">
         entry
        </c:when>
        <c:otherwise>
        entries 
        </c:otherwise>
      </c:choose>
      in ${rep.key}
      <br></br>
      </c:forEach>
     </c:if>

<%-- GET DATA --%>
<html:link
        href="/${WEB_PROPERTIES['webapp.path']}/experiment.do?experiment=${exp.name}">
        <img src="model/images/get_data_button.png" alt="Get Data" style="align:middle">
        </html:link>

</td>


  </tr>
  
</c:forEach>
</c:forEach>
  </table>


</div>


<%--
     <c:if test="${fn:length(exp.featureCounts) > 0 }"> 
It generates 
      <c:forEach items="${exp.featureCounts}" var="fc" varStatus="status">
     <c:if test="${status.count > 1 && !status.last }">, </c:if> 
     <c:if test="${status.count > 1 && status.last }"> and </c:if> 
     <html:link
href="/${WEB_PROPERTIES['webapp.path']}/features.do?type=experiment&action=results&experiment=${exp.name}&feature=${fc.key}">${fc.value} ${fc.key}s
</html:link>
      </c:forEach>.
--%>


<!-- links to all subs -->

<table cellspacing="4"><tr>

<td>    
<im:querylink text="Fly" showArrow="true" skipBuilder="true">
  <query name="" model="genomic"
    view="Submission.title Submission.DCCid Submission.design Submission.factorName Submission.publicReleaseDate"
    sortOrder="Submission.title">
  <node path="Submission" type="Submission">
  </node>
  <node path="Submission.organism" type="Organism">
  <constraint op="LOOKUP" value="Drosophila melanogaster" description=""
    identifier="" code="A" extraValue="">
  </constraint>
  </node>
  </query>
</im:querylink>
    </td>

<td>
<im:querylink text="Worm" showArrow="true" skipBuilder="true">
  <query name="" model="genomic"
    view="Submission.title Submission.DCCid Submission.design Submission.factorName Submission.publicReleaseDate"
    sortOrder="Submission.title">
  <node path="Submission" type="Submission">
  </node>
  <node path="Submission.organism" type="Organism">
  <constraint op="LOOKUP" value="Caenorhabditis elegans" description=""
    identifier="" code="A" extraValue="">
  </constraint>
  </node>
  </query>
</im:querylink>
</td>

<td>    
<im:querylink text="All submissions" showArrow="true" skipBuilder="true">
  <query name="" model="genomic"
    view="Submission.title Submission.DCCid Submission.design Submission.factorName Submission.publicReleaseDate"
    sortOrder="Submission.title">
  <node path="Submission" type="Submission">
  </node>
  </query>
</im:querylink>
    </td>


  </tr></table>

