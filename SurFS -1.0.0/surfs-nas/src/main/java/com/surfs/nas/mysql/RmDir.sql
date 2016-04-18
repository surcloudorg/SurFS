/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
CREATE PROCEDURE `RmDir`(IN _dirid INTEGER(11))
    NOT DETERMINISTIC
    CONTAINS SQL
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
    DECLARE tmpstatus INT;
    
    DECLARE CONTINUE HANDLER FOR 1146 SET @tmpstatus=-1;   
    DECLARE CONTINUE HANDLER FOR 1243 SET @tmpstatus=-1;   
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
    	--SET tmpstatus =release_lock('nas_dir_accsess_lock');
        select 5 as restype;
    END; 
    
    --IF get_lock('nas_dir_accsess_lock',60) THEN   
        SET @tmpstatus=0;
    	SET @sqltext=CONCAT("select exists(select fileid from ",_dirid,"_Files limit 0,1) into @tmpstatus");
        PREPARE findfile FROM @sqltext;
	EXECUTE findfile; 
        DEALLOCATE PREPARE findfile; 
        IF  @tmpstatus>0 THEN
            SELECT 1 as restype;
        ELSE
            IF @tmpstatus=0 THEN
                SET @sqltext=CONCAT("drop table if exists ",_dirid,"_Files");	
                PREPARE deltab FROM @sqltext;
                EXECUTE deltab; 
        	DEALLOCATE PREPARE deltab; 
            END IF;      
            SELECT exists(select dirid from Directorys where parentid=_dirid limit 0,1) into tmpstatus;
            IF tmpstatus>0 THEN
            	SELECT 2 as restype;
            ELSE
                delete from Directorys where dirid=_dirid;
                SELECT 0 as restype;
            END IF; 
        END IF;
        --SET tmpstatus =release_lock('nas_dir_accsess_lock');
    --ELSE
    --    select 4 as restype;
    --END IF;
END;
