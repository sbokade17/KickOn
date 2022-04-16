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

INSERT INTO public.feed_category_mapping(
	id, category_name, category_url, display_order)
	VALUES (1, 'All', 'https://content.voltax.io/feed/01fs2kwrxmxxw2', 1);

INSERT INTO public.feed_category_mapping(
	id, category_name, category_url, display_order)
	VALUES (2, 'Transfer', 'https://content.voltax.io/feed/01g0fhpn2q5zv0',2);

INSERT INTO public.feed_category_mapping(
	id, category_name, category_url, display_order)
	VALUES (3, 'World News', 'https://content.voltax.io/feed/01g0fhqd09yzbh',3);


INSERT INTO public.feed_category_mapping(
	id, category_name, category_url, display_order)
	VALUES (4, 'World Cup', 'https://content.voltax.io/feed/01fzgdpvb7z35s',4);


INSERT INTO public.feed_category_mapping(
	id, category_name, category_url, display_order)
	VALUES (5, 'Premier League', 'https://content.voltax.io/feed/01fzgdqfjnkjkg',5);


INSERT INTO public.feed_category_mapping(
	id, category_name, category_url, display_order)
	VALUES (6, 'Champions League', 'https://content.voltax.io/feed/01g0fhmseprmy2',6);


INSERT INTO public.feed_category_mapping(
	id, category_name, category_url, display_order)
	VALUES (7, 'La Liga', 'https://content.voltax.io/feed/01g0fhnw231jyq',7);
