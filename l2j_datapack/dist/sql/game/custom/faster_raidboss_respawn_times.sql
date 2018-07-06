UPDATE raidboss_spawnlist SET respawn_delay=86400, respawn_random=43200 WHERE (respawn_delay=129600 and respawn_random=86400) or (boss_id = 25149);
UPDATE raidboss_spawnlist SET respawn_delay=302400, respawn_random=43200 WHERE boss_id = 29040;
UPDATE raidboss_spawnlist SET respawn_delay=43200, respawn_random=21600 WHERE respawn_delay=21600 and respawn_random=21600;