## Overview
The OneAU application relies on the JPL ephemeris data from NASA to interpolate the positions of the planets, the Sun, and the Moon at any moment in time over many hundreds of years.

This light-weight, Java application is highly optimized to effectively operate within 512MB of RAM, and still rely on the full range of data in the JPL Ephmemeris (exceeding 1GB of textual data).  It is service oriented and will return results in JSON, thus enabling easy integration with other web applications as well as mobile and other apps operating in restricted contexts.

## Calculations Supported
* Determine the location in the sky of the most common heavenly bodies at any point in time from the year 1600 through the year 2400 AD, using the JPL Ephemeris files.
* Calculate the magnetic declination for any location on the earth.

## View Online
* Planetary Positions
    * http://74.208.17.94/oneau/planetary-position.jspx
* [Magnetic Declination]
    * http://74.208.17.94/oneau/magnetic-declination.jspx

## More Info
* Ephemeris Calculations
    * http://eqbridges.wordpress.com/2011/06/11/ephemeris-calculations/
* Understanding JPL Ephemerides Data, Pt. 1 & 2
    * http://eqbridges.wordpress.com/2010/02/13/understanding-jpl-ephemerides-data-pt-1/
    * http://eqbridges.wordpress.com/2010/02/15/understanding-jpl-ephemerides-data-pt-2/

## License
This software is licensed under a Creative Commons Attribution-NonCommercial (CC BY-NC) license.  Please see the LICENSE.txt file accompanying the distribution for details.

![CC BY-NC](http://i.creativecommons.org/l/by-nc/3.0/88x31.png "CC BY-NC")
