ALTER TABLE provider DROP COLUMN idfm;
ALTER TABLE provider DROP COLUMN code_idfm;
ALTER TABLE chouette_info ADD COLUMN idfm BOOLEAN DEFAULT true;
ALTER TABLE chouette_info ADD COLUMN code_idfm varchar(255) null;
