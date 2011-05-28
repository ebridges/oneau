#!/usr/bin/perl

# taking an ascp file on stdin, prints out the begin date and end dates 
# covered by the file and the count of observations in the file.

##      1  1018                                                                    
##  0.241499250000000000D+07  0.241502450000000000D+07  0.316038116576787783D+07  
## -0.197805902834290452D+08 -0.114480001530180161D+06  0.132870470153988455D+06  

my @file = <>;

my $coeffs = 1;
my $start = undef;
my $end = undef;

for(my $i=0; $i<$#file; $i++){
    if($file[$i] =~ m/^\s{3,6}+/) {
	$file[$i] =~ s/^\s+//;
	$coeffs = (split /\s+/, $file[$i])[0];
	$i++;
	$file[$i] =~ s/^\s+//;
	my @firstRow = split /\s+/, $file[$i];
	$start = $firstRow[0]
	    unless $start;
	$end = $firstRow[1];
    }
}

$start = conv($start);
$end = conv($end);

print "$coeffs, $start, $end\n";

sub conv() {
    my $v = shift;
    my ($a,$b) = split /D/, $v;
    return $a * 10 ** $b;
}
