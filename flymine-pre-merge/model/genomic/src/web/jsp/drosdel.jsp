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
        <p>
          <a href="http://www.drosdel.org.uk">DrosDel</a> is a collection
          of <i>P</i>-element insertions for generating custom chromosomal aberrations
          in <i>D. melanogaster</i>.  The locations of the <i>P</i>-element insertions
          and the deletions that can be constructed from them have been loaded into
          FlyMine.  Deletions that can be made from existing fly stocks are tagged as
          available in FlyMine. Stocks can be ordered from the Szeged stock centre via
          the <a href="http://www.drosdel.org.uk/">DrosDel</a> web site.
        </p>
        <p>
          The DrosDel collection has been reported in Ryder et al (2004)
          Genetics 167 (2): 797-813
          (<a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=15238529">PubMed: 15238529</a>).
        </p>
      </div>
    </td>
    <td width="40%" valign="top">
      <div class="heading2">
        Bulk download
      </div>
      <div class="body">
        <ul>
          <li>
            <im:querylink text="Available deletions from the DrosDel dataset (browse)"
                          skipBuilder="true">
              <query name="" model="genomic" view="ArtificialDeletion">
                <node path="ArtificialDeletion" type="ArtificialDeletion">
                </node>
                <node path="ArtificialDeletion.organism" type="Organism">
                </node>
                <node path="ArtificialDeletion.organism.name" type="String">
                  <constraint op="=" value="Drosophila melanogaster" description="" identifier="" code="A">
                  </constraint>
                  <node path="ArtificialDeletion.available" type="Boolean">
                    <constraint op="=" value="true" description="" identifier="" code="B">
                    </constraint>
                  </node>
                </node>
              </query>
            </im:querylink>
          </li>
          <li>
            <im:querylink text="Available DrosDel deletions and positions (export)"
                          skipBuilder="true">
              <query name="" model="genomic" view="ArtificialDeletion.identifier ArtificialDeletion.chromosome.identifier ArtificialDeletion.chromosomeLocation.start ArtificialDeletion.chromosomeLocation.end">
                <node path="ArtificialDeletion" type="ArtificialDeletion">
                </node>
                <node path="ArtificialDeletion.organism" type="Organism">
                </node>
                <node path="ArtificialDeletion.organism.name" type="String">
                  <constraint op="=" value="Drosophila melanogaster" description="" identifier="" code="A">
                  </constraint>
                  <node path="ArtificialDeletion.available" type="Boolean">
                    <constraint op="=" value="true" description="" identifier="" code="B">
                    </constraint>
                  </node>
               </node>
              </query>
            </im:querylink>
          </li>
          <li>
            <im:querylink text="All deletions from the DrosDel dataset (browse)"
                          skipBuilder="true">
              <query name="" model="genomic" view="ArtificialDeletion">
                <node path="ArtificialDeletion" type="ArtificialDeletion">
                </node>
                <node path="ArtificialDeletion.organism" type="Organism">
                </node>
                <node path="ArtificialDeletion.organism.name" type="String">
                  <constraint op="=" value="Drosophila melanogaster" description="" identifier="" code="A">
                  </constraint>
                </node>
              </query>
            </im:querylink>
          </li>
          <li>
            <im:querylink text="All DrosDel deletions and positions (export)"
                          skipBuilder="true">
              <query name="" model="genomic" view="ArtificialDeletion.identifier ArtificialDeletion.available ArtificialDeletion.chromosome.identifier ArtificialDeletion.chromosomeLocation.start ArtificialDeletion.chromosomeLocation.end">
                <node path="ArtificialDeletion" type="ArtificialDeletion">
                </node>
                <node path="ArtificialDeletion.organism" type="Organism">
                </node>
                <node path="ArtificialDeletion.organism.name" type="String">
                  <constraint op="=" value="Drosophila melanogaster" description="" identifier="" code="A">
                  </constraint>
                </node>
              </query>
            </im:querylink>
          </li>
          <li>
            <im:querylink text="All insertion sites from the DrosDel dataset (browse)"
                          skipBuilder="true">
              <query name="" model="genomic" view="TransposableElementInsertionSite">
                <node path="TransposableElementInsertionSite" type="TransposableElementInsertionSite">
                </node>
                <node path="TransposableElementInsertionSite.organism" type="Organism">
                </node>
                <node path="TransposableElementInsertionSite.organism.name" type="String">
                  <constraint op="=" value="Drosophila melanogaster" description="" identifier="" code="A">
                  </constraint>
                </node>
              </query>
            </im:querylink>
          </li>
          <li>
            <im:querylink text="DrosDel insertion sites and positions (export)"
                          skipBuilder="true">
              <query name="" model="genomic" view="TransposableElementInsertionSite.identifier TransposableElementInsertionSite.type TransposableElementInsertionSite.subType TransposableElementInsertionSite.chromosome.identifier TransposableElementInsertionSite.chromosomeLocation.start TransposableElementInsertionSite.chromosomeLocation.end">
                <node path="TransposableElementInsertionSite" type="TransposableElementInsertionSite">
                </node>
                <node path="TransposableElementInsertionSite.organism" type="Organism">
                </node>
                <node path="TransposableElementInsertionSite.organism.name" type="String">
                  <constraint op="=" value="Drosophila melanogaster" description="" identifier="" code="A">
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
