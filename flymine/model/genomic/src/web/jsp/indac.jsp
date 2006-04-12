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
          The aim of <A href="http://www.indac.net">INDAC</A> is to produce a widely
          available and uniform set of array reagents so that microarray data collected
          from different studies may be more easily compared. On behalf of INDAC, the
          <A href="http://www.flychip.org.uk">FlyChip group</A> has designed a set
          of 65-69mer long oligonucleotides to release 4.1 of the <I>D. melanogaster</I>
          genome. Oligos were designed using a modified version of 
          <A href="http://berry.engin.umich.edu/oligoarray2">OligoArray2</A> and other
          post-processing steps (David Kreil, Debashis Rana, Gos Micklem unpublished).
        </p>
        <p>
          Synthesis of the set by <A href="http://www.illumina.com">Illumina</A>
          began in April 2005.  FlyMine will incorporate the results of these tests
          when available.
        </p>
        <p>
          Note that currently FlyMine stores the positions of the oligos relative to
          the transcript rather than to the chromosome.
        </p>
      </div>
    </td>
    <td valign="top" width="40%">
      <div class="heading2">
        Explore data sets
      </div>
      <div class="body">
        <ul>
          <li>
            <im:querylink text="All INDAC microarray oligos and their
                                associated transcript (browse)" skipBuilder="true">
              <query name="" model="genomic" view="MicroarrayOligo MicroarrayOligo.transcript"/>
            </im:querylink>
          </li>
          <li>
            <im:querylink text="All INDAC microarray oligo identifiers and the identifier
                                of the associated transcript (for export/download)" skipBuilder="true">
              <query name="" model="genomic" view="MicroarrayOligo.identifier MicroarrayOligo.distance3Prime MicroarrayOligo.tm MicroarrayOligo.transcript.identifier"/>
            </im:querylink>
          </li>
        </ul>
        <ul>
          <li>
            <im:querylink text="All INDAC microarray oligos identifiers and
                                their locations on the associated transcript
                                (for export/download)" skipBuilder="true">
              <query name="" model="genomic" view="MicroarrayOligo.identifier MicroarrayOligo.distance3Prime MicroarrayOligo.tm MicroarrayOligo.objects.start MicroarrayOligo.objects.end MicroarrayOligo.objects.subject.identifier">
                <node path="MicroarrayOligo" type="MicroarrayOligo">
                </node>
                <node path="MicroarrayOligo.objects" type="Location">
                </node>
              </query>
            </im:querylink>
          </li>
        </ul>
      </div>
    </td>
  </tr>
</table>
