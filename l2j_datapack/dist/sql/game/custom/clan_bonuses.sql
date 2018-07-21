CREATE TABLE IF NOT EXISTS clan_bonuses (
    clan_id INTEGER PRIMARY KEY,
    bonus_type varchar(255) NOT NULL,
    bonus_time BIGINT NOT NULL
);
