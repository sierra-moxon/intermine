<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>


<table width="100%">
  <tr>
    <td valign="top">
      <div class="heading2">
        Current data
      </div>
      <div class="body">


          <p>A mapping between genes and publications for <i>D. melanogaster</i>, 
            <i>C. elegans</i> and <i>S. cerevisiae</i> from NCBI and
for <i>D. melanogaster</i> and <i>D. pseudoobscura</i> from FlyBase.
</p>

      </div>
    </dl>


    </td>

    <td width="40%" valign="top">
      <div class="heading2">
        Bulk download
      </div>
      <div class="body">
        <ul>

          <li>
           <im:querylink text="All <i> D. melanogaster</i> publications"  skipBuilder="true">
<query name="" model="genomic" view="Gene.publications.year Gene.publications.firstAuthor Gene.publications.journal Gene.publications.title Gene.publications.pubMedId" sortOrder="Gene.publications.year asc">
  <node path="Gene" type="Gene">
  </node>
  <node path="Gene.organism" type="Organism">
    <constraint op="LOOKUP" value="Drosophila melanogaster" description="" identifier="" code="A" extraValue="">
    </constraint>
  </node>
</query>
       </im:querylink>
          </li>

          <li>
           <im:querylink text="All <i> D. pseudoobscura</i> publications"  skipBuilder="true">
<query name="" model="genomic" view="Gene.publications.year Gene.publications.firstAuthor Gene.publications.journal Gene.publications.title Gene.publications.pubMedId" sortOrder="Gene.publications.year asc">
  <node path="Gene" type="Gene">
  </node>
  <node path="Gene.organism" type="Organism">
    <constraint op="LOOKUP" value="Drosophila pseudoobscura" description="" identifier="" code="A" extraValue="">
    </constraint>
  </node>
</query>
       </im:querylink>
          </li>

          <li>
           <im:querylink text="All <i> C. elegans</i> publications"  skipBuilder="true">
<query name="" model="genomic" view="Gene.publications.year Gene.publications.firstAuthor Gene.publications.journal Gene.publications.title Gene.publications.pubMedId" sortOrder="Gene.publications.year asc">
  <node path="Gene" type="Gene">
  </node>
  <node path="Gene.organism" type="Organism">
    <constraint op="LOOKUP" value="Caenorhabditis elegans" description="" identifier="" code="A" extraValue="">
    </constraint>
  </node>
</query>
           </im:querylink>
          </li>

          <li>
          <im:querylink text="All <i> S. cerevisiae</i> publications"  skipBuilder="true">
<query name="" model="genomic" view="Gene.publications.year Gene.publications.firstAuthor Gene.publications.journal Gene.publications.title Gene.publications.pubMedId" sortOrder="Gene.publications.year asc">
  <node path="Gene" type="Gene">
  </node>
  <node path="Gene.organism" type="Organism">
    <constraint op="LOOKUP" value="Saccharomyces cerevisiae" description="" identifier="" code="A" extraValue="">
    </constraint>
  </node>
</query>
           </im:querylink>
          </li>

        </ul>
      </div>
    </td>

  </tr>
</table>
