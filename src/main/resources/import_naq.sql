DELETE FROM provider CASCADE;
DELETE FROM chouette_info CASCADE;
DELETE FROM responsibility_set_roles CASCADE;
DELETE FROM responsibility_role_assignment CASCADE;
DELETE FROM role CASCADE;
DELETE FROM entity_classification CASCADE;
DELETE FROM entity_type CASCADE;
DELETE FROM user_account_responsibility_sets  CASCADE;
DELETE FROM user_account CASCADE;
DELETE FROM organisation CASCADE;
DELETE FROM responsibility_set CASCADE;
DELETE FROM code_space CASCADE;
DELETE FROM contact_details CASCADE;


INSERT INTO public.chouette_info (id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format, regtopp_calendar_strategy, enable_validation, allow_create_missing_stop_place, enable_stop_place_id_mapping, enable_clean_import, enable_auto_import, migrate_data_to_provider)
VALUES (1, 'AGE', 'http://rmr.nouvelle-aquitaine.pro/age', 'age', 'Naq', 'age@naq.org', NULL, NULL, 'gtfs', NULL, TRUE, TRUE, TRUE, FALSE, TRUE, 1001);
INSERT INTO public.chouette_info (id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format, regtopp_calendar_strategy, enable_validation, allow_create_missing_stop_place, enable_stop_place_id_mapping, enable_clean_import, enable_auto_import, migrate_data_to_provider)
VALUES (2, 'TUT', 'http://rmr.nouvelle-aquitaine.pro/tut', 'tut', 'Naq', 'tut@naq.org', NULL, NULL, 'gtfs', NULL, TRUE, TRUE, TRUE, FALSE, TRUE, 1002);
INSERT INTO public.chouette_info (id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format, regtopp_calendar_strategy, enable_validation, allow_create_missing_stop_place, enable_stop_place_id_mapping, enable_clean_import, enable_auto_import, migrate_data_to_provider)
VALUES (3, 'VIT', 'http://rmr.nouvelle-aquitaine.pro/vit', 'vit', 'Naq', 'vit@naq.org', NULL, NULL, 'gtfs', NULL, TRUE, TRUE, TRUE, FALSE, TRUE, 1003);

INSERT INTO public.chouette_info (id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format, regtopp_calendar_strategy, enable_validation, allow_create_missing_stop_place, enable_stop_place_id_mapping, enable_clean_import, enable_auto_import, migrate_data_to_provider)
VALUES (1001, 'AGE', 'http://rmr.nouvelle-aquitaine.pro/age', 'naq_age', 'Naq', 'admin+age@naq.org', NULL, NULL, 'neptune', NULL, TRUE, TRUE, TRUE, FALSE, TRUE,
        NULL);
INSERT INTO public.chouette_info (id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format, regtopp_calendar_strategy, enable_validation, allow_create_missing_stop_place, enable_stop_place_id_mapping, enable_clean_import, enable_auto_import, migrate_data_to_provider)
VALUES (1002, 'TUT', 'http://rmr.nouvelle-aquitaine.pro/tut', 'naq_tut', 'Naq', 'admin+tut@naq.org', NULL, NULL, 'neptune', NULL, TRUE, TRUE, TRUE, FALSE, TRUE,
        NULL);
INSERT INTO public.chouette_info (id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format, regtopp_calendar_strategy, enable_validation, allow_create_missing_stop_place, enable_stop_place_id_mapping, enable_clean_import, enable_auto_import, migrate_data_to_provider)
VALUES (1003, 'VIT', 'http://rmr.nouvelle-aquitaine.pro/vit', 'naq_vit', 'Naq', 'admin+vit@naq.org', NULL, NULL, 'neptune', NULL, TRUE, TRUE, TRUE, FALSE, TRUE,
        NULL);


INSERT INTO public.provider (id, name, sftp_account, chouette_info_id)
VALUES (1, 'CA Agen', 'age', 1);
INSERT INTO public.provider (id, name, sftp_account, chouette_info_id)
VALUES (2, 'CA Tulles', 'tut', 2);
INSERT INTO public.provider (id, name, sftp_account, chouette_info_id)
VALUES (3, 'Grand Poitiers', 'vit', 3);

INSERT INTO public.provider (id, name, sftp_account, chouette_info_id)
VALUES (1001, 'NAQ/CA Agen', NULL, 1001);
INSERT INTO public.provider (id, name, sftp_account, chouette_info_id)
VALUES (1002, 'NAQ/CA Tulles', NULL, 1002);
INSERT INTO public.provider (id, name, sftp_account, chouette_info_id)
VALUES (1003, 'NAQ/Grand Poitiers', NULL, 1003);

INSERT INTO code_space (pk, entity_version, lock_version, private_code, xmlns, xmlns_url) VALUES (1, 1, 1, 'age', 'AGE', 'http://rmr.nouvelle-aquitaine.pro/age');
INSERT INTO code_space (pk, entity_version, lock_version, private_code, xmlns, xmlns_url) VALUES (2, 1, 1, 'tut', 'TUT', 'http://rmr.nouvelle-aquitaine.pro/tut');
INSERT INTO code_space (pk, entity_version, lock_version, private_code, xmlns, xmlns_url) VALUES (3, 1, 1, 'vit', 'VIT', 'http://rmr.nouvelle-aquitaine.pro/vit');
INSERT INTO code_space (pk, entity_version, lock_version, private_code, xmlns, xmlns_url) VALUES (1000, 1, 1, 'naq', 'NAQ', 'http://rmr.nouvelle-aquitaine.pro/naq');

INSERT INTO role (pk, entity_version, lock_version, private_code, name) VALUES (1, 1, 1, 'editOrganisation', 'Create and edit users, roles, responsibility sets and organisations');
INSERT INTO role (pk, entity_version, lock_version, private_code, name) VALUES (2, 1, 1, 'adminEditRouteData', 'adminEditRouteData - TODO desc');
INSERT INTO role (pk, entity_version, lock_version, private_code, name) VALUES (3, 1, 1, 'editRouteData', 'Submit and manipulate transport data for its organization');
INSERT INTO role (pk, entity_version, lock_version, private_code, name) VALUES (4, 1, 1, 'naq', 'naq - TODO desc');


INSERT INTO entity_type (pk, entity_version, lock_version, code_space_pk, private_code, name) VALUES (1, 1, 1, 1, 'StopPlace', 'Point d''arrêt');
INSERT INTO entity_type (pk, entity_version, lock_version, code_space_pk, private_code, name) VALUES (2, 1, 1, 1, 'PlaceOfInterest', 'Point d''intérêt');


INSERT INTO entity_classification (pk, entity_version, lock_version, entity_type_pk, code_space_pk, private_code, name)
VALUES (1, 1, 1, 1, 1, 'onstreetBus', 'Arrêt de bus');

INSERT INTO entity_classification (pk, entity_version, lock_version, entity_type_pk, code_space_pk, private_code, name)
VALUES (2, 1, 1, 1, 1, 'onstreetTram', 'Arrêt de tram');

INSERT INTO entity_classification (pk, entity_version, lock_version, entity_type_pk, code_space_pk, private_code, name)
VALUES (3, 1, 1, 2, 1, '*', 'Tous les points d''intérêt');


INSERT INTO organisation (pk, dtype, entity_version, lock_version, code_space_pk, private_code, name) VALUES (nextval('hibernate_sequence'), 'Authority', 1, 1, 1, 'AGE', 'CA Agen');
INSERT INTO organisation (pk, dtype, entity_version, lock_version, code_space_pk, private_code, name) VALUES (nextval('hibernate_sequence'), 'Authority', 1, 1, 2, 'TUT', 'CA Tulles');
INSERT INTO organisation (pk, dtype, entity_version, lock_version, code_space_pk, private_code, name) VALUES (nextval('hibernate_sequence'), 'Authority', 1, 1, 3, 'VIT', 'Grand Poitiers');


ALTER SEQUENCE hibernate_sequence RESTART WITH 20000;