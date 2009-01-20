package InterMine::PathQuery;

=head1 NAME

InterMine::PathQuery::PathQuery - a object representation of a query

=head1 SYNOPSIS

=head1 AUTHOR

FlyMine C<< <support@flymine.org> >>

=head1 BUGS

Please report any bugs or feature requests to C<support@flymine.org>.

=head1 SUPPORT

You can find documentation for this module with the perldoc command.

    perldoc InterMine::PathQuery::PathQuery

You can also look for information at:

=over 4

=item * FlyMine

L<http://www.flymine.org>

=back

=head1 COPYRIGHT & LICENSE

Copyright 2009 FlyMine, all rights reserved.

This program is free software; you can redistribute it and/or modify it
under the same terms as Perl itself.

=head1 FUNCTIONS

=cut

use strict;

use InterMine::Path;
use IO::String;
use XML::Writer;

=head2 new

 Usage   : my $path_query = new InterMine::PathQuery($model);
 Function: create a new, empty path query
 Args    : $model - the InterMine::Model to use for validating paths in the
                    query

=cut
sub new
{
  my $class = shift;

  if (@_ != 1) {
    die "PathQuery::new() needs 1 argument - the model\n";
  }

  my $model = shift;

  my $self = {model => $model, view => []};

  return bless $self, $class;
}

=head2 add_view

 Usage   : $path_query->add_view("Department.name Department.company.name");
             or
           $path_query->add_view(qw(Department.name Department.company.name));
 Function: add paths to the "view" of this PathQuery so that they will appear
           in the output
 Args    : $paths - the paths to add, either a string of space or comma
                    separated paths, or list of paths

=cut
sub add_view
{
  my $self = shift;

  if (@_ == 0) {
    die "no arguments passed to add_view()\n";
  }

  my @paths = map { split /[,\s]+/ } @_;

  for my $path (@paths) {
    InterMine::Path->validate($self->{model}, $path);

    if (!grep {$_ eq $path} @{$self->{view}}) {
      push @{$self->{view}}, $path;
    }
  }
}

=head2 view

 Usage   : my @view_paths = $path_query->view();
 Function: get the current view paths

=cut
sub view
{
  my $self = shift;
  return @{$self->{view}};
}

=head2 sort_order

 Usage   : $path_query->sort_order('Department.name');
 Function: set the sort order
 Args    : $path - a path from the current view that will be the new sort order

=cut
sub sort_order
{
  my $self = shift;

  if (@_ == 0) {
    my $sort_order = $self->{sort_order};
    if (defined $sort_order && $self->_has_view_path($sort_order)) {
      return $sort_order;
    } else {
      # the sort path has gone from the view or was never set, find another
      my @view = $self->view();
      if (@view) {
        $self->{sort_order} = $view[0];
        return $view[0];
      } else {
        die "can't get the sort order because the view is not set\n";
      }
    }
  } else {
    my $sort_order = shift;

    if ($self->_has_view_path($sort_order)) {
      $self->{sort_order} = $sort_order;
    } else {
      die "the new sort order ($sort_order) is not in the view (",
          $self->view(), "\n";
    }
  }
}

=head2 add_constraint

 Usage   : $path_query->add_constraint("Department.name = '$dep_name'");
 Function: add a constraint to this query

=cut
sub add_constraint
{
  my $self = shift;
  my $constraint_string = shift;

  if (!defined $constraint_string) {
    die "no constraint string specified for PathQuery->add_constraint()\n";
  }

  my @bits = split /\s+/, $constraint_string, 3;

  if (@bits < 2) {
    die "can't parse constraint: $constraint_string\n";
  }

  my $path = $bits[0];
  my $op = $bits[1];
  my $value = $bits[2];

  InterMine::Path->validate($self->{model}, $path);

  my %details = (op => $op);

  if (defined $value) {
    $value =~ s/^'(.*)'$/$1/;
    $value =~ s/^"(.*)"$/$1/;

    $details{value} = $value;
  }

  push @{$self->{constraints}->{$path}}, \%details;
}

=head2

 Usage   : $path_query->to_xml_string()
 Function: return an XML representation of this path query

=cut
sub to_xml_string
{
  my $self = shift;

  $self->_is_valid(1);

  my $output = new IO::String();
  my $writer = new XML::Writer(DATA_MODE => 1, DATA_INDENT => 3, OUTPUT => $output);
  $writer->startTag('query', name => '', model => $self->{model}->model_name(),
                    view => (join ' ', $self->view()), 
                    sortOrder => $self->sort_order());

  for my $path_string (sort keys %{$self->{constraints}}) {
    my $details = $self->{constraints}->{$path_string};

    $writer->startTag('node', path => $path_string);

    for my $detail (@$details) {
      my $op = $detail->{op};

      if (defined $detail->{value}) {
        $writer->startTag('constraint', op => $op, value => $detail->{value});
      } else {
        $writer->startTag('constraint', op => $op);
      }
      $writer->endTag();
    }

    $writer->endTag();
  }

  $writer->endTag();

  return ${$output->string_ref};
}

sub _is_valid
{
  my $self = shift;
  my $die_on_error = shift;

  if (scalar($self->view()) > 0) {
    return 1;
  } else {
    if ($die_on_error) {
      die "PathQuery is not valid because there no view set\n";
    } else {
      return 0;
    }
  }
}

sub _has_view_path
{
  my $self = shift;
  my $path = shift;

  return grep {$_ eq $path} @{$self->{view}};
}

1;
