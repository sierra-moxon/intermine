#!/usr/bin/perl -w

# NB: This isn't the script that is run with the update, that code is in get_uniprot

#splits the uniprot xml files into separate files based on the ncbi taxon id. Reads the taxons of interest from a 
#centrally stored file. Source directory and output directory are supplied as command line args, expexts source
#files to be uniprot_sprot.xml uniprot_trembl.xml 

use strict;
use warnings;

my($start, $end, $buffer, $trigger) = ("<entry", "</entry>", '', 'type="NCBI Taxonomy"');
my ($filename, $fname_end);
my ($keep, $taxon) = ("false", '0000');
my (@new_contents, @old_contents);
my %taxons;

#open file to get taxonIDs currently in flymine
my $config_file="../../flymine/project.xml";
%taxons = &get_taxonIds($config_file,"uniprot.organisms");
my $size = %taxons;
print "size: $size\n";



if ($size eq 0) {
    die "no taxonIds!  please add the taxonIds to the uniprot entry in the project.xml file\n";
}


#the xml declaration and root element for each output file			
my $prolog = '<?xml version="1.0" encoding="UTF-8"?>
<uniprot xmlns="http://uniprot.org/uniprot"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xsi:schemaLocation="http://uniprot.org/uniprot http://www.uniprot.org/support/docs/uniprot.xsd">';
#define the last elements for each output file
my $element_end = '<copyright>
Copyrighted by the UniProt Consortium, see http://www.uniprot.org/terms
Distributed under the Creative Commons Attribution-NoDerivs License
</copyright>
</uniprot>';

#define source and output directories
my $version = $ARGV[0] or die "\nPlease supply the UniProt version you wish to split.
e.g. 9.3\n\n";
my $source_dir = "/shared/data/uniprot/$version/";

my $split_dir = "/shared/data/uniprot/$version/split/";

#create output dir, empty it if already exists?
if(!-e $split_dir ){
    mkdir ("$split_dir", 0755) || die "Cannot mkdir newdir: $!";
}else{
    opendir(DIR,$split_dir) || die("Cannot open directory !\n");
    @old_contents=grep(!/^\.\.?$/,readdir DIR);
    closedir(DIR);
    if(@old_contents){
        print "\n!!$split_dir is not empty!!\nDo you want to delete files and continue running split_uniprot.pl [y]?\n";
        $| = 1;               # force a flush after print
        $_ = <STDIN>;
        chomp;
        if($_ eq'y'){
            foreach my $file (@old_contents){
                print "Deleting $file\n";
                unlink $split_dir.$file;
            }
        }else{
                die "\nsplit_uniprot.pl terminated\n";
        }
    }		
}

#use hash to define output name for each source file
#comment out one of them to process the other file only
my %files = (
             'sprot'  => { 'filename' => $source_dir."uniprot_sprot.xml", 
			 				'fname_end' => '_uniprot_sprot.xml'},
             'trembl'  => { 'filename' => $source_dir."uniprot_trembl.xml",
			 				'fname_end' => '_uniprot_trembl.xml'},
             );		



#open each uniprot file
foreach my $file_type(sort keys %files){
    $filename = $files{$file_type}->{'filename'};
    $fname_end = $files{$file_type}->{'fname_end'};
    
    #read a line at a time and identify the start/stop/taxon id
    open(F,"<$filename") or die "$! was expecting something like uniprot_sprot.xml or uniprot_trembl.xml";
    while(my $newline = <F>){
        if($newline && $newline =~ /$start/gi){
            $keep = "true";
        }elsif($newline && $newline =~ /$trigger/gi){
            if($newline=~/id="(\d+)"/){
                $taxon = $1;
            }
        }elsif($newline && $newline =~ /$end/gi){
            $buffer .= $newline;
            if(exists $taxons{$taxon}){
                &writefile($buffer,$taxon,$fname_end);
            }
            $keep = "false";
            $buffer = "";
            $taxon = 0000;
        }	
        if($keep eq "true"){
            $buffer .= $newline;
        }
    }
    close(F) or die "$!";
}

#identify the output files
opendir(DIR,$split_dir) || die("Cannot open directory !\n");
@new_contents=grep(!/^\.\.?$/,readdir DIR);
closedir(DIR);

#add the copyright and close the root element to the output files
foreach my $file (@new_contents){
    $file = $split_dir.$file;
    open( FILE, ">>$file") or die "cannot open $file: $!";
    print FILE "$element_end";
    close(FILE);
}	

#creates files and adds elements as appropriate 
sub writefile(){
my ($xml,$species,$end) = @_;
my $new_file = $split_dir.$species.$end;

	if(!-e $new_file ){
		open(FH, ">$new_file") || die "$!";
		print FH "$prolog\n";
		print FH "$xml";
		close(FH);
	}else{
	open(FH, ">>$new_file") || die "$!";
		print FH "$xml";
		close(FH);
	}
}
