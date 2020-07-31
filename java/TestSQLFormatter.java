/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017,2020 Roger L. Whitcomb.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *	Test the new SQLFormatter class so we can do stand-alone testing.
 *
 * History:
 *	25-May-2016 (rlwhitcomb)
 *	    Initial coding.
 *	26-May-2016 (rlwhitcomb)
 *	    Add some procedure and rule examples to test.
 *	    Display any exceptions caught during formatting.
 *	20-Jun-2016 (rlwhitcomb)
 *	    Add additional examples.
 *	    Add the ability to specify multiple tests via one "-t" parameter
 *	    as comma-separated values or dash-separated ranges.
 *	20-Jun-2016 (rlwhitcomb)
 *	    Move the useful parsing of multiple number values into the Options class.
 *	20-Jun-2016 (rlwhitcomb)
 *	    Add the remaining examples to our internal tests.
 *	    Set the "debug" flag in the formatter if requested on our command line.
 *	05-Jul-2016 (rlwhitcomb)
 *	    Simplify the Intl.PackageResourceProvider installation.
 *	05-Aug-2016 (rlwhitcomb)
 *	    Add some more test cases from this issue.  Report totals at the end.
 *	11-Aug-2016 (rlwhitcomb)
 *	    One glitch in Procedures with no local variables.
 *	25-Aug-2016 (rlwhitcomb)
 *	    Add the "monster.sql" as a test.
 *	21-Mar-2017 (rlwhitcomb)
 *	    Call new, more automatic method of Intl resource initialization.
 *	16-Jun-2017 (rlwhitcomb)
 *	    Adapt to new return values from LineProcessor methods.
 *	28-Jul-2017 (rlwhitcomb)
 *	    As part of cleanup, move the default package resource initialization
 *	    into Intl itself, so all the callers don't have to do it.
 *	18-Feb-2020 (rlwhitcomb)
 *	    Change to use default methods of LineProcessor interface.
 */
import java.io.*;
import java.util.*;

import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.FileProcessor;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.LineProcessor;
import info.rlwhitcomb.util.Options;
import info.rlwhitcomb.util.SQLFormatter;


public class TestSQLFormatter
{
	private static final String[] TEST_CASES = {
	    "create table dummy._1010 (firstname varchar(100) with null with default, middlename varchar(255) with null with default, lastname varchar(100) with null with default, suffix varchar(100) with null with default, company varchar(100) with null with default, department bigint with null with default, jobtitle varchar(100) with null with default, businessstreet varchar(100) with null with default, businessstreet2 varchar(255) with null with default, businessstreet3 varchar(255) with null with default, businesscity varchar(100) with null with default ) with NOPARTITION",
	    "create view  geometry_columns(f_table_schema, f_table_name, f_geometry_column, geometry_type, coord_dimension, srid)as select VARCHAR(r.relowner, 256), VARCHAR(r.relid, 256), VARCHAR(a.attname, 256), VARCHAR(geomname(a.attgeomtype), 32), geomdimensions(a.attgeomtype), a.attsrid from \"owner\". relation r, \"owner\". attribute a where r.reltid=a.attrelid and r.reltidx=a.attrelidx and a.attver_dropped=0 and a.attgeomtype> -1",
	    "create view  access as select table_name=r.relid, table_owner=r.relowner, table_type=char(byteextract('TVI', (mod((r.relstat/32), (2)) +(2*(mod((r.relstat/128), (2))))) +1)), system_use=char(byteextract('USSG', (mod(r.relstat, (2))) +(mod((r.relstat/16384), (2))) +(3*mod((r.relstat2/16), (2))) +1)), permit_user=p.prouser, permit_type=permittype(p.proopset, 0)from \"owner\". relation r, \"owner\". protect p where r.reltid=p.protabbase and(mod((p.proopset/128), 2)=0)",
	    "create view  alt_columns(table_name, table_owner, key_id, column_name, key_sequence)as select r1.relid, r1.relowner, r2.reltidx, a.attname, int2(a.attkdom)from \"owner\". relation r1, \"owner\". relation r2, \"owner\". attribute a where r1.relidxcount>0 and r1.reltid=r2.reltid and r2.reltidx!=0 and MOD(r2.relstat/65536, (2))=1 and r2.reltid=a.attrelid and r2.reltidx=a.attrelidx and a.attname!='tidp' and a.attname!='TIDP'",
	    "create view  audittables as select table_name=r.relid, table_owner=r.relowner, g.audit_log, register_date=gmt_timestamp(g.reg_date)from \"owner\". relation r, \"owner\". gw06_relation g where r.reltid=g.reltid and r.reltidx=g.reltidx and session_priv('auditor')='Y'",
	    "create procedure  apitest1(    p1 varchar(32) not null,     p2 integer not null,     p3 char(4) with null,     p4 tinyint with null,     p5 smallint with null,     p6 bigint with null,     p7 float4 with null,     p8 float with null,     p9 integer with null,     p10 integer with null )as declare     sum integer not null; begin     message 'This is a test message from apitest1';     :sum = :p1 + :p2;    :p1 = :p1 + 1;    :p2 = :p2 + 1;    return :sum; end",
	    "create procedure  apitest2(    table_name varchar(32) with null,     column_count integer with null ) as declare     table_count integer not null; begin     message 'This is a test message from apitest2';     if ( :table_name is null ) then         select table_name into :table_name from \"owner\". tables;     endif;     message 'This is a second test message from apitest2';     select count(*) into :column_count from \"owner\". columns         where table_name = :table_name;     select count(*) into :table_count from \"owner\". tables;     message 'This is a third test message from apitest2';     return :table_count; end",
	    "create rule  \"rlwhitcomb\".\"$has_a_r00023a88000000003\" AFTER DELETE FROM \"rlwhitcomb\".\"has_primary_key\" WHERE old.\"pkey1\" IS NOT NULL  AND old.\"pkey2\" IS NOT NULL  AND old.\"pkey3\" IS NOT NULL  FOR EACH STATEMENT EXECUTE PROCEDURE \"rlwhitcomb\". \"$has_a_r00023a88000000003\"(\"pkey1\" = old.\"pkey1\",\"pkey2\" = old.\"pkey2\",\"pkey3\" = old.\"pkey3\" )",
	    "create rule  \"rlwhitcomb\".\"$has_a_r00023a88000000004\" AFTER UPDATE( \"pkey1\",\"pkey2\",\"pkey3\" ) OF \"rlwhitcomb\".\"has_primary_key\" WHERE old.\"pkey1\" IS NOT NULL  AND old.\"pkey2\" IS NOT NULL  AND old.\"pkey3\" IS NOT NULL  AND ( new.\"pkey1\" != old.\"pkey1\" OR new.\"pkey2\" != old.\"pkey2\" OR new.\"pkey3\" != old.\"pkey3\" ) FOR EACH STATEMENT EXECUTE PROCEDURE \"rlwhitcomb\". \"$has_a_r00023a88000000004\"(new1 = new.\"pkey1\",new2 = new.\"pkey2\",new3 = new.\"pkey3\" , old1 = old.\"pkey1\",old2 = old.\"pkey2\",old3 = old.\"pkey3\" )",
	    "create rule  \"rlwhitcomb\".\"$has_f_r00023a7b000000003\" AFTER DELETE FROM \"rlwhitcomb\".\"has_primary_key\" WHERE old.\"pkey1\" IS NOT NULL  AND old.\"pkey2\" IS NOT NULL  AND old.\"pkey3\" IS NOT NULL  FOR EACH STATEMENT EXECUTE PROCEDURE \"rlwhitcomb\". \"$has_f_r00023a7b000000003\"(\"pkey1\" = old.\"pkey1\",\"pkey2\" = old.\"pkey2\",\"pkey3\" = old.\"pkey3\" )",
	    "CREATE RULE  \"system\".\"$user__r0000014f000000001\" after insert into \"system\".\"user_profile\" WHERE new.\"up_airport\" IS NOT NULL  FOR EACH STATEMENT EXECUTE PROCEDURE \"system\". \"$user__r0000014f000000001\"(\"up_airport\" = new.\"up_airport\" )",
	    "CREATE RULE  \"system\".\"$user__r0000014f000000002\" after update ( \"up_airport\") OF \"SYSTEM\".\"USER_PROFILE\" WHERE NEW.\"UP_AIRPORT\" IS NOT NULL  AND ( new.\"up_airport\" ! = old.\"up_airport\" OR old.\"up_airport\" IS NULL  ) FOR EACH STATEMENT EXECUTE PROCEDURE \"SYSTEM\". \"$USER__R0000014F000000002\"(\"up_airport\"  = new.\"up_airport\" )",
	    "create procedure  get_airlines result row( nchar(3) not null, nchar(3) not null, nvarchar(60) not null) as declare iatacode nchar(3); icaocode nchar(3); name nvarchar(60); begin for select al_iatacode, al_icaocode, al_name into :iatacode, :icaocode, :name from \"rlwhitcomb\". airline do return row (:iatacode, :icaocode, :name); endfor; end",
	    "create procedure  get_airports result row( nchar(3) not null, nvarchar(30) not null, nvarchar(50) not null) as declare iatacode nchar(3); place nvarchar(30); name nvarchar(50); begin for select ap_iatacode, ap_place, ap_name into :iatacode, :place, :name from \"rlwhitcomb\". airport do return row (:iatacode, :place, :name); endfor; end",
	    "create procedure  get_my_airlines( alname nvarchar(60) ) result row( nchar(3) not null, nchar(3) not null, nvarchar(60) not null) as declare iatacode nchar(3); icaocode nchar(3); name nvarchar(60); begin for select al_iatacode, al_icaocode, al_name into :iatacode, :icaocode, :name from \"rlwhitcomb\". airline where al_name like :alname do return row (:iatacode, :icaocode, :name); endfor; end",
	    "CREATE PROCEDURE  get_my_airports ( ccode NCHAR(2) NOT NULL NOT DEFAULT, area NVARCHAR(30) NOT NULL NOT DEFAULT) RESULT ROW( nchar(3) not null,  nvarchar(30) not null,  nvarchar(50) not null) AS DECLARE IATACODE NCHAR(3); PLACE NVARCHAR(30); NAME NVARCHAR(50); MSTRING NVARCHAR(100) NOT NULL; BEGIN FOR SELECT AP_IATACODE, AP_PLACE, AP_NAME INTO :IATACODE, :PLACE, :NAME FROM \"SYSTEM\". AIRPORT WHERE AP_CCODE LIKE :CCODE AND AP_PLACE LIKE :AREA ORDER BY AP_IATACODE DO RETURN ROW (:iatacode,  :place,  :name); ENDFOR; END",
	    "create procedure  search_route( depart_from nchar(3) not null not default, arrive_to nchar(3) not null not default, flight_day nchar(7) not null not default) result row( nchar(3) not null, integer not null, nchar(3) not null, nchar(3) not null, systemdate not null, systemdate not null, nchar(7) not null) as declare o_airline nchar(3) not null; o_flight_num integer not null; o_depart_from nchar(3) not null; o_arrive_to nchar(3) not null; o_depart_at systemdate not null; o_arrive_at systemdate not null; o_flight_day nchar(7) not null; begin for select rt_airline, rt_flight_num, rt_depart_from, rt_arrive_to, rt_depart_at, rt_arrive_at, rt_flight_day into :o_airline, :o_flight_num, :o_depart_from, :o_arrive_to, :o_depart_at, :o_arrive_at, :o_flight_day from \"rlwhitcomb\". route where rt_depart_from = depart_from and rt_arrive_to = arrive_to and rt_flight_day like flight_day order by rt_airline, rt_flight_num do return row(:o_airline, :o_flight_num, :o_depart_from, :o_arrive_to, :o_depart_at, :o_arrive_at, :o_flight_day); endfor; end",
	    "create procedure  \"system\".\"$airli_r00000115000000001\"(\"$A\" SET OF ( \"al_ccode\" nchar(2) NOT NULL  ) ) AS DECLARE COUNTER INTEGER; BEGIN SELECT ANY( 1 ) INTO :COUNTER FROM ( \"$A\" \"$REFING\" LEFT JOIN \"system\".\"country\" \"$REFED\" ON \"$REFING\".\"al_ccode\" = \"$REFED\".\"ct_code\" ) WHERE \"$REFED\".\"ct_code\" IS NULL;  IF COUNTER != 0 THEN  EXECUTE PROCEDURE \"owner\". IIERROR( errorno = 6406, detail = 0, p_count = 3, p1 = '\"airline\"', p2 = '\"country\"', p3 = '\"$airli_r0000011500000000\"'); ENDIF; END",
	    "create procedure  \"system\".\"$airli_r00000115000000002\"(\"$A\" SET OF ( \"al_ccode\" nchar(2) NOT NULL  ) ) AS DECLARE COUNTER INTEGER; BEGIN SELECT ANY( 1 ) INTO :COUNTER FROM ( \"$A\" \"$REFING\" LEFT JOIN \"system\".\"country\" \"$REFED\" ON \"$REFING\".\"al_ccode\" = \"$REFED\".\"ct_code\" ) WHERE \"$REFED\".\"ct_code\" IS NULL;  IF COUNTER != 0 THEN  EXECUTE PROCEDURE \"owner\". IIERROR( errorno = 6407, detail = 0, p_count = 3, p1 = '\"airline\"', p2 = '\"country\"', p3 = '\"$airli_r0000011500000000\"'); ENDIF; END",
	    "create procedure  \"system\".\"$airpo_r00000121000000004\"(\"$A\" SET OF ( new1 nchar(2) NOT NULL  , old1 nchar(2) NOT NULL  ) ) AS DECLARE COUNTER INTEGER; BEGIN SELECT ANY( 1 ) INTO :COUNTER FROM \"system\".\"airport\" \"$REFING\", ( \"$A\" \"$DEL\" LEFT JOIN \"$A\" \"$INS\" ON \"$DEL\".old1 = \"$INS\".new1 ) WHERE \"$REFING\".\"ap_ccode\" = \"$DEL\".old1 AND \"$INS\".new1 IS NULL;  IF COUNTER != 0 THEN  EXECUTE PROCEDURE \"owner\". IIERROR( errorno = 6409, detail = 0, p_count = 3, p1 = '\"country\"', p2 = '\"airport\"', p3 = '\"$airpo_r0000012100000000\"'); ENDIF; END",
	    "create procedure  update_emp (in id varchar(10) NOT NULL NOT DEFAULT ) AS BEGIN insert into \"dummy\". emp(name, start_date) values (:id, date('now')); END ",
	    "CREATE TABLE up_checks_part1 AS SELECT p.rowid, p.studyid, CASE WHEN p.nid IS NULL AND p.nid15 IS NULL AND p.nid18 IS NULL THEN 0 ELSE 1 END has_nid, CASE WHEN p.nid18 IS NULL THEN 0 ELSE 1 END has_nid18, CASE WHEN p.name_u64 IS NULL THEN 0 ELSE 1 END has_name,  CASE WHEN e.studyid IS NULL THEN ifnull(p.studyid,'null') END invalid_studyid, CASE WHEN multiple.studyid IS NOT NULL THEN ifnull(p.studyid,'null') END repeated_studyid, CASE WHEN p.is_female IS NULL AND p.partner_name_u64 IS NULL AND p.dob_y IS NULL AND p.dob_m IS NULL AND p.dob_d IS NULL AND p.add_town_u64 IS NULL AND p.add_village_u64 IS NULL AND p.add_home_u64 IS NULL THEN 0 ELSE 1 END has_other_data, CASE WHEN p.nid15 NOT IN (15,18) THEN ifnull(p.nid,'null') WHEN length(trim(p.nid15)) <> 15 THEN ifnull(p.nid15,'null') WHEN length(trim(p.nid18)) <> 18 THEN ifnull(p.nid18,'null') END bad_nid_length, length (CASE WHEN length(trim(p.nid)) NOT IN (15,18) THEN ifnull(p.nid,'null') WHEN length(trim(p.nid15)) <> 15 THEN ifnull(p.nid15,'null') WHEN length(trim(p.nid18)) <> 18 THEN ifnull(p.nid18,'null') END) bad_length, nullif( trim(CASE WHEN p.nid15 NOT SIMILAR TO '[0-9]{15}' THEN p.nid15 ELSE '' END) || trim(CASE WHEN p.nid18 NOT SIMILAR TO '[0-9]{17}[0-9xX]' THEN p.nid18 ELSE '' END), '') bad_nid_character, CASE WHEN right(p.nid18,1) <> substring('10X98765432' from (mod(int4(substring(p.nid18 from 1 for 1) * 7) + int4(substring(p.nid18 from 2 for 1) * 9) + int4(substring(p.nid18 from 3 for 1) * 10) + int4(substring(p.nid18 from 4 for 1) * 5) + int4(substring(p.nid18 from 5 for 1) * 8) + int4(substring(p.nid18 from 6 for 1) * 4) + int4(substring(p.nid18 from 7 for 1) * 2) + int4(substring(p.nid18 from 8 for 1) * 1) + int4(substring(p.nid18 from 9 for 1) * 6) + int4(substring(p.nid18 from 10 for 1) * 3) + int4(substring(p.nid18 from 11 for 1) * 7) + int4(substring(p.nid18 from 12 for 1) * 9) + int4(substring(p.nid18 from 13 for 1) * 10) + int4(substring(p.nid18 from 14 for 1) * 5) + int4(substring(p.nid18 from 15 for 1) * 8) + int4(substring(p.nid18 from 16 for 1) * 4) + int4(substring(p.nid18 from 17 for 1) * 2) ,11)+1) for 1) THEN substring('10X98765432' from (mod(int4(substring(p.nid18 from 1 for 1) * 7) + int4(substring(p.nid18 from 2 for 1) * 9) + int4(substring(p.nid18 from 3 for 1) * 10) + int4(substring(p.nid18 from 4 for 1) * 5) + int4(substring(p.nid18 from 5 for 1) * 8) + int4(substring(p.nid18 from 6 for 1) * 4) + int4(substring(p.nid18 from 7 for 1) * 2) + int4(substring(p.nid18 from 8 for 1) * 1) + int4(substring(p.nid18 from 9 for 1) * 6) + int4(substring(p.nid18 from 10 for 1) * 3) + int4(substring(p.nid18 from 11 for 1) * 7) + int4(substring(p.nid18 from 12 for 1) * 9) + int4(substring(p.nid18 from 13 for 1) * 10) + int4(substring(p.nid18 from 14 for 1) * 5) + int4(substring(p.nid18 from 15 for 1) * 8) + int4(substring(p.nid18 from 16 for 1) * 4) + int4(substring(p.nid18 from 17 for 1) * 2) ,11)+1) for 1) END bad_nid_checksum, CASE WHEN _isvaliddate( (CASE WHEN substring(p.nid15 from 11 for 2) <> c.dob_d THEN substring(p.nid15 from 11 for 2) WHEN substring(p.nid18 from 13 for 2) <> c.dob_d THEN substring(p.nid18 from 13 for 2) END) || '/' || (CASE WHEN substring(p.nid15 from 9 for 2) <> c.dob_m THEN substring(p.nid15 from 9 for 2) WHEN substring(p.nid18 from 11 for 2) <> c.dob_m THEN substring(p.nid18 from 11 for 2) END) || '/' || (CASE WHEN 1900 + substring(p.nid15 from 7 for 2) <> c.dob_y THEN 1900 + substring(p.nid15 from 7 for 2) WHEN substring(p.nid18 from 7 for 4) <> c.dob_y THEN substring(p.nid15 from 7 for 4) END) ) = 0 THEN (CASE WHEN substring(p.nid15 from 11 for 2) <> c.dob_d THEN substring(p.nid15 from 11 for 2) WHEN substring(p.nid18 from 13 for 2) <> c.dob_d THEN substring(p.nid18 from 13 for 2) END) || '/' || (CASE WHEN substring(p.nid15 from 9 for 2) <> c.dob_m THEN substring(p.nid15 from 9 for 2) WHEN substring(p.nid18 from 11 for 2) <> c.dob_m THEN substring(p.nid18 from 11 for 2) END) || '/' || (CASE WHEN 1900 + substring(p.nid15 from 7 for 2) <> c.dob_y THEN 1900 + substring(p.nid15 from 7 for 2) WHEN substring(p.nid18 from 7 for 4) <> c.dob_y THEN substring(p.nid15 from 7 for 4) END) END bad_nid_dob, CASE WHEN p.nid15 <> trim(char(substring(p.nid18 from 1 for 6))) + trim(char(substring(p.nid18 from 9 for 9))) THEN p.nid15 END nid15_nid18_mismatch_nid15, CASE WHEN p.nid15 <> trim(char(substring(p.nid18 from 1 for 6))) + trim(char(substring(p.nid18 from 9 for 9))) THEN trim(char(substring(p.nid18 from 1 for 6))) + trim(char(substring(p.nid18 from 9 for 9))) END nid15_nid18_mismatch_nid18, CASE WHEN p.nid15 <> e.nid15 OR p.nid18 <> e.nid18 THEN (CASE WHEN 1 - mod(substring(p.nid15 from 15 for 1),2) <> c.is_female THEN 1 - mod(substring(p.nid15 from 15 for 1),2) WHEN 1 - mod(substring(p.nid18 from 17 for 1),2) <> c.is_female THEN 1 - mod(substring(p.nid18 from 17 for 1),2) END) END nid_gender_mismatch, c.is_female current_is_female, CASE WHEN p.nid15 <> e.nid15 OR p.nid18 <> e.nid18 THEN (CASE WHEN substring(p.nid15 from 11 for 2) <> c.dob_d THEN substring(p.nid15 from 11 for 2) WHEN substring(p.nid18 from 13 for 2) <> c.dob_d THEN substring(p.nid18 from 13 for 2) END) END nid_dob_d_mismatch, c.dob_d current_dob_d, CASE WHEN p.nid15 <> e.nid15 OR p.nid18 <> e.nid18 THEN (CASE WHEN substring(p.nid15 from 9 for 2) <> c.dob_m THEN substring(p.nid15 from 9 for 2) WHEN substring(p.nid18 from 11 for 2) <> c.dob_m THEN substring(p.nid18 from 11 for 2) END) END nid_dob_m_mismatch, c.dob_m current_dob_m, CASE WHEN p.nid15 <> e.nid15 OR p.nid18 <> e.nid18 THEN (CASE WHEN 1900 + substring(p.nid15 from 7 for 2) <> c.dob_y THEN 1900 + substring(p.nid15 from 7 for 2) WHEN substring(p.nid18 from 7 for 4) <> c.dob_y THEN substring(p.nid15 from 7 for 4) END) END nid_dob_y_mismatch, c.dob_y current_dob_y,  CASE WHEN p.nid15 <> e.nid15 THEN e.nid15 WHEN p.nid18 <> e.nid18 THEN e.nid18 END nid_change_old, CASE WHEN p.nid15 <> e.nid15 THEN p.nid15 WHEN p.nid18 <> e.nid18 THEN p.nid18 END nid_change_new, CASE WHEN p.name_u64 <> c.name_u64 THEN c.name_u64 END name_change_old_u64, CASE WHEN p.name_u64 <> c.name_u64 THEN p.name_u64 END name_change_new_u64 FROM updated_participants_part1 p LEFT JOIN extra_data e ON p.studyid = e.studyid LEFT JOIN consent2 c ON e.studyid = c.studyid AND e.latest_consent_date = c.creation_date LEFT JOIN ( SELECT studyid, count(*) freq FROM ( SELECT distinct studyid, name_u64, nid, nid15, nid18, is_female, partner_name_u64, dob_d, dob_m, dob_y, add_town_u64, add_village_u64, add_home_u64 FROM updated_participants_part1 ) x GROUP BY 1 HAVING count(*) > 1 ) multiple ON p.studyid = multiple.studyid"
	};

	private static final SQLFormatter formatter = new SQLFormatter();

	private static void showHelp() {
	    System.out.println("Usage: java TestSqlFormatter [-f filename]* [-t testnumber]* [-debug|-d]");
	    System.out.println("   If no files are given, then a series of");
	    System.out.println("     internal tests are performed.");
	    System.out.println("   The number and order of the internal tests");
	    System.out.println("     can be specified with one or more \"-t\" values");
	    System.out.format ("     where the values can be from 1 to %1$d.%n", TEST_CASES.length);
	    System.out.println("   The internal test numbers can also be entered as \"1,3-5,7\".");
	    System.out.println("   Use the \"-debug\" flag to get detailed diagnostic information.");
	}

	private static final String BANNER_1 = "*******************************";
	private static final String BANNER_2 = "******* Test #%1$3d Input *******%n";
	private static final String BANNER_3 = "*****************************";
	private static final String BANNER_4 = "********* Formatted *********";
	private static final String BANNER_5 = "********* Exception *********";

	private static boolean runTest(String test, int testNumber) {
	    String formatted = formatter.formatSQL(test);

	    System.out.println();
	    System.out.println(BANNER_1);
	    System.out.format(BANNER_2, testNumber);
	    System.out.println(BANNER_1);
	    System.out.println(test);
	    System.out.println(BANNER_3);
	    System.out.println(BANNER_4);
	    System.out.println(BANNER_3);
	    System.out.println(formatted);

	    Throwable exception = formatter.getException();
	    if (exception != null) {
		System.out.println(BANNER_3);
		System.out.println(BANNER_5);
		System.out.println(BANNER_3);
		exception.printStackTrace(System.out);
		return false;
	    }
	    return true;
	}

	private static class TestProcessor implements LineProcessor
	{
		private StringBuffer buf = new StringBuffer(32768);
		private int testNumber = 0;
		public int testsPassed = 0;
		public int testsFailed = 0;

		@Override
		public boolean preProcess(File file) {
		    buf.setLength(0);
		    testNumber++;
		    return true;
		}

		@Override
		public boolean processLine(String line) {
		    if (buf.length() > 0)
			buf.append('\n');
		    buf.append(line);
		    return true;
		}

		@Override
		public boolean postProcess(File inputFile) {
		    if (runTest(buf.toString(), testNumber))
			testsPassed++;
		    else
			testsFailed++;
		    // Continue processing regardless
		    return true;
		}
	}

	private static TestProcessor lineProcessor = new TestProcessor();

	public static void main(String[] args) {
	    Environment.setDesktopApp(true);

	    boolean needFile = false;
	    boolean needTest = false;
	    List<String> fileNames = new ArrayList<>(args.length);
	    Set<Integer> tests = null;

	    for (String arg : args) {
		String option = Options.isOption(arg);
		if (option != null) {
		    switch (option.toUpperCase()) {
			case "F":
			case "FILE":
			    needFile = true;
			    break;
			case "T":
			case "TEST":
			    needTest = true;
			    break;
			case "ECHO:TRUE":
			// Needed for compatibility with ScriptTester
			    break;
			case "D":
			case "DEBUG":
			    formatter.setDebug(true);
			    break;
			case "?":
			    showHelp();
			    return;
			default:
			    System.out.format("Unknown option: \"%1$s\"%n", arg);
			    showHelp();
			    return;
		    }
		}
		else {
		    if (needFile) {
			fileNames.add(arg);
			needFile = false;
		    }
		    else if (needTest) {
			try {
			    tests = Options.parseNumberSet(arg, 1, TEST_CASES.length);
			    needTest = false;
			}
			catch (RuntimeException ex) {
			    System.err.format("A number between 1 and %1$d is required, but you entered \"%2$s\"!%n",
				TEST_CASES.length, arg);
			    showHelp();
			    return;
			}
		    }
		}
	    }

	    int totalPassed = 0;
	    int totalFailed = 0;

	    if (!fileNames.isEmpty()) {
		// Read and process the files
		for (String fileName : fileNames) {
		    new FileProcessor(fileName, lineProcessor).processFile();
		}
		totalPassed = lineProcessor.testsPassed;
		totalFailed = lineProcessor.testsFailed;
	    }
	    else {
		// Process our internal test strings
		if (tests != null && !tests.isEmpty()) {
		    for (Integer testNumber : tests) {
			int testNo = testNumber.intValue() - 1;
			if (runTest(TEST_CASES[testNo], testNo + 1))
			    totalPassed++;
			else
			    totalFailed++;
		    }
		}
		else {
		    // Run all of the test cases
		    int testNo = 1;
		    for (String test : TEST_CASES) {
			if (runTest(test, testNo++))
			    totalPassed++;
			else
			    totalFailed++;
		    }
		}
	    }

	    System.out.format("Total number of tests: %1$3d; passed: %2$3d, failed: %3$3d.%n", totalPassed + totalFailed, totalPassed, totalFailed);
	}
}

