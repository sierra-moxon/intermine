<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>

<!-- currentTags.jsp -->

<html:xhtml/>

<%-- onChangeCode parameter is additional javascript, that should be called when tags are changed - user clicked at delete tag link  --%>
<tiles:importAttribute  name="onChangeCode" ignore="true"/>
<c:forEach items="${currentTags}" var="item" varStatus="status">
  <span class="tag">${item.tagName} <a href="#" class="deleteTagLink" onclick="new Ajax.Request('<html:rewrite action="/inlineTagEditorChange"/>', {parameters:'method=delete&tagid='+'${item.id}', asynchronous:false});refreshTags('${uid}', '${type}');return false;">[x]</a></span>
</c:forEach>

<!-- /currentTags.jsp -->

