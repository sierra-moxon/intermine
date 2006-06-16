<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>


<div class="heading">
  Custom tile heading
</div>

<div class="body">
  <p>Custom tile body.</p>
  <ol>
    <li>
      <im:querylink text="Execute employeesWithOldManagers, skipping query builder" skipBuilder="true">
        <query name="employeesWithOldManagers" model="testmodel" view="Employee.name Employee.age Employee.department.name Employee.department.manager.age">
          <node path="Employee.department.manager.age">
            <constraint op=">" value="10"/>
          </node>
        </query>
      </im:querylink>
    </li>
    <li>
      <im:querylink text="Execute employeesWithOldManagers, go to builder" skipBuilder="false">
        <query name="employeesWithOldManagers" model="testmodel" view="Employee.name Employee.age Employee.department.name Employee.department.manager.age">
          <node path="Employee.department.manager.age">
            <constraint op=">" value="10"/>
          </node>
        </query>
      </im:querylink>
    </li>
    
  </ol>
</div>
