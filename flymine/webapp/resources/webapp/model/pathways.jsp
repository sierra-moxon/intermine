<!-- pathways.jsp -->
<%@ taglib tagdir="/WEB-INF/tags" prefix="im" %>

<table width="100%">
  <tr>
    <td valign="top">
      <div class="heading2">
        Current data
      </div>
      <div class="body">

        <dt>The pathway data in the KEGG database have been manually
        entered from published materials. Current KEGG data in FlyMine only
        include Drosophila KEGG pathway names with their IDs and
        the genes involved.</dt>

       </div>
    </td>

    <td width="40%" valign="top">
      <div class="heading2">
        Bulk download
      </div>
      <div class="body">
         <ul>

          <li>
            <im:querylink text="All KEGG pathways with associated genes " skipBuilder="true">

            <query name="" model="genomic" view="Pathway.identifier Pathway.name Pathway.genes.primaryIdentifier Pathway.genes.identifier"></query>
            </im:querylink>

          </li>
         </ul>
      </div>
    </td>
  </tr>
</table>
<!-- /pathways.jsp -->