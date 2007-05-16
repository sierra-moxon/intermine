#!/bin/sh
echo "==========================================================="
echo "Getting Fly Anatomy Ontology"
./get_fly_anatomy_ontology
echo "==========================================================="
echo
echo "==========================================================="
echo "Getting FlyAtlas"
./get_flyatlas
echo "==========================================================="
echo
echo "==========================================================="
echo "Getting go annotation"
./get_go-annotation
echo "==========================================================="
echo
echo "==========================================================="
echo "Getting intact data"
./get_intact
echo "==========================================================="
echo
echo "==========================================================="
echo "Getting KEGG data"
./get_kegg
echo "==========================================================="
echo
echo "==========================================================="
echo "Getting wormbase identifiers"
./get_wormbase_identifiers
echo "==========================================================="
echo
echo "==========================================================="
echo "Getting pubmed data"
./get_ncbi_pubmed
echo "==========================================================="
echo 
echo "==========================================================="
echo "Getting homophila"
./get_homophila
echo "==========================================================="
echo
echo "==========================================================="
echo "Getting uniprot"
./get_uniprot
echo "==========================================================="
echo
echo "==========================================================="
echo "Getting Ensembl GeneId to PeptideId data"
./get_ensemblgeneID2peptideID
echo "==========================================================="
echo
echo "==========================================================="
echo "Getting InParanoid"
./get_inparanoid
echo "==========================================================="
echo
echo "==========================================================="
echo "Getting Interpro xml file"
./get_interproXML
echo "==========================================================="
today=$(date +"%F")
tempfile="/shared/data/download_logs/temp_log.txt"
logfile="/shared/data/download_logs/$today.txt"
mv $tempfile $logfile
mail -s "Data download log for $today" philip@flymine.org < $logfile
