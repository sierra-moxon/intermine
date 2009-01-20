package InterMine::Model::Field;

=head1 NAME

InterMine::Model::Field - Representation of a field of a class

=head1 SYNOPSIS

=head1 AUTHOR

FlyMine C<< <support@flymine.org> >>

=head1 BUGS

Please report any bugs or feature requests to C<support@flymine.org>.

=head1 SUPPORT

You can find documentation for this module with the perldoc command.

    perldoc InterMine::Model::Field

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

my %type_map = (
                'java.lang.String' => 'text',
                'java.lang.Boolean' => 'boolean',
                'java.lang.Float' => 'float',
                'java.lang.Double' => 'float',
                'java.lang.Integer' => 'int'
               );

=head2 new

 Usage   : this is an abstract class, construct an Attribute, Collection or
           Reference instead
 Function: create a new Field object
 Args    : name - the field name
           model - the Model

=cut
sub new
{
  my $class = shift;
  my %opts = @_;
  my $self = {%opts};

  if (exists $opts{type}) {
    my $type = $opts{type};
    if (exists $type_map{$type}) {
      $self->{type} = $type_map{$type};
    }
  }

  bless $self, $class;
  return $self;
}

=head2

 Usage   : $name = $field->field_name();
 Function: return the name of this field

=cut
sub field_name
{
  my $self = shift;
  return $self->{name};
}

=head2

 Usage   : $name = $field->field_type();
 Function: return the type of this field, "Attribute", "Reference" or
           "Collection"

=cut
sub field_type
{
  my $self = shift;
  return lc (((ref $self) =~ /.*::(.*)/)[0]);
}

=head2

 Usage   : my $class = $field->field_class();
 Function: returns the ClassDescriptor of the (base) class that defines this
           field

=cut
sub field_class
{
  my $self = shift;
  if (@_ > 0) {
    $self->{field_class} = shift;
  } else {
    return $self->{field_class};
  }
}

1;
