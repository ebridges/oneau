#!/usr/bin/perl

use strict;
use feature 'switch';

my $jdate_url = 'http://aa.usno.navy.mil/cgi-bin/aa_jdconv.pl';

my $y_begin = 1851;
my $y_end = 2050;
my $m_begin = 1;
my $m_end = 10;
my $d_begin = 1;
my $h_begin = 1;
my $h_end = 18;
my $mm_begin = 1;
my $mm_end = 55;
my $s_begin = 0;
my $s_end = 0;

for(my $Y=$y_begin; $Y<=$y_end; $Y++) {
    for(my $M=$m_begin; $M<=$m_end; $M++) {
	#my $d_end = &days_in_month($Y, $M);
	my $d_end = 26;
	for(my $D=$d_begin; $D<=$d_end; $D++) {
	    for(my $h=$h_begin; $h<=$h_end; $h++) {
		for(my $m=$mm_begin; $m<=$mm_end; $m++) {
		    for(my $s=$s_begin; $s<=$s_end; $s++) {
			my $jdate = lookup_julian_date($Y, $M, $D, $h, $m, $s);
			print sprintf("%04d-%02d-%02dT%02d:%02d:%02d.000Z,%s\n", $Y, $M, $D, $h, $m, $s, $jdate);
		    }
		}
	    }
	}
    }
}

sub days_in_month() {
    my $Y = shift;
    my $M = shift;
    
    given($M) {
	when([1, 3, 5, 7, 8, 10, 12]) {
	    return 31;
	}
	when([4, 6, 9, 11]) {
	    return 30;
	}
	when(is_leap_year($Y)) {
	    return 29;
	}
	default {
	    return 28;
	}
    }
}

sub is_leap_year() {
    my $Y = shift;
    return 1
	if (not $Y % 4 and $Y % 100) or (not $Y % 400);
}


sub lookup_julian_date() {
    my $era = 1;  # CE
#    my $era = -1; # BCE
    my $Y = shift;
    my $M = shift;
    my $D = shift;
    my $h = shift;
    my $m = shift;
    my $s = shift;

    my $request = "curl -s -d era=$era -d xxy=$Y -d xxm=$M -d xxd=$D -d t1=$h -d t2=$m -d t3=$s -d ZZZ=END -d FFX=1 -d ID=A $jdate_url";

    my @response = `$request`;

    for(@response) {
	return $1
	    if(/^JD ([0-9.]+)/);
    }
}
