package InterMine::DataDownloader;

use strict;
use warnings;
use Net::FTP;
use IO::All;
use IO::All::LWP;
use File::Compare;
require Exporter;

our @ISA = qw(Exporter);
our @EXPORT = qw(ftp_connect make_link ftp_download http_download compare_files
				checkdir_exists date_string_file unzip_dir convert_date
				config_species write_version write_log search_webpage get_taxonIds);

#connect to server
sub ftp_connect(){
my ($server,$user,$password) = @_;

my $ftp = Net::FTP->new($server, Passive => 1)
or die "Cannot connect to $server: $@";

$ftp->login($user,$password)
or die "Cannot login ", $ftp->message;

return $ftp;
}

#download file from ftp server
sub ftp_download(){
	my ($ftp,$dir, $file) = @_;
	print STDERR "getting $file to $dir\n";

  	$ftp->binary();
  	$ftp->get($file, "$dir/$file")or die "get failed ", $ftp->message;
}

#download file from http server
sub http_download(){
	my ($source, $destination) = @_;

	print "getting $source\n";
	io($source) > io($destination);
}

#compare two files, return 1 if it is a new version or if
#the current data link is missing. Otherwise return 0
sub compare_files(){
my ($old,$new)=@_;

	if(compare("$old","$new") == 1){
		print "New version found.\n";
		return 1;
	}
	#delete the downloaded files if there are no differences
	elsif(compare("$old","$new") == 0){
		print "Current version up to date - ";
		return 0;
	#download anyway if no comparison can be made
	}else{
		print "Current data file not found - cannot compare files. ";
		return 1;
	}
}

#check if a directory exists and return 0 if it does,
#create it and return 1 if it doesn't
sub checkdir_exists(){
	my $dir = shift;
	if (!(-d $dir)) {
	    print STDERR "creating directory: $dir\n";
	    mkdir $dir
	        or die "failed to create directory $dir";
		return 1;
	}else{
		print STDERR "$dir exists\n";
		return 0;
	}
}
#get the date stamp from a file to be downloaded
sub date_string_file(){
	my ($ftp,$file) = @_;

	my $gene_date_stamp = $ftp->mdtm($file);
	my $date_string = &convert_date($gene_date_stamp);
	return $date_string;
}

#convert date string into day/month/year, if no string, use current date
sub convert_date(){
	my $string = shift;
	my ($second, $minute, $hour, $day, $month, $year, $weekday, $dayofyear, $isdst);

	if($string){
		($second, $minute, $hour, $day, $month, $year, $weekday, $dayofyear, $isdst) = localtime($string);
	}else{
		($second, $minute, $hour, $day, $month, $year, $weekday, $dayofyear, $isdst) = localtime;
	}
	$month += 1;
	$year -= 100;
	$year += 2000;
	#print "date is $day $month $year\n";
	my $date_string = sprintf "%02s-%02s-%02s", $year, $month, $day;

	return $date_string;
}

#unzip files
sub unzip_dir(){
	my $dir = shift;
	print"gzip -dr $dir\n";
	if ((system "gzip -dr $dir") != 0) {
	  die qq|system "gzip -dr $dir" failed: $?\n|;
	}
}

#create symbolic links to the latest file
sub make_link(){
	my ($dir, $link) = @_;
	unlink $link;
	symlink ($dir, "$link") or die "can't create $link";
}

#get taxon Ids from config file 
sub config_species(){
    my ($file,$trigger) = @_;
    my %data;
    
    open(F,"<$file") or die "$!";
    while(<F>){
        my @f = split/\t/;
        if($f[0] =~ /^$trigger/g) {
            #for 2 value configs i.e. get_go_annoatation
            if($f[2]){
                chomp $f[3];
                $data{$f[1]}{$f[2]}=$f[3];
            }
            #for everything else
            else{
                chomp $f[1];
                $data{$f[1]}=$f[1];
            }
        }
    }
    close(F) or die "$!";
    return %data;
}


sub get_organisms(){
    my ($file,$trigger) = @_;
    my @organisms;

    open(F,"<$file") or die "$!";
    while(<F>){
        my @f = split/\t/;
        if($f[0] =~ /^$trigger/g) {
            chomp $f[1];
            push(@organisms, $f[1]); 
        }
    }
    close(F) or die "$!";
    return @organisms;
}



#get taxon Ids from project.xml. not implemented yet, but will work with uniprot
sub get_taxonIds(){
    my ($file,$trigger) = @_;
    # parse file looking for this line: <property name="uniprot.organisms" value="7955 9606"/>                                                                                                                 
    open(F,"<$file") or die "$! [$file]";
    my @projectxml = <F>;
    my @lines = grep(/$trigger/, @projectxml);
    close(F) or die "$!";

    my $line = $lines[0];
    my $i = index($line, 'value="') + 7;
    my $valueSubstr = substr $line, $i;
    my $locationSecondQuotation = index($valueSubstr, '"');
    my $taxonIds = substr $valueSubstr, 0, $locationSecondQuotation;
    print "processing $taxonIds\n";
    my @orgArray = split(/ /, $taxonIds);
    my %orgHash;
    @orgHash{@orgArray} = (1) x @orgArray;
    return %orgHash;
}



#write the version file
sub write_version(){
	my ($root_dir,$buffer) = @_;

	my $version = "$root_dir/VERSION";
	unlink $version;
	&write_file($version,$buffer);
}

#write the download log file
sub write_log(){
	my ($buffer,$logdir,$logname) = @_;

	my $log = $logdir.$logname;
	&checkdir_exists($logdir);
	&write_file($log,$buffer);
}

#for write_version and write_log
sub write_file(){
	my($path,$buffer)=@_;

	if(-e $path){
		open(FH, ">>$path") || die "$!";
		print FH $buffer;
		close(FH);
	}else{
		open(FH, ">$path") || die "$!";
		print FH $buffer;
		close(FH);
	}
}

#use a reg exp to get a version/release number from a web page
sub search_webpage(){
	my ($server,$reg_exp) = @_;
	my $number;

	my $page = io($server)->slurp();
	if ($page =~ $reg_exp) {
		$number = $1;
	}
	return $number;
}

1;
