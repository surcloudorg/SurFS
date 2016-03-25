
CREATE PROCEDURE `MkDir`(IN _dirname VARCHAR(255), IN _parentid INTEGER(11))
    NOT DETERMINISTIC
    CONTAINS SQL
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
    DECLARE tmpstatus INT;     
    
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
    	--SET tmpstatus =release_lock('nas_dir_accsess_lock');
        select 5 as restype;
    END; 
          

    --IF get_lock('nas_dir_accsess_lock',60) THEN   
        --tmpstatus,
        SET tmpstatus=1;
        IF _parentid>0 then
            SELECT exists (select dirid from Directorys where dirid=_parentid) into tmpstatus;
        END IF;
        IF tmpstatus>0 THEN 	
            --@_id,0
            SET @_id=0;
            SET @sqltext=CONCAT("select fileid,volumeid,randomname,length,lastmodified from ", _parentid,
              "_Files where filename='",_dirname,"' into @_id,@_volumeid,@_randomname,@_length,@_lastmodified"); 
            PREPARE findfile FROM @sqltext;
	    EXECUTE findfile; 
            DEALLOCATE PREPARE findfile; 			
            if @_id>0 THEN
                
            	select 1 as restype, @_id,@_volumeid,@_randomname,@_length,@_lastmodified;
            ELSE
                select dirid from Directorys where parentid=_parentid and dirname=_dirname into @_id;
 		if @_id>0 then
                    
                    select 2 as restype, @_id;
                else
             
                    insert into Directorys(parentid,dirname) values(_parentid,_dirname);
                    select LAST_INSERT_ID() into @_id;
                   
                    SET @sqltext=CONCAT("create table if not exists ",@_id,
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
                    
                    select 0 as restype,@_id;
                END IF;
            END IF;
        ELSE
  
            select 3 as restype;
    	END IF;
    	--SET tmpstatus =release_lock('nas_dir_accsess_lock');
    --ELSE
        
        --select 4 as restype;
    --END IF;
END;