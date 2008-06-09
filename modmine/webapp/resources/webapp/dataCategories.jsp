<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>


<html:xhtml/>

<div class="body">
<im:boxarea title="Data" stylename="plainbox"><p><fmt:message key="dataCategories.intro"/></p></im:boxarea>


<table cellpadding="0" cellspacing="5" border="0" class="dbsources">
  <tr>
    <th>Data Category</th>
    <th>Organism</th>
    <th>Data</th>
    <th>Source</th>
    <th>PubMed</th>
    <th>Note</th>
  </tr>

  <tr><td rowspan="1" class="leftcol">
        <html:link action="/aspect?name=Genomics"> <p><img src="model/images/genomics.gif" /></p>
        <p> Genomics </p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> Genome annotation - Release 5.1</td>
    <td><a href="http://www.flybase.org" target="_new">Flybase</a></td>
    <td> Crosby et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099233" target="_new">PubMed: 17099233</a></td>
    <td> &nbsp;</td>
  </tr>


  <tr><td rowspan="1"  class="leftcol">
        <html:link action="/aspect?name=Comparative%20Genomics">
          <p>  <img src="model/images/comparativeGenomics.png" /></p>
          <p> Comparative Genomics </p></html:link></td>
    <td>
       <p><i>D. melanogaster</i></p>
<%--
       <p><i>D. pseudoobscura</i></p>
       <p><i>A. gambiae</i></p>
       <p><i>A. mellifera</i></p>
--%>
<p><i>C. elegans</i></p>
    </td>
    <td> Orthologue and paralogue relationships between these 2 organisms</td>
    <td> <a href="http://inparanoid.sbc.su.se/" target="_new">InParanoid</a> - Version 5.1</td>
    <td> O'Brien et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=15608241" target="_new">PubMed: 15608241</a></td>
    <td> &nbsp;</td>
<!--
    <td><html:link action="/dataCategories" anchor="note1" title="${note1}">#1</html:link></td>    
-->
  </tr>


  <tr><td rowspan="4"  class="leftcol">
        <html:link action="/aspect?name=Proteins">
        <p> <img src="model/images/proteins.png" /></p>
        <p> Proteins </p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> Protein annotation</td>
    <td> <a href="http://www.ebi.uniprot.org/index.shtml" target="_new">UniProt</a> - Release 12.2</td>
    <td> UniProt Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17142230" target="_new">PubMed: 17142230</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>C. elegans</i> </td>
    <td> Protein annotation</td>
    <td> <a href="http://www.ebi.uniprot.org/index.shtml" target="_new">UniProt</a> - Release 12.2</td>
    <td> UniProt Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17142230" target="_new">PubMed: 17142230</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>D. melanogaster</i></td>
    <td> Protein family and domain assignments to proteins</td>
    <td> <a href="http://www.ebi.ac.uk/interpro" target="_new">InterPro</a> - Release 15.1</td>
    <td> Mulder et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17202162" target="_new">PubMed: 17202162</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>C. elegans</i> </td>
    <td> Protein family and domain assignments to proteins</td>
    <td> <a href="http://www.ebi.ac.uk/interpro" target="_new">InterPro</a> - Release 15.1</td>
    <td> Mulder et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17202162" target="_new">PubMed: 17202162</a></td>
    <td> &nbsp;</td>
  </tr>


  <tr><td rowspan="2"  class="leftcol">

        <html:link action="/aspect?name=Gene%20Ontology">
         <p> <img src="model/images/geneOntology.png" /></p>
        <p> Gene Ontology </p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org" target="_new">Gene Ontology Site</a> - 21th May 2007</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651" target="_new">PubMed:10802651</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>C. elegans</i></td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org" target="_new">Gene Ontology Site</a> - 13th June 2007</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651" target="_new">PubMed:10802651</a></td>
    <td> &nbsp;</td>
  </tr>


  <tr><td rowspan="2" class="leftcol">
        <html:link action="/aspect?name=Gene%20Expression">
        <p> <img src="model/images/marray.gif" /></p>
        <p> Gene Expression</p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> Microarray-based gene expression data for the life cycle of <i>D. melanogaster</i></td>
    <td> <a href="http://www.ebi.ac.uk/arrayexpress/" target="_new"> ArrayExpress </a> - Experiment E-FLYC-6</td>
    <td> Arbeitman et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 12351791" target="_new">PubMed: 12351791</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Affymetrix microarray-based atlas of gene expression in larval and adult tissues</td>
    <td> <a href="http://www.flyatlas.org" target="_new">FlyAtlas</a> - 3rd April 2007 </td>
    <td> Chintapalli et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17534367" target="_new">PubMed: 17534367</a></td>
    <td> &nbsp;</td>
  </tr>



  <tr><td rowspan="1" class="leftcol">
       <html:link action="/aspect?name=Transcriptional%20Regulation">
        <p> <img src="model/images/bindingSites.png" /></p>
        <p> Transcriptional Regulation</p></html:link></td>
    <td> <i>D. melanogaster</i> </td>

<%--
    <td> Transcriptional cis-regulatory modules (CRMs)</td>
    <td> <a href="http://redfly.ccr.buffalo.edu/" target="_new">REDfly</a> - 25th June 2006</td>
    <td> Gallo et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=16303794" target="_new">PubMed: 16303794</a></td>
    <td><html:link action="/dataCategories" anchor="note2" title="${note2}">#2</html:link></td> 
  </tr>


  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> DNase I footprints</td>
    <td> <a href="http://www.flyreg.org/" target="_new">FlyREG - Drosophila DNase I Footprint Database</a> - Version 2.0</td>
    <td> Bergman et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=15572468" target="_new">PubMed: 15572468</a></td>
	<td><html:link action="/dataCategories" anchor="note2" title="${note2}">#2</html:link></td> 
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>


    <td> Regulatory elements</td>
    <td> <a href="http://www.flybase.org" target="_new">Flybase</a></td>
    <td> Crosby et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099233" target="_new">PubMed: 17099233</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Binding site predictions</td>
    <td> <a href="http://servlet.sanger.ac.uk/tiffin/" target="_new">Tiffin</a> - 1.2</td>
    <td> Down et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17238282" target="_new">PubMed: 17238282</a></td>
    <td><html:link action="/dataCategories" anchor="note2" title="${note2}">#2</html:link></td> 
  </tr>
--%>
<%--
  <tr><td rowspan="2" class="leftcol">
       <html:link action="/aspect?name=RNAi">
        <p> <img src="model/images/rnai.png" /></p>
        <p> RNAi</p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> High-throughput cell-based RNAi screens</td>
    <td> <a href="http://flyrnai.org/" target="_new">Drosophila RNAi Screening Center</a></td>
    <td> Flockhart et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=16381918" target="_new">PubMed: 16381918</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>C. elegans</i> </td>
    <td> RNAi data from in vivo experiments</td>
    <td> <a href="http://www.wormbase.org" target="_new">WormBase</a> - 30th September 2006</td>
    <td> Bieri et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099234" target="_new">PubMed: 17099234</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr><td rowspan="1" class="leftcol">
       <html:link action="/aspect?name=Pathways">
        <p> <img src="model/images/pathways.png" /></p>
        <p> Pathways</p></html:link></td>
    <td> <i>D. melanogaster</i></td>
    <td> Pathway information and the genes involved in them</td>
    <td> <a href="http://www.genome.jp/kegg/" target="_new">KEGG</a> - Release 41</td>
    <td> Kanehisa et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=16381885" target="_new">PubMed: 16381885</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr><td rowspan="1" class="leftcol">
       <html:link action="/aspect?name=Disease">
        <p> <img src="model/images/disease.png" /></p>
        <p> Diseases</p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> Human disease to Drosophila gene data set</td>
    <td> <a href="http://superfly.ucsd.edu/homophila/" target="_new">Homophila</a> - Version 2.0</td>
    <td> Reiter et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=11381037" target="_new">PubMed: 11381037</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr><td rowspan="6" class="leftcol">
       <html:link action="/aspect?name=Resources">
        <p> <img src="model/images/drosdel.gif" /></p>
        <p> Resources</p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> Insertions and deletions</td>
    <td> <a href="http://www.drosdel.org.uk/" target="_new">DrosDel</a></td>
    <td> Ryder et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=15238529" target="_new">PubMed: 15238529</a></td>

	  <td><html:link action="/dataCategories" anchor="note2" title="${note2}">#2</html:link></td> 
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Insertions</td>
    <td> <a href="http://drosophila.med.harvard.edu" target="_new">Exelixis</a></td>
    <td> &nbsp;</td>

      <td><html:link action="/dataCategories" anchor="note3" title="${note3}">#3</html:link></td> 
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Insertions</td>
    <td> <a href="http://www.flybase.org" target="_new">Flybase</a></td>
    <td> Crosby et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099233" target="_new">PubMed: 17099233</a></td>
    <td>&nbsp;</td>
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Probe sets from the Affymetrix GeneChip Drosophila Genome 2.0 Array</td>
    <td> <a href="http://www.affymetrix.com/" target="_new">Affymetrix</a></td>
    <td> &nbsp;</td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> INDAC Microarray oligo set - Version 1.0</td>
    <td> <a href="http://www.indac.net/" target="_new">International Drosophila Array Consortium</a></td>
    <td> &nbsp;</td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Whole genome tiling path - Version 4.0</td>
    <td> &nbsp;</td>
    <td> &nbsp; </td>
    <td> &nbsp;</td>
  </tr>
--%>

  <tr><td rowspan="2" class="leftcol">
       <html:link action="/aspect?name=Literature">
        <p> <img src="model/images/book.png" /></p>
        <p> Literature</p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> Gene versus publications</td>
    <td> <a href="http://www.ncbi.nlm.nih.gov" target="_new">NCBI</a> - 14th December 2006</td>
    <td> &nbsp;</td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>C. elegans</i></td>
    <td> Gene versus publications</td>
    <td> <a href="http://www.ncbi.nlm.nih.gov" target="_new">NCBI</a> - 14th December 2006</td>
    <td> &nbsp;</td>
    <td> &nbsp;</td>
  </tr>

</table>
<!--
<div class="body">
<ol>
	<li><a name="note1">${note1}</a></li>
	<li><a name="note2">${note2}</a></li>
	<li><a name="note3">${note3}</a></li>
</ol>
</div>
-->
</div>
<!-- /dataCategories -->
