<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>

<style>

pre.stacktrace {
 background: #f7f7f7;
 border: 1px solid #d7d7d7;
 padding: .25em;
 overflow: auto;
}

div.error_body {
  background-color: white;
  border: solid 1px #bbb;
}

</style>

<!-- error.jsp -->
<html:xhtml/>

<tiles:insert page="/errorMessages.jsp"/>

<div class="error_body">
<div class="body"><b><fmt:message key="error.stacktrace"/></b></div>

<div class="body">
<pre class="stacktrace">
  <c:out value="${stacktrace}"/>
 </pre>
</div>

</div>

<!-- /error.jsp =-->