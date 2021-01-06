UPDATE public.chouette_info
SET xmlns = 'TECHNIQUE', xmlnsurl = 'http://www.okina.fr/technique', referential = 'technique', cuser = 'technique@okina.com'
WHERE id = 1;

UPDATE public.chouette_info
SET xmlns = 'TECHNIQUE', xmlnsurl = 'http://www.okina.fr/technique', referential = 'mosaic_technique', cuser = 'technique@okina.com'
WHERE id = 2;

UPDATE public.provider
SET name = 'technique', sftp_account = 'technique'
WHERE id = 1;

UPDATE public.provider
SET name = 'mosaic_technique', sftp_account = 'mosaic_technique'
WHERE id = 2;

UPDATE code_space
SET private_code = 'mosaic_technique', xmlns = 'TECHNIQUE', xmlns_url = 'http://www.okina.fr/technique'
WHERE pk = 1;

UPDATE organisation
SET private_code = 'mosaic_technique', name = 'mosaic_technique'
WHERE pk = 1;

UPDATE responsibility_set
SET private_code = 'technique', name = 'technique'
WHERE pk = 1;