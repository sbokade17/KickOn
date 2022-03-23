drop database kickondb;
drop user kickonmanager;
create user kickonmanager with password 'password';
create database kickondb with template=template0 owner=kickonmanager;

delete from public.amenities_table;

INSERT INTO public.amenities_table(
	id, name)
	VALUES (1, 'Parking');

INSERT INTO public.amenities_table(
	id, name)
	VALUES (2, 'Water');

INSERT INTO public.amenities_table(
	id, name)
	VALUES (3, 'Changing Room');

INSERT INTO public.amenities_table(
	id, name)
	VALUES (4, 'Floodlight');