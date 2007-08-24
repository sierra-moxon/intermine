<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>

<!-- dataCategories -->

<table padding="0px" margin="0px" width="100%">
  <tr>
    <td valign="top" width="30%">
      <div id="pageDesc" class="pageDesc"><p><fmt:message key="dataCategories.intro"/></p></div></td>

    <td valign="top" width="30%">
        <im:roundbox title="Actions" stylename="welcome">
           <a href="/sources.html"><fmt:message key="dataCategories.action1"/></a>
         <BR/>
           <html:link action="/templates">
             <fmt:message key="dataCategories.action2"/>
           </html:link>
  </im:roundbox> </td>
 </tr>



<table padding="0px" margin="0px" width="100%" table border="1" class='dbsources'>
  <thead>
  <tr>

    <th>Data Category</th>
    <th>Organism</th>
    <th>Data</th>
    <th>Source</th>
    <th>PubMed</th>  
    <th>Note</th>

  </tr>
  </thead>

 <tbody>

  <tr><th rowspan="5">  
        <html:link action="/aspect?name=Genomics"> <p><img src="model/genomics.gif" /></p>
        <p> Genomics </p></html:link></th>
    <td> D. melanogaster </td>
    <td> Genome annotation - Release 5.1</td>
    <td><a href="http://www.flybase.org">Flybase</a></td>
    <td> Crosby et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099233">PubMed: 17099233</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> D. pseudoobscura</td>
    <td> Genome annotation - Release 2.0</td>
    <td><a href="http://www.flybase.org">Flybase</a></td>
    <td> Crosby et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099233">PubMed: 17099233</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> A. gambiae</td>
    <td> Genome annotation - Release AgamP3</td>
    <td> <a href="http://www.ensembl.org">Ensembl</a> - Release 37.3</td>
    <td> Hubbard et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17148474 ">PubMed: 17148474</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> A. gambiae</td>
    <td> EST data set</td>
    <td> <a href="http://web.bioinformatics.ic.ac.uk/vectorbase/AnoEST.v8/index.php">anoEST database version 8</a></td>
    <td> Kriventseva et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=15899967">PubMed: 15899967</a></td> 
    <td> &nbsp;</td>
  </tr>



  <tr>
    <td> A. mellifera</td>
    <td> Genome annotation - Release 2</td>
    <td> <a href="http://www.ensembl.org">Ensembl</a> - Release 37.2d</td>
    <td> Hubbard et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17148474 ">PubMed: 17148474</a></td> 
    <td> &nbsp; </td>
  </tr>

  <tr><th rowspan="1">
        <html:link action="/aspect?name=Comparative%20Genomics">
          <p>  <img src="model/comparativeGenomics.png" /></p>
          <p> Comparative Genomics </p></html:link></th>
    <td>
       <p>D. melanogaster</p>
       <p>D. pseudoobscura</p>
       <p>A. gambiae</p>
       <p>A. mellifera</p>
       <p>C. elegans</p>
    </td>
    <td> Orthologue and paralogue relationships between these 5 organism</td>
    <td> <a href="http://inparanoid.sbc.su.se/">InParanoid</a> - Version 5.1</td>
    <td> O'Brien et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=15608241">PubMed: 15608241</a></td> 
    <td>(1)</td>
  </tr>


  <tr><th rowspan="8">
        <html:link action="/aspect?name=Proteins">
        <p> <img src="model/proteins.png" /></p>
        <p> Proteins </p></html:link></th>
    <td> D. melanogaster </td>
    <td> Protein annotation</td>
    <td> <a href="http://www.ebi.uniprot.org/index.shtml">UniProt</a> - Release 12.2</td>
    <td> UniProt Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17142230 ">PubMed: 17142230</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> A. gambiae</td>
    <td> Protein annotation</td>
    <td> <a href="http://www.ebi.uniprot.org/index.shtml">UniProt</a> - Release 12.2</td>
    <td> UniProt Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17142230 ">PubMed: 17142230</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> C. elegans </td>
    <td> Protein annotation</td>
    <td> <a href="http://www.ebi.uniprot.org/index.shtml">UniProt</a> - Release 12.2</td>
    <td> UniProt Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17142230 ">PubMed: 17142230</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> H. sapiens</td>
    <td> Protein annotation</td>
    <td> <a href="http://www.ebi.uniprot.org/index.shtml">UniProt</a> - Release 12.2</td>
    <td> UniProt Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17142230 ">PubMed: 17142230</a></td>  
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> M. musculus</td>
    <td> Protein annotation</td>
    <td> <a href="http://www.ebi.uniprot.org/index.shtml">UniProt</a> - Release 12.2</td>
    <td> UniProt Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17142230 ">PubMed: 17142230</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> D. melanogaster</td>
    <td> Protein family and domain assignments to proteins</td>
    <td> <a href="http://www.ebi.ac.uk/interpro">InterPro</a> - Release 15.1</td>
    <td> Mulder et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17202162 ">PubMed: 17202162</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> A. gambiae</td>
    <td> Protein family and domain assignments to proteins</td>
    <td> <a href="http://www.ebi.ac.uk/interpro">InterPro</a> - Release 15.1</td>
    <td> Mulder et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17202162 ">PubMed: 17202162</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> C. elegans </td>
    <td> Protein family and domain assignments to proteins</td>
    <td> <a href="http://www.ebi.ac.uk/interpro">InterPro</a> - Release 15.1</td>
    <td> Mulder et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17202162 ">PubMed: 17202162</a></td> 
    <td> &nbsp;</td>
  </tr>


  <tr><th rowspan="2">
        <html:link action="/aspect?name=Protein%20Structure">
        <p> <img src="model/pstructure.gif" /></p>
        <p> Protein Structure</p></html:link></th>
    <td> D. melanogaster </td>
    <td> 3-D structure predictions for protein domains</td>
    <td> <a href="http://www-cryst.bioc.cam.ac.uk/~kenji/NEW/index.htm">Kenji Mizuguchi</a> - 9th April 2006</td>
    <td> &nbsp;</td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> A. gambiae </td>
    <td> 3-D structure predictions for protein domains</td>
    <td> <a href="http://www-cryst.bioc.cam.ac.uk/~kenji/NEW/index.htm">Kenji Mizuguchi</a> - 27th June 2007</td>
    <td> &nbsp;</td> 
    <td> &nbsp;</td>
  </tr>


  <tr><th rowspan="3">
        <html:link action="/aspect?name=Protein%20Interactions">
         <p> <img src="model/proteinInteraction.gif" /></p>
        <p> Protein Interactions</p></html:link></th>
    <td> D. melanogaster </td>
    <td> High-throughput yeast 2-hybrid protein interaction datasets </td>
    <td> <a href="http://www.ebi.ac.uk/intact">IntAct</a> - 25th May 2007</td>
    <td> Kerrien et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17145710 ">PubMed:17145710</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> C. elegans</td>
    <td> High-throughput yeast 2-hybrid protein interaction datasets </td>
    <td> <a href="http://www.ebi.ac.uk/intact">IntAct</a> - 25th May 2007</td>
    <td> Kerrien et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17145710 ">PubMed:17145710</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> S. cerevisiae</td>
    <td> High-throughput yeast 2-hybrid protein interaction datasets </td>
    <td> <a href="http://www.ebi.ac.uk/intact">IntAct</a> - 25th May 2007</td>
    <td> Kerrien et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17145710 ">PubMed:17145710</a></td> 
    <td> &nbsp;</td>
  </tr>


  <tr><th rowspan="8">

        <html:link action="/aspect?name=Gene%20Ontology">
         <p> <img src="model/geneOntology.png" /></p>
        <p> Gene Ontology </p></html:link></th>
    <td> D. melanogaster </td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org">Gene Ontology Site</a> - 21th May 2007</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651">PubMed:10802651</a></td> 
    <td> &nbsp;</td>
  </tr> 

  <tr>
    <td> C. elegans</td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org">Gene Ontology Site</a> - 13th June 2007</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651">PubMed:10802651</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> M. musculus</td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org">Gene Ontology Site</a> - 13th June 2007</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651">PubMed:10802651</a></td> 
    <td> &nbsp;</td>
  </tr> 

  <tr>
    <td> P. falciparum</td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org">Gene Ontology Site</a> - 9th May 2007</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651">PubMed:10802651</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> S. cerevisiae</td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org">Gene Ontology Site</a> - 13th June 2007</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651">PubMed:10802651</a></td>
    <td> &nbsp;</td>
  </tr> 

  <tr>
    <td> S. pombe</td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org">Gene Ontology Site</a> - 19th May 2007</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651">PubMed:10802651</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> A. gambiae </td>
    <td> GO annotations </td>
    <td> <a href="http://www.ebi.ac.uk/GOA/uniprot_release.html">Uniprot GOA</a> - 13th June 2007</td>
    <td> Camon et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=14681408">PubMed: 14681408</a></td> 
    <td> &nbsp;</td>
  </tr> 

  <tr>
    <td> A. mellifera </td>
    <td> GO annotations </td>
    <td> <a href="http://www.ebi.ac.uk/GOA/uniprot_release.html">Uniprot GOA</a> - 13th June 2007</td>
    <td> Camon et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=14681408">PubMed: 14681408</a></td> 
    <td> &nbsp;</td>
  </tr> 


  <tr><th rowspan="2">
        <html:link action="/aspect?name=Gene%20Expression">
        <p> <img src="model/marray.gif" /></p>
        <p> Gene Expression</p></html:link></th>
    <td> D. melanogaster </td>
    <td> Microarray-based gene expression data for the life cycle of D. melanogaster</a></td>
    <td> <a href="http://www.ebi.ac.uk/arrayexpress/"> ArrayExpress </a> - Experiment E-FLYC-6</td>
    <td> Arbeitman et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 12351791">PubMed: 12351791</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> D. melanogaster </td>
    <td> Affymetrix microarray-based atlas of gene expression in larval and adult tissues</td>
    <td> <a href="http://www.flyatlas.org">FlyAtlas</a> - 3rd April 2007 </td>
    <td> Chintapalli et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17534367">PubMed: 17534367</a></td> 
    <td> &nbsp;</td>
  </tr>



  <tr><th rowspan="4">
       <html:link action="/aspect?name=Transcriptional%20Regulation">
        <p> <img src="model/bindingSites.png" /></p>
        <p> Transcriptional Regulation</p></html:link></th>
    <td> D. melanogaster </td>
    <td> Transcriptional cis-regulatory modules (CRMs)</td>
    <td> <a href="http://redfly.ccr.buffalo.edu/">REDfly</a> - 25th June 2006</td>
    <td> Gallo et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=16303794">PubMed: 16303794</a></td> 
    <td> (2)</td>
  </tr>

  <tr>
    <td> D. melanogaster </td>
    <td> DNase I footprints</td>
    <td> <a href="http://www.flyreg.org/">FlyREG - Drosophila DNase I Footprint Database</a> - Version 2.0</td>
    <td> Bergman et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=15572468">PubMed: 15572468</a></td> 
    <td> (2)</td>
  </tr>

  <tr>
    <td> D. melanogaster </td>
    <td> Regulatory elements</td>
    <td> <a href="http://www.flybase.org">Flybase</a></td>
    <td> Crosby et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099233 ">PubMed: 17099233</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> D. melanogaster </td>
    <td> Binding site predictions</td>
    <td> <a href="http://servlet.sanger.ac.uk/tiffin/">Tiffin</a> - 1.2</td>
    <td> Down et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17238282">PubMed: 17238282</a></td> 
    <td> (2)</td>
  </tr>

  <tr><th rowspan="2">
       <html:link action="/aspect?name=RNAi">
        <p> <img src="model/rnai.png" /></p>
        <p> RNAi</p></html:link></th>
    <td> D. melanogaster </td>
    <td> High-throughput cell-based RNAi screens</td>
    <td> <a href="http://flyrnai.org/">Drosophila RNAi Screening Center</a></td>
    <td> Flockhart et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=16381918">PubMed: 16381918</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> C. elegans </td>
    <td> RNAi data from in vivo experiments</td>
    <td> <a href="http://www.wormbase.org">WormBase</a> - 30th September 2006</td>
    <td> Bieri et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099234">PubMed: 17099234</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr><th rowspan="1">
       <html:link action="/aspect?name=Pathways">
        <p> <img src="model/pathways.png" /></p>
        <p> Pathways</p></html:link></th>
    <td> D. melanogaster</td>
    <td> Pathway information and the genes involved in them</td>
    <td> <a href="http://www.genome.jp/kegg/">KEGG</a> - Release 41</td>
    <td> Kanehisa et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=16381885">PubMed: 16381885</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr><th rowspan="1">
       <html:link action="/aspect?name=Disease">
        <p> <img src="model/disease.png" /></p>
        <p> Diseases</p></html:link></th>
    <td> D. melanogaster </td>
    <td> Human disease to Drosophila gene data set</td>
    <td> <a href="http://superfly.ucsd.edu/homophila/">Homophila</a> - Version 2.0</td>
    <td> Reiter et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=11381037 ">PubMed: 11381037</a></td> 
    <td> &nbsp;</td>
  </tr>

  <tr><th rowspan="6">
       <html:link action="/aspect?name=Resources">
        <p> <img src="model/drosdel.gif" /></p>
        <p> Resources</p></html:link></th>
    <td> D. melanogaster </td>
    <td> Insertions and deletions</td>
    <td> <a href="http://www.drosdel.org.uk/">DrosDel</a></td>
    <td> Ryder et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=15238529">PubMed: 15238529</a></td> 
    <td> (2)</td>
  </tr>

  <tr>
    <td> D. melanogaster </td>
    <td> Insertions</td>
    <td> <a href="http://drosophila.med.harvard.edu">Exelixis</a></td>
    <td> &nbsp;</td> 
    <td> (3)</td>
  </tr>

  <tr>
    <td> D. melanogaster </td>
    <td> Insertions</td>
    <td> <a href="http://www.flybase.org">Flybase</a></td>
    <td> Crosby et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099233 ">PubMed: 17099233</a></td> 
    <td>&nbsp;</td>
  </tr>

  <tr>
    <td> D. melanogaster </td>
    <td> Probe sets from the Affymetrix GeneChip Drosophila Genome 2.0 Array</td>
    <td> <a href="http://www.affymetrix.com/">Affymetrix</a></td>
    <td> &nbsp;</td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> D. melanogaster </td>
    <td> INDAC Microarray oligo set - Version 1.0</td>
    <td> <a href="http://www.indac.net/">International Drosophila Array Consortium</a></td>
    <td> &nbsp;</td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> D. melanogaster </td>
    <td> Whole genome tiling path - Version 4.0</td>
    <td> &nbsp;</td>
    <td> &nbsp; </td> 
    <td> &nbsp;</td>
  </tr>

  <tr><th rowspan="3">
       <html:link action="/aspect?name=Literature">
        <p> <img src="model/book.png" /></p>
        <p> Literature</p></html:link></th>
    <td> D. melanogaster </td>
    <td> Gene versus publications</td>
    <td> <a href="http://www.ncbi.nlm.nih.gov">NCBI</a> - 14th December 2006</td>
    <td> &nbsp;</td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> C. elegans</td>
    <td> Gene versus publications</td>
    <td> <a href="http://www.ncbi.nlm.nih.gov">NCBI</a> - 14th December 2006</td>
    <td> &nbsp;</td> 
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> S. cerevisiae </td>
    <td> Gene versus publications</td>
    <td> <a href="http://www.ncbi.nlm.nih.gov">NCBI</a> - 14th December 2006</td>
    <td> &nbsp;</td> 
    <td> &nbsp;</td>
  </tr>

 </tbody>
</table>

<p>
(1) Also orthologues from these 5 organisms to C. familiaris, D. discoideum, D. rerio, G. gallus, H. sapiens, M. musculus, P. troglodytes, R. norvegicus, S. cerevisiae, S. pombe
</p>

<p>
(2) These data have been re-mapped to genome sequence release 5.0 as of FlyMine release 7.0
</p>

<p>
(3) Coordinates for the Exelixis set are still to genomce sequence release 4.0. These will be updated to release 5.0 in the next release of FlyMine
</p>






      <script type="text/javascript">
      	Nifty("div#pageDesc","big");
      	Nifty("div#dataCategories","big");
      </script>
  

<!-- /dataCategories -->
