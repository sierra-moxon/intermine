<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="im"%>


<!-- dataCategories -->

<c:set var="note1" value=" Genome annotation for D. melanogaster (R5.5), D. ananassae (R1.0), D. erecta (R1.0), D. grimshawi (R1.0), D. mojavensis (R1.0), D. persimilis (R1.0), D. pseudoobscura pseudoobscura (R2.1), D. sechellia (R1.0), D. simulans (R1.0), D. virilis (R1.0), D. willistoni (R1.0) and D. yakuba (R1.0)."/>
<c:set var="note2" value="Also orthologues from these 5 organisms to <i>C. familiaris</i>, <i>D. discoideum</i>, <i>D. rerio</i>, <i>G. gallus</i>, <i>H. sapiens</i>, <i>M. musculus</i>, <i>P. troglodytes</i>, <i>R. norvegicus</i>, <i>S. cerevisiae</i>, <i>S. pombe</i>." />
<c:set var="note3" value="These data have been re-mapped to genome sequence release 5.0 as of FlyMine release 7.0."/>


<html:xhtml/>

<div class="body">
<im:boxarea title="Data" stylename="plainbox"><p><fmt:message key="dataCategories.intro"/></p></im:boxarea>


<table cellpadding="0" cellpadding="0" border="0" class="dbsources">
  <tr>
    <th>Data Category</th>
    <th>Organism</th>
    <th>Data</th>
    <th>Source</th>
    <th>PubMed</th>
    <th>Note</th>

  </tr>

  <tr><td rowspan="4" class="leftcol">
        <html:link action="/aspect?name=Genomics"> <p><img src="model/images/genomics.gif" /></p>
        <p> Genomics </p></html:link></td>
    <td> <i>Drosophila</i> </td>
    <td> Genome annotation</td>
    <td><a href="http://www.flybase.org" target="_new">Flybase</a> - Version FB2008_02</td>
    <td> Crosby et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099233" target="_new">PubMed: 17099233</a></td>
    <td><html:link action="/dataCategories" anchor="note1" title="${note1}">#1</html:link></td>
  </tr>

  <tr>
    <td> <i>A. gambiae</i></td>
    <td> Genome annotation - Release AgamP3.4</td>
    <td> <a href="http://www.ensembl.org" target="_new">Ensembl</a> - Release 46.3i</td>
    <td> Hubbard et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17148474 " target="_new">PubMed: 17148474</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>A. gambiae</i></td>
    <td> EST data set</td>
    <td> <a href="http://web.bioinformatics.ic.ac.uk/vectorbase/AnoEST.v8/index.php" target="_new">anoEST database</a> - Version 8</td>
    <td> Kriventseva et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=15899967" target="_new">PubMed: 15899967</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>A. mellifera</i></td>
    <td> Genome annotation - Release 2</td>
    <td> <a href="http://www.ensembl.org" target="_new">Ensembl</a> - Release 37.2d</td>
    <td> Hubbard et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17148474 " target="_new">PubMed: 17148474</a></td>
    <td> &nbsp; </td>
  </tr>

  <tr><td rowspan="1"  class="leftcol">
        <html:link action="/aspect?name=Comparative%20Genomics">
          <p>  <img src="model/images/comparativeGenomics.png" /></p>
          <p> Comparative Genomics </p></html:link></td>
    <td>
       <p><i>D. melanogaster</i></p>
       <p><i>D. pseudoobscura</i></p>
       <p><i>A. gambiae</i></p>
       <p><i>A. mellifera</i></p>
       <p><i>C. elegans</i></p>
    </td>
    <td> Orthologue and paralogue relationships between these 5 organisms</td>
    <td> <a href="http://inparanoid.sbc.su.se/" target="_new">InParanoid</a> - Version 6.0</td>
    <td> O'Brien et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=15608241" target="_new">PubMed: 15608241</a></td>
    <td><html:link action="/dataCategories" anchor="note2" title="${note2}">#2</html:link></td>    
  </tr>


  <tr><td rowspan="8"  class="leftcol">
        <html:link action="/aspect?name=Proteins">
        <p> <img src="model/images/proteins.png" /></p>
        <p> Proteins </p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> Protein annotation</td>
    <td> <a href="http://www.ebi.uniprot.org/index.shtml" target="_new">UniProt</a> - Release 13.0</td>
    <td> UniProt Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17142230" target="_new">PubMed: 17142230</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>A. gambiae</i></td>
    <td> Protein annotation</td>
    <td> <a href="http://www.ebi.uniprot.org/index.shtml" target="_new">UniProt</a> - Release 13.0</td>
    <td> UniProt Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17142230" target="_new">PubMed: 17142230</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>C. elegans</i> </td>
    <td> Protein annotation</td>
    <td> <a href="http://www.ebi.uniprot.org/index.shtml" target="_new">UniProt</a> - Release 13.0</td>
    <td> UniProt Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17142230" target="_new">PubMed: 17142230</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>H. sapiens</i></td>
    <td> Protein annotation</td>
    <td> <a href="http://www.ebi.uniprot.org/index.shtml" target="_new">UniProt</a> - Release 13.0</td>
    <td> UniProt Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17142230">PubMed: 17142230</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>M. musculus</i></td>
    <td> Protein annotation</td>
    <td> <a href="http://www.ebi.uniprot.org/index.shtml" target="_new">UniProt</a> - Release 13.0</td>
    <td> UniProt Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17142230" target="_new">PubMed: 17142230</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>D. melanogaster</i></td>
    <td> Protein family and domain assignments to proteins</td>
    <td> <a href="http://www.ebi.ac.uk/interpro" target="_new">InterPro</a> (from Uniprot release 13.0)</td>
    <td> Mulder et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17202162" target="_new">PubMed: 17202162</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>A. gambiae</i></td>
    <td> Protein family and domain assignments to proteins</td>
    <td> <a href="http://www.ebi.ac.uk/interpro" target="_new">InterPro</a> (from Uniprot release 13.0)</td>
    <td> Mulder et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17202162" target="_new">PubMed: 17202162</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>C. elegans</i> </td>
    <td> Protein family and domain assignments to proteins</td>
    <td> <a href="http://www.ebi.ac.uk/interpro" target="_new">InterPro</a> (from Uniprot release 13.0)</td>
    <td> Mulder et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17202162" target="_new">PubMed: 17202162</a></td>
    <td> &nbsp;</td>
  </tr>


  <tr><td rowspan="3"  class="leftcol">
        <html:link action="/aspect?name=Protein%20Structure">
        <p> <img src="model/images/pstructure.gif" /></p>
        <p> Protein Structure</p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> 3-D structure predictions for protein domains</td>
    <td> <a href="http://www-cryst.bioc.cam.ac.uk/~kenji/NEW/index.htm" target="_new">Kenji Mizuguchi</a> - 9th April 2006</td>
    <td> &nbsp;</td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>A. gambiae</i> </td>
    <td> 3-D structure predictions for protein domains</td>
    <td> <a href="http://www-cryst.bioc.cam.ac.uk/~kenji/NEW/index.htm" target="_new">Kenji Mizuguchi</a> - 27th June 2007</td>
    <td> &nbsp;</td>
    <td> &nbsp;</td>
  </tr>


  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Experimentally determined 3-D structures</td>
    <td> <a href="http://www.rcsb.org/pdb/home/home.do" target="_new">PDB [Protein Data Bank]</a> - 9th August 2007</td>
    <td> &nbsp;</td>
    <td> &nbsp;</td>
  </tr>


  <tr><td rowspan="6"  class="leftcol">
        <html:link action="/aspect?name=Interactions">
         <p> <img src="model/images/interaction.gif" /></p>
        <p> Interactions</p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> High-throughput yeast 2-hybrid protein interaction datasets </td>
    <td> <a href="http://www.ebi.ac.uk/intact" target="_new">IntAct</a> - 7th January 2008</td>
    <td> Kerrien et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17145710" target="_new">PubMed:17145710</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>C. elegans</i></td>
    <td> High-throughput yeast 2-hybrid protein interaction datasets </td>
    <td> <a href="http://www.ebi.ac.uk/intact" target="_new">IntAct</a> - 7th January 2008</td>
    <td> Kerrien et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17145710" target="_new">PubMed:17145710</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>S. cerevisiae</i></td>
    <td> High-throughput yeast 2-hybrid protein interaction datasets </td>
    <td> <a href="http://www.ebi.ac.uk/intact" target="_new">IntAct</a> - 7th January 2008</td>
    <td> Kerrien et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17145710" target="_new">PubMed:17145710</a></td>
    <td> &nbsp;</td>
  </tr>
 <tr>
    <td> <i>D. melanogaster</i></td>
    <td> Genetic interactions from the BioGRID</td>
    <td> <a href="http://www.thebiogrid.org/" target="_new">BioGRID</a> - Version 2.0.38 </td>
    <td> Stark et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=16381927" target="_new">PubMed:16381927</a></td>
    <td> &nbsp;</td>
  </tr>

 <tr>
    <td> <i>C. elegans</i></td>
    <td> Genetic interactions from the BioGRID</td>
    <td> <a href="http://www.thebiogrid.org/" target="_new">BioGRID</a> - Version 2.0.38 </td>
    <td> Stark et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=16381927" target="_new">PubMed:16381927</a></td>
    <td> &nbsp;</td>
  </tr>

 <tr>
    <td> <i>S. cerevisiae</i></td>
    <td> Genetic interactions from the BioGRID</td>
    <td> <a href="http://www.thebiogrid.org/" target="_new">BioGRID</a> - Version 2.0.38 </td>
    <td> Stark et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=16381927" target="_new">PubMed:16381927</a></td>
    <td> &nbsp;</td>
  </tr>




  <tr><td rowspan="7"  class="leftcol">

        <html:link action="/aspect?name=Gene%20Ontology">
         <p> <img src="model/images/geneOntology.png" /></p>
        <p> Gene Ontology </p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org" target="_new">Gene Ontology Site</a> - 11th February 2008</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651" target="_new">PubMed:10802651</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>C. elegans</i></td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org" target="_new">Gene Ontology Site</a> - 21st December 2007</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651" target="_new">PubMed:10802651</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>M. musculus</i></td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org" target="_new">Gene Ontology Site</a> - 18th January 2008</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651" target="_new">PubMed:10802651</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>S. cerevisiae</i></td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org" target="_new">Gene Ontology Site</a> - 19th January 2008</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651" target="_new">PubMed:10802651</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>S. pombe</i></td>
    <td> GO annotations </td>
    <td> <a href="http://www.geneontology.org" target="_new">Gene Ontology Site</a> - 17th December 2007</td>
    <td> Gene Ontology Consortium - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 10802651" target="_new">PubMed:10802651</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>A. gambiae</i> </td>
    <td> GO annotations </td>
    <td> <a href="http://www.ebi.ac.uk/GOA/uniprot_release.html" target="_new">Uniprot GOA</a> - 21st January 2008</td>
    <td> Camon et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=14681408" target="_new">PubMed: 14681408</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>A. mellifera</i> </td>
    <td> GO annotations </td>
    <td> <a href="http://www.ebi.ac.uk/GOA/uniprot_release.html" target="_new">Uniprot GOA</a> - 21st January 2008</td>
    <td> Camon et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=14681408" target="_new">PubMed: 14681408</a></td>
    <td> &nbsp;</td>
  </tr>


  <tr><td rowspan="5" class="leftcol">
        <html:link action="/aspect?name=Gene%20Expression">
        <p> <img src="model/images/embryos.jpg" /></p>
        <p> Gene Expression</p>
         </html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> Microarray-based gene expression data for the life cycle of D. melanogaster</td>
    <td> <a href="http://www.ebi.ac.uk/arrayexpress/" target="_new"> ArrayExpress </a> - Experiment E-FLYC-6</td>
    <td> Arbeitman et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 12351791" target="_new">PubMed: 12351791</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Affymetrix microarray-based atlas of gene expression in larval and adult tissues</td>
    <td> <a href="http://www.flyatlas.org" target="_new">FlyAtlas</a> - 16th November 2007 </td>
    <td> Chintapalli et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17534367" target="_new">PubMed: 17534367</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Expression patterns of mRNAs at the subcellular level during early embryogenesis</td>
    <td> <a href="http://fly-fish.ccbr.utoronto.ca/" target="_new">Fly-FISH</a> - 16th October 2007 </td>
    <td> Lecuyer et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17923096" target="_new">PubMed: 17923096</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Expression patterns of mRNAs during embryogenesis</td>
    <td> <a href="http://www.fruitfly.org/cgi-bin/ex/insitu.pl/" target="_new">BDGP</a> - 13th February 2008 </td>
    <td> Tomancak et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17645804" target="_new">PubMed:17645804</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>A. gambiae</i> </td>
    <td> Microarray-based gene expression data for the life cycle of A. gambiae</td>
    <td> <a href="http://www.ebi.ac.uk/arrayexpress/" target="_new"> ArrayExpress </a> - Experiment E-TABM-186</td>
    <td> Koutsos et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids= 17563388" target="_new">PubMed: 17563388</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr><td rowspan="4" class="leftcol">
       <html:link action="/aspect?name=Transcriptional%20Regulation">
        <p> <img src="model/images/bindingSites.png" /></p>
        <p> Transcriptional Regulation</p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> Transcriptional cis-regulatory modules (CRMs)</td>
    <td> <a href="http://redfly.ccr.buffalo.edu/" target="_new">REDfly</a> - Version 2.1</td>
    <td> Gallo et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=16303794" target="_new">PubMed: 16303794</a></td>
    <td><html:link action="/dataCategories" anchor="note3" title="${note3}">#3</html:link></td> 
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Transcription factor binding sites</td>
    <td> <a href="http://www.flyreg.org/" target="_new">REDfly</a> - Version 2.1</td>
    <td> Bergman et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=15572468" target="_new">PubMed: 15572468</a></td>
	<td><html:link action="/dataCategories" anchor="note3" title="${note3}">#3</html:link></td> 
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Enhancers</td>
    <td> <a href="http://www.flybase.org" target="_new">Flybase</a> - Version FB2008_02</td>
    <td> Crosby et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099233" target="_new">PubMed: 17099233</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Binding site predictions</td>
    <td> <a href="http://servlet.sanger.ac.uk/tiffin/" target="_new">Tiffin</a> - 1.2</td>
    <td> Down et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17238282" target="_new">PubMed: 17238282</a></td>
    <td><html:link action="/dataCategories" anchor="note3" title="${note3}">#3</html:link></td> 
  </tr>

  <tr><td rowspan="2" class="leftcol">
       <html:link action="/aspect?name=RNAi">
        <p> <img src="model/images/rnai.png" /></p>
        <p> RNAi</p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> High-throughput cell-based RNAi screens</td>
    <td> <a href="http://flyrnai.org/" target="_new">Drosophila RNAi Screening Center</a> - 3rd January 2008</td>
    <td> Flockhart et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=16381918" target="_new">PubMed: 16381918</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>C. elegans</i> </td>
    <td> RNAi data from in vivo experiments</td>
    <td> <a href="http://www.wormbase.org" target="_new">WormBase</a> - Release 185 </td>
    <td> Bieri et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099234" target="_new">PubMed: 17099234</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr><td rowspan="1" class="leftcol">
       <html:link action="/aspect?name=Pathways">
        <p> <img src="model/images/pathways.png" /></p>
        <p> Pathways</p></html:link></td>
    <td> <i>D. melanogaster</i></td>
    <td> Pathway information and the genes involved in them</td>
    <td> <a href="http://www.genome.jp/kegg/" target="_new">KEGG</a> - Release 45</td>
    <td> Kanehisa et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=16381885" target="_new">PubMed: 16381885</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr><td rowspan="1" class="leftcol">
       <html:link action="/aspect?name=Disease">
        <p> <img src="model/images/disease.png" /></p>
        <p> Diseases</p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> Human disease to Drosophila gene data set</td>
    <td> <a href="http://superfly.ucsd.edu/homophila/" target="_new">Homophila</a> - Version 2.1</td>
    <td> Reiter et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=11381037" target="_new">PubMed: 11381037</a></td>
    <td> &nbsp;</td>
  </tr>

  <tr><td rowspan="7" class="leftcol">
       <html:link action="/aspect?name=Resources">
        <p> <img src="model/images/drosdel.gif" /></p>
        <p> Resources</p></html:link></td>
    <td> <i>D. melanogaster</i> </td>
    <td> Insertions and deletions from <a href="http://www.drosdel.org.uk" target="_new">DrosDel</a></td>
    <td> <a href="http://www.flybase.org" target="_new">FlyBase </a> - Version FB2008_02</td>
    <td> Ryder et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=15238529" target="_new">PubMed: 15238529</a></td>
    <td><html:link action="/dataCategories" anchor="note3" title="${note3}">#3</html:link></td> 
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Insertions from <a href="http://drosophila.med.harvard.edu" target="_new">Exelixis</a></td>
    <td> <a href="http://www.flybase.org" target="_new">FlyBase </a> - Version FB2008_02</td>
    <td> &nbsp;</td>
    <td> &nbsp;</td>
   </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Insertions</td>
    <td> <a href="http://www.flybase.org" target="_new">Flybase</a> - Version FB2008_02</td>
    <td> Crosby et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099233" target="_new">PubMed: 17099233</a></td>
    <td>&nbsp;</td>
  </tr>

  <tr>
    <td> <i>D. melanogaster</i> </td>
    <td> Probe sets from the Affymetrix GeneChip Drosophila Genome 1.0 Array</td>
    <td> <a href="http://www.affymetrix.com/" target="_new">Affymetrix</a></td>
    <td> &nbsp;</td>
    <td> &nbsp;</td>
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

  <tr><td rowspan="4" class="leftcol">
       <html:link action="/aspect?name=Literature">
        <p> <img src="model/images/book.png" /></p>
        <p> Literature</p></html:link></td>
    <td> <i>Drosophila</i> </td>
    <td> Gene versus publications</td>
    <td><a href="http://www.flybase.org" target="_new">Flybase</a> - Version FB2008_02</td>
    <td> Crosby et al - <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=17099233" target="_new">PubMed: 17099233</a></td>
    <td> &nbsp;</td>
  </tr>

    <td> <i>D. melanogaster</i> </td>
    <td> Gene versus publications</td>
    <td> <a href="http://www.ncbi.nlm.nih.gov" target="_new">NCBI</a> - 7th March 2008</td>
    <td> &nbsp;</td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>C. elegans</i></td>
    <td> Gene versus publications</td>
    <td> <a href="http://www.ncbi.nlm.nih.gov" target="_new">NCBI</a> - 7th March 2008</td>
    <td> &nbsp;</td>
    <td> &nbsp;</td>
  </tr>

  <tr>
    <td> <i>S. cerevisiae</i> </td>
    <td> Gene versus publications</td>
    <td> <a href="http://www.ncbi.nlm.nih.gov" target="_new">NCBI</a> - 7th March 2008</td>
    <td> &nbsp;</td>
    <td> &nbsp;</td>
  </tr>

</table>

<div class="body">
<ol>
	<li><a name="note1">${note1}</a></li>
	<li><a name="note2">${note2}</a></li>
	<li><a name="note3">${note3}</a></li>
</ol>
</div>

</div>
<!-- /dataCategories -->
