package DataDownloader::Role::SystemCommand;

use Moose::Role;

sub execute_system_command {
    my $self = shift;
    my @args = @_;
    no autodie qw(open close); # We will handle the pipe problems ourselves
    my $command = join( ' ', @args );
    $self->debug("Executing: $command");
    open( my $process, '-|', @args );
    while (<$process>) {
        $self->debug( "[output from $args[0]]", $_ );
    }
    close $process;
    if ($?) {
        my $exit_code = $? >> 8;
        my $signal    = $? & 127;
        $self->die("'$command' failed: exit code=$exit_code, signal=$signal");
    }
}

=head2 unzip_dir( [$dir] )

Unzip a directory. If no directory is given, defaults to the destination directory.

The destination directory for a source is source_dir/version

The destination directory for a resource is source_dir/version/sub/dirs

=cut

sub unzip_dir {
    my $self = shift;
    my $dir = shift || $self->get_destination_dir;
    $self->execute_system_command("gzip -dr $dir");
}

sub unzip_zip {
    my $self = shift;
    my $file = shift || $self->get_destination;
    my @args = ('unzip', "$file", '-d', $self->get_destination_dir);
    $self->execute_system_command(@args);
}

1;
