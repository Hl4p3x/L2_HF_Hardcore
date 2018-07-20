DROP IF EXISTS login_bonuses;

CREATE TABLE login_bonuses (
    bonus_owner INTEGER NOT NULL,    
    bonus_type VARCHAR(255) NOT NULL,
    last_bonus_time BIGINT NOT NULL,
    INDEX bonus_owner_idx (bonus_owner)
);