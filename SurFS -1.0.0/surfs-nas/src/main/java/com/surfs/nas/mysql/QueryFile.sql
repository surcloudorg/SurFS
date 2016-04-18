/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
CREATE PROCEDURE `QueryFile`(IN _fname VARCHAR(255), IN _parentid INTEGER(11))
    NOT DETERMINISTIC
    CONTAINS SQL
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
    DECLARE CONTINUE HANDLER FOR 1146
    BEGIN  
 	SET @sqltext=CONCAT("create table if not exists ",
    			_parentid,
                        "_Files(`fileid` INTEGER(11) NOT NULL AUTO_INCREMENT,
                        `filename` CHAR(255) NOT NULL DEFAULT '',
                        `volumeid` CHAR(20) NOT NULL DEFAULT '',
                        `randomname` CHAR(32) NOT NULL DEFAULT '',
                        `length` BIGINT(20) NOT NULL DEFAULT '0',
                        `lastmodified` BIGINT(20) NOT NULL DEFAULT '0',
                        PRIMARY KEY `fileid` (`fileid`),
                        UNIQUE KEY `filename` (`filename`))"); 
        PREPARE creattab FROM @sqltext;
	EXECUTE creattab; 
        DEALLOCATE PREPARE creattab;
        SET @_id=0;
    END;  
    DECLARE CONTINUE HANDLER FOR 1243 SET @_id=0;   

    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        select 5 as restype;
    END; 
          
    SET @_id=0;
    SET @sqltext=CONCAT("select fileid,volumeid,randomname,length,lastmodified from ", _parentid,
        "_Files where filename='",_fname,"' into @_id,@_volumeid,@_randomname,@_length,@_lastmodified");      
    PREPARE findfile FROM @sqltext;
    EXECUTE findfile; 
    DEALLOCATE PREPARE findfile; 			
    if @_id>0 THEN
        select 1 as restype, @_id,@_volumeid,@_randomname,@_length,@_lastmodified;
    ELSE
        select dirid from Directorys where parentid=_parentid and dirname=_fname into @_id;
 	if @_id>0 then
            select 2 as restype, @_id;
        else        
            select 0 as restype;
        END IF;
    end if;
END;
