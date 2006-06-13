<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>

<!-- mainBrowser.jsp -->

<html:xhtml/>

    <div class="browserline">
      <c:if test="${node.indentation > 0}">
        <c:forEach begin="1" end="${node.indentation}">
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </c:forEach>
      </c:if>
      <a name="${node.path}"></a>
      <c:set var="isNull" value="${EMPTY_FIELD_MAP[node.parentType][node.fieldName]}"/>
      <c:if test="${isNull}">
        <span class="nullStrike">
      </c:if>
      <%-- construct the real path for this node --%>
      <c:choose>
        <c:when test="${prefix == null}">
          <c:set var="fullpath" value="${node.path}"/>
        </c:when>
        <c:when test="${prefix != null && node.indentation == 0}">
          <c:set var="fullpath" value="${prefix}"/>
        </c:when>
        <c:otherwise>
          <c:set var="fullpath" value="${prefix}.${fn:substringAfter(node.path,'.')}"/>
        </c:otherwise>
      </c:choose>
      <c:choose>
        <c:when test="${isNull}">
          <img border="0" src="images/plus-disabled.gif" width="11" height="11" alt="+"/>
        </c:when>
        <c:when test="${node.button == '+'}">
          <html:link action="/mainChange?method=changePath&amp;path=${node.path}"
            onclick="return toggle('${node.path}', '${node.path}')">
            <img id="img_${node.path}" border="0" src="images/plus.gif" width="11" height="11" alt="+"/>
          </html:link>
        </c:when>
        <c:when test="${node.button == '-'}">
          <html:link action="/mainChange?method=changePath&amp;path=${node.prefix}"
            onclick="return toggle('${node.path}', '${node.path}');">
            <img id="img_${node.path}" border="0" src="images/minus.gif" width="11" height="11" alt="-"/>
          </html:link>
        </c:when>
        <c:otherwise>
          <img src="images/blank.gif" width="11" height="11" alt=" "/>
        </c:otherwise>
      </c:choose>
      <im:viewableSpan path="${fullpath}" viewPaths="${viewPaths}" idPrefix="browser">
        <c:if test="${node.indentation > 0}">
          <c:choose>
            <c:when test="${node.collection}">
              <c:set var="fieldNameClass" value="collectionField"/>
            </c:when>
            <c:when test="${node.reference}">
              <c:set var="fieldNameClass" value="referenceField"/>
            </c:when>
            <c:otherwise>
              <c:set var="fieldNameClass" value="attributeField"/>
            </c:otherwise>
          </c:choose>
          <c:if test="${isNull}">
            <c:set var="fieldNameClass" value="${fieldNameClass} nullReferenceField"/>
          </c:if>
          <span class="${fieldNameClass}">
            <c:out value="${node.fieldName}"/>
          </span>
          <im:typehelp type="${node.parentType}.${node.fieldName}"/>
        </c:if>
        <span class="collectionDescription ${isNull ? 'nullReferenceField' : ''}">
        <c:if test="${node.type != 'String' && node.type != 'Integer'}">
          <span class="type">${node.type}</span><c:if test="${!isNull}"><im:typehelp type="${node.type}"/></c:if>
        </c:if>
        <c:if test="${node.collection}">
          <fmt:message key="query.collection"/>
        </c:if>
        </span>
      </im:viewableSpan>
      <c:choose>
        <c:when test="${node.indentation > 0}">
          <fmt:message key="query.showNodeTitle" var="selectNodeTitle">
            <fmt:param value="${node.fieldName}"/>
          </fmt:message>
          <fmt:message key="query.addConstraintTitle" var="addConstraintToTitle">
            <fmt:param value="${node.fieldName}"/>
          </fmt:message>
        </c:when>
        <c:otherwise>
          <fmt:message key="query.showNodeTitle" var="selectNodeTitle">
            <fmt:param value="${node.type}"/>
          </fmt:message>
          <fmt:message key="query.addConstraintTitle" var="addConstraintToTitle">
            <fmt:param value="${node.type}"/>
          </fmt:message>
        </c:otherwise>
      </c:choose>
      <c:choose>
        <c:when test="${viewPaths[fullpath] == null && !isNull}">
          <html:link action="/mainChange?method=addToView&amp;path=${node.path}" title="${selectNodeTitle}">
            <img class="arrow" src="images/show.gif" width="43" height="13" alt="show" style="margin-right:-0.5ex"/>
          </html:link>
        </c:when>
        <c:otherwise>
          <img class="arrow" src="images/show-disabled.gif" width="43" height="13" alt="show" style="margin-right:-0.5ex"/>
        </c:otherwise>
      </c:choose>
      <c:choose>
        <c:when test="${empty node.parent && prefix == path
                      && empty PROFILE.objectBags && empty loopQueryOps}">
          <%-- don't offer to constrain if this is the top node, there are no
               bags and no potential loop queries --%> 
          <img class="arrow" src="images/constrain-disabled.gif" width="70"
               height="13" alt="constrain"/> 
        </c:when>
        <c:when test="${isNull}">
          <img class="arrow" src="images/constrain-disabled.gif" width="70"
               height="13" alt="constrain"/> 
        </c:when>
        <c:otherwise>
          <html:link action="/mainChange?method=addPath&path=${node.path}" title="${addConstraintToTitle}"
            onclick="return addConstraint('${node.path}');">
            <img class="arrow" src="images/constrain.gif" width="70" height="13" alt="constrain"/>
          </html:link>
        </c:otherwise>
      </c:choose>
      <c:if test="${isNull}">
        </span>
        <c:choose>
          <c:when test="${node.reference}">
            <fmt:message key="query.nullRefHelp" var="strikeThruHelp">
              <fmt:param value="${node.parentType}"/>
              <fmt:param value="${node.fieldName}"/>
            </fmt:message>
          </c:when>
          <c:when test="${node.collection}">
            <fmt:message key="query.emptyCollHelp" var="strikeThruHelp">
              <fmt:param value="${node.parentType}"/>
              <fmt:param value="${node.fieldName}"/>
            </fmt:message>
          </c:when>
          <c:otherwise>
            <%-- null attribute help? --%>
          </c:otherwise>
        </c:choose>
        <im:helplink text="${strikeThruHelp}"/>
      </c:if>
    </div>
    <%-- this if preserves correct interaction with statically rendered tree --%>
    <c:if test="${node.button == '+'}">
      <div id="${node.path}"></div><!-- div+ ${node.path} -->
    </c:if>

<!-- /mainBrowserLine.jsp -->
