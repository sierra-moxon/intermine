<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>

<!-- historyTemplates.jsp -->
<html:xhtml/>

  <im:body id="savedTemplates">
  
    <script LANGUAGE="JavaScript">
      <!--//<![CDATA[
          function confirmAction() {
          return confirm("Do you really want to delete the selected queries?")
          }
          //]]>-->
    </script>

    <p>
      <fmt:message key="history.savedtemplates.help"/>
      <c:set var="helpUrl" value="${WEB_PROPERTIES['project.helpLocation']}/manual/manualQuickStartTemplates.shtml"/>
      [<html:link href="${helpUrl}"><fmt:message key="begin.link.help"/></html:link>]
    </p>
    
    <%-- Choose the queries to display --%>
    <c:choose>
      <c:when test="${empty PROFILE.savedTemplates}">
        <div class="altmessage">
          None
        </div>
      </c:when>
      <c:otherwise>
        
        <html:form action="/modifyTemplate">
        <table class="results history" cellspacing="0">
          <tr>
            <th>
              <input type="checkbox" id="selected_template"
                     onclick="selectColumnCheckbox(this.form, 'template')"/>
            </th>
            <th align="left" nowrap>
              <fmt:message key="history.namecolumnheader"/>
            </th>
            <th align="left" nowrap>
              <fmt:message key="history.titleheader"/>
            </th>
            <th align="left" nowrap>
              <fmt:message key="history.descriptionheader"/>
            </th>
            <th align="left" nowrap>
              <fmt:message key="history.commentheader"/>
            </th>
            <th align="center" nowrap>
              <fmt:message key="history.actionscolumnheader"/>
            </th>
          </tr>    
          <c:forEach items="${PROFILE.savedTemplates}" var="savedTemplate" varStatus="status">
            <tr>
              <td>
                <html:multibox property="selected" styleId="selected_template_${status.index}">
                  <c:out value="${savedTemplate.key}"/>
                </html:multibox>
              </td>
              
              <c:choose>
                <c:when test="${!savedTemplate.value.valid}">
                  <td align="left" nowrap>
                    <html:link action="/templateProblems?name=${savedTemplate.key}&amp;type=user" styleClass="brokenTmplLink">
                    <strike>${savedTemplate.value.name}</strike>
                    </html:link>
                  </td>
                </c:when>
                <c:otherwise>
                  <td>
                    <fmt:message var="linkTitle" key="templateList.run">
					  <fmt:param value="${savedTemplate.value.name}"/>
    			    </fmt:message>
					  ${savedTemplate.value.name}
                    <tiles:insert name="starTemplate.tile">
                      <tiles:put name="templateName" value="${savedTemplate.value.name}"/>
                    </tiles:insert>
                    <c:if test="${IS_SUPERUSER}">
                      <c:set var="taggable" value="${savedTemplate.value}"/>
                      <tiles:insert name="inlineTagEditor.tile">
                        <tiles:put name="taggable" beanName="taggable"/>
                        <tiles:put name="vertical" value="true"/>
                        <tiles:put name="show" value="true"/>
                      </tiles:insert>
                    </c:if>
                  </td>
                </c:otherwise>
              </c:choose>
              
              <td nowrap>
                ${savedTemplate.value.title}
              </td>
              <td nowrap>
                <c:choose>
                  <c:when test="${fn:length(savedTemplate.value.description) > 60}">
                    ${fn:substring(savedTemplate.value.description, 0, 60)}...
                  </c:when>
                  <c:otherwise>
                    ${savedTemplate.value.description}
                  </c:otherwise>
                </c:choose>
              </td>
              <td nowrap>
                <c:choose>
                  <c:when test="${fn:length(savedTemplate.value.comment) > 60}">
                    ${fn:substring(savedTemplate.value.comment, 0, 60)}...
                  </c:when>
                  <c:otherwise>
                    ${savedTemplate.value.comment}
                  </c:otherwise>
                </c:choose>
              </td>
              <td align="center" nowrap>
               <html:link action="/template?name=${savedTemplate.value.name}&amp;type=user" 
               					titleKey="history.action.execute.hover">
                  <fmt:message key="history.action.execute"/>
               </html:link> |
                <html:link action="/editTemplate?name=${savedTemplate.value.name}"
                				titleKey="history.action.edit.hover">
                  <fmt:message key="history.action.edit"/>
                </html:link> |
                <html:link action="/exportTemplates?type=user&amp;name=${savedTemplate.value.name}"
                				titleKey="history.action.execute.hover">
                  <fmt:message key="history.action.export"/>
                </html:link>
                <c:if test="${IS_SUPERUSER && savedTemplate.value.valid}">
	                <tiles:insert name="precomputeTemplate.tile">
	                	<tiles:put name="templateName" value="${savedTemplate.value.name}"/>
	                </tiles:insert>
                </c:if>
              </td>
            </tr>
          </c:forEach>
        </table>
        <br/>
        <html:submit property="delete" onclick="return confirmAction()">
          <fmt:message key="history.delete"/>
        </html:submit>
        <html:submit property="export">
          <fmt:message key="history.exportSelected"/>
        </html:submit>
        </html:form>
        <br/>
      </c:otherwise>
    </c:choose>
  
      <span class="smallnote">
        <c:if test="${!empty PROFILE.savedTemplates}">
          <html:link action="/exportTemplates?type=user" titleKey="begin.exportTemplatesDesc">
            <fmt:message key="begin.exportTemplates"/>
          </html:link><br/>
        </c:if>
        <html:link action="/import" titleKey="begin.importTemplatesDesc">
          <fmt:message key="begin.importTemplates"/>
        </html:link>
      </span>
  </im:body>

<!-- /historyTemplates.jsp -->
