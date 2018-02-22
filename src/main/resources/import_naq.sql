DELETE FROM provider cascade;
DELETE FROM chouette_info cascade;
DELETE FROM role cascade;
DELETE FROM entity_classification cascade;
DELETE FROM entity_type cascade;
DELETE FROM organisation cascade;
DELETE FROM code_space cascade;


INSERT INTO public.chouette_info (id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format, regtopp_calendar_strategy, enable_validation, allow_create_missing_stop_place, enable_stop_place_id_mapping, enable_clean_import, enable_auto_import, migrate_data_to_provider)
VALUES (1, 'AGE', 'http://rmr.nouvelle-aquitaine.pro/age', 'ch_75', 'Naq', 'admin@naq.fr', NULL, NULL, 'gtfs', NULL, TRUE, TRUE, TRUE, FALSE, TRUE, 1001);

INSERT INTO public.chouette_info (id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format, regtopp_calendar_strategy, enable_validation, allow_create_missing_stop_place, enable_stop_place_id_mapping, enable_clean_import, enable_auto_import, migrate_data_to_provider)
VALUES (1001, 'AGE', 'http://rmr.nouvelle-aquitaine.pro/age', 'ch_76', 'Naq', 'admin+age@naq.fr', NULL, NULL, 'neptune', NULL, TRUE, TRUE, TRUE, FALSE, TRUE,
        NULL);


INSERT INTO public.provider (id, name, sftp_account, chouette_info_id)
VALUES (1, 'CA Agen', 'age', 1);

INSERT INTO public.provider (id, name, sftp_account, chouette_info_id)
VALUES (1001, 'NAQ/CA Agen', NULL, 1001);

INSERT INTO code_space (pk, entity_version, lock_version, private_code, xmlns, xmlns_url) VALUES (1, 1, 1, 'naq', 'NAQ', 'http://www.rutebanken.org/ns/naq');

INSERT INTO role (pk, entity_version, lock_version, private_code, name)
VALUES (1, 1, 1, 'editOrganisation', 'Create and edit users, roles, responsibility sets and organisations');


INSERT INTO entity_type (pk, entity_version, lock_version, code_space_pk, private_code, name) VALUES (1, 1, 1, 1, 'StopPlace', 'Point d''arrêt');
INSERT INTO entity_type (pk, entity_version, lock_version, code_space_pk, private_code, name) VALUES (2, 1, 1, 1, 'PlaceOfInterest', 'Point d''intérêt');


INSERT INTO entity_classification (pk, entity_version, lock_version, entity_type_pk, code_space_pk, private_code, name)
VALUES (1, 1, 1, 1, 1, 'onstreetBus', 'Arrêt de bus');

INSERT INTO entity_classification (pk, entity_version, lock_version, entity_type_pk, code_space_pk, private_code, name)
VALUES (2, 1, 1, 1, 1, 'onstreetTram', 'Arrêt de tram');

INSERT INTO entity_classification (pk, entity_version, lock_version, entity_type_pk, code_space_pk, private_code, name)
VALUES (3, 1, 1, 2, 1, '*', 'Tous les points d''intérêt');

INSERT INTO organisation (pk, dtype, entity_version, lock_version, code_space_pk, private_code, name)
VALUES (nextval('hibernate_sequence'), 'Authority', 1, 1, 1, 'AGE', 'CA Agen');


ALTER SEQUENCE hibernate_sequence RESTART WITH 20000;