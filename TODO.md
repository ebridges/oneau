xx 1.  Modify DAOFactory to accept jdbc params as args.
xx. 2. Create test of DAOFactory to confirm it
3XX. Modify EphemerisServlet.init() to:
     a. get jdbc params from properties file
XX     b. initialize the DAOFactory
XX     c. get an instance of EphemerisDAO
XX     d. initialize instance of Ephemeris with the dao
XX 4. Modify Ephemeris to use a member dao instead of calling the factory directly.
5. Change UI to include appropriate date ranges, and to include option to select different ephemeris data.


BUGS
XX 1. Missing ephemeris files: select * from oneau.EPHEMERIS_DATA where filename in (
 'ascp1800.405',
  'ascp1820.405',
   'ascp1840.405',
    'ascp1860.405',
     'ascp1880.405'
 )
	-- due to use of list in EphemerisDataFile, change to accept a list of filenames on cmd line
XX 2. ascp2200.405 seems to lose some accuracy when reading data.
   -- seems to be a bug in coefficient parsing logic which skips first digit + '.' in coefficients. this only occurs
      in the 2200 file -- all other files begin with 0 + '.', which is not significant (?).
XX 3. max observation_num is 99, but is expected to be 229/230.  Each observation should have a start & end date + 1018 coefficients.
   -- bug in the way that loader was reading observations -- assumed that an observation started with exactly 5 blanks,
      but could be 3-5 blanks.
XX 4. observation parsing is skipping the first two coefficients (after begin/end dates).  see query:
   -- Before, first observation was 757058.861894
   -- If correct, first observation should be 21290125.8767516427  
   SELECT O.id, D.FILENAME, O.observation_num, I.range_from, I.range_to, M.NAME, O.COEFFICIENT 
   FROM ONEAU.OBSERVATION O
           LEFT JOIN ONEAU.EPHEMERIS_DATA D ON O.FILE_ID = D.ID 
           LEFT JOIN oneau.measured_item M ON O.measured_item_id = M.id
           LEFT JOIN oneau.ephemeris_interval I ON O.interval_id = I.id
   WHERE D.FILENAME = '/ephemeris/ascp2000.405'
   AND M.name = 'Mercury'
   AND O.observation_num = 22
5. when running with HSQL driver, an error occurred:
    Exception in thread "main" java.lang.IllegalArgumentException: java.sql.SQLIntegrityConstraintViolationException: integrity constraint violation: unique constraint or index violation; SYS_CT_59 table: EPHEMERIS_INTERVAL
        at com.oneau.loader.ephemeris.JdbcObservationWriter.write(JdbcObservationWriter.java:113)
        at com.oneau.parser.ephemeris.AscpFileParser.readObservationsFromFile(AscpFileParser.java:59)
        at com.oneau.parser.ephemeris.EphemerisParser.run(EphemerisParser.java:79)
        at com.oneau.parser.ephemeris.EphemerisParser.main(EphemerisParser.java:40)
    Caused by: java.sql.SQLIntegrityConstraintViolationException: integrity constraint violation: unique constraint or index violation; SYS_CT_59 table: EPHEMERIS_INTERVAL
        at org.hsqldb.jdbc.Util.sqlException(Unknown Source)
        at org.hsqldb.jdbc.Util.sqlException(Unknown Source)
        at org.hsqldb.jdbc.JDBCStatement.fetchResult(Unknown Source)
        at org.hsqldb.jdbc.JDBCStatement.executeUpdate(Unknown Source)
        at com.oneau.loader.ephemeris.JdbcObservationWriter.executeUpdate(JdbcObservationWriter.java:118)
        at com.oneau.loader.ephemeris.JdbcObservationWriter.write(JdbcObservationWriter.java:107)
   -- After analysis it was determined that file ascp2180.405 ends with the same date range which ascp2200.405 begins with.
   -- It's not clear how to proceed with that.  Even though the application should be able to link up the observations with
      a preexisting date range (see: com.oneau.loader.ephemeris.AbstractSqlGenerator#writeFileInfo()), there are no other
      circumstances in the ephemeris files where there are duplicate date ranges.
   -- this also did not occur with the postgresql jdbc driver since there was no unique constraint on the combo of to/from range.
6. Only 3 date ranges are created in the ephemeris_interval table with the HSQL driver.  PSQL driver does not have this behavior.
7. EphemerisDataFile doesn't have metadata for all date ranges: need begin/end & # observations.
