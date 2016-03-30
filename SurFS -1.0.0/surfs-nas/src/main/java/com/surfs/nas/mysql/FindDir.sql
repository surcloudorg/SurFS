
CREATE PROCEDURE `FindDir`(IN _path VARCHAR(5120))
    NOT DETERMINISTIC
    CONTAINS SQL
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
    DECLARE _dirname CHAR(255);
    DECLARE _dirid INTEGER(11) DEFAULT 0;
    DECLARE _id INTEGER(11) DEFAULT 0;
    DECLARE _index INT;
    DECLARE _dirs VARCHAR(512) DEFAULT NULL;

    SET _index=LOCATE('/',_path,1); 
    WHILE _index>0 do
        SET _dirname=SUBSTRING(_path,1,_index-1);
        SET _path=SUBSTRING(_path,_index+1);
        select dirid from Directorys where parentid=_dirid and dirname=_dirname into _id;
        if _id>0 then
        	SET _dirid=_id;
            if _dirs is NULL then
                SET _dirs=CONVERT(_dirid,CHAR);
            else
                SET _dirs=CONCAT(_dirs,'/',_dirid);
            end if;          
            SET _id=0;
            SET _index=LOCATE('/',_path,1);
        else
            SET _index=0;
        end if;
    END WHILE;
    SELECT _dirs as dirids;
END;