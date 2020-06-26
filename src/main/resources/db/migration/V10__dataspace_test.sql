INSERT INTO public.chouette_info (id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format, regtopp_calendar_strategy, enable_validation, allow_create_missing_stop_place, enable_stop_place_id_mapping, enable_clean_import, enable_auto_import, migrate_data_to_provider, code_idfm)
VALUES (1, 'TEST', 'http://ratp.mosaic.pro/test', 'test', 'Mosaic', 'test@mosaic.org', NULL, NULL, 'gtfs', NULL, TRUE, TRUE, TRUE, FALSE, TRUE, 2, 12);
INSERT INTO public.chouette_info (id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format, regtopp_calendar_strategy, enable_validation, allow_create_missing_stop_place, enable_stop_place_id_mapping, enable_clean_import, enable_auto_import, migrate_data_to_provider, code_idfm)
VALUES (2, 'TEST', 'http://ratp.mosaic.pro/test', 'mosaic_test', 'Mosaic', 'mosaic_test@mosaic.org', NULL, NULL, 'gtfs', NULL, TRUE, TRUE, TRUE, FALSE, TRUE,
        NULL, 12);
INSERT INTO public.provider (id, name, sftp_account, chouette_info_id)
VALUES (1, 'test', 'test', 1);
INSERT INTO public.provider (id, name, sftp_account, chouette_info_id)
VALUES (2, 'mosaic_test', 'mosaic_test', 2);
INSERT INTO code_space (pk, entity_version, lock_version, private_code, xmlns, xmlns_url) VALUES (1, 1, 1, 'mosaic_test', 'TEST', 'http://ratp.mosaic.pro/test');
INSERT INTO organisation (pk, dtype, entity_version, lock_version, code_space_pk, private_code, name) VALUES (1, 'Authority', 1, 1, 1, 'mosaic_test', 'mosaic_test');
INSERT INTO responsibility_set (pk, entity_version, lock_version, code_space_pk, private_code, name) VALUES (1, 1, 1, 1, 'test', 'test');