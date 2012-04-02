<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>

<!-- table.jsp -->

<tiles:importAttribute/>
<html:xhtml/>
<link rel="stylesheet" href="css/resultstables.css" type="text/css" />
<link rel="stylesheet" href="css/table.css" type="text/css" />
<tiles:get name="objectTrail.tile"/> <%--<im:vspacer height="1"/>--%>


<div class="results collection-table nowrap nomargin">
  <tiles:insert name="resultsTable.tile">
     <tiles:put name="pagedResults" beanName="resultsTable" />
     <tiles:put name="currentPage" value="results" />
  </tiles:insert>
</div>

<!-- /table.jsp -->
