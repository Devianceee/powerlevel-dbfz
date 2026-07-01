CREATE TABLE player
(
    id         BIGINT PRIMARY KEY,
    steam_id   BIGINT,
    name       TEXT        NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE replay
(
    id                 BIGINT PRIMARY KEY,

    winner_id          BIGINT      NOT NULL REFERENCES player (id),
    loser_id           BIGINT      NOT NULL REFERENCES player (id),

    winner_character_1 SMALLINT    NOT NULL,
    winner_character_2 SMALLINT    NOT NULL,
    winner_character_3 SMALLINT    NOT NULL,

    loser_character_1  SMALLINT    NOT NULL,
    loser_character_2  SMALLINT    NOT NULL,
    loser_character_3  SMALLINT    NOT NULL,

    mode               SMALLINT    NOT NULL,
    game_version       SMALLINT    NOT NULL,

    played_at          TIMESTAMPTZ NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE rating
(
    player_id  BIGINT PRIMARY KEY REFERENCES player (id),

    rating     DOUBLE PRECISION NOT NULL,
    deviation  DOUBLE PRECISION NOT NULL,
    volatility DOUBLE PRECISION NOT NULL,

    updated_at TIMESTAMPTZ      NOT NULL DEFAULT NOW()
);

CREATE TABLE rating_history
(
    id                BIGSERIAL PRIMARY KEY,

    replay_id         BIGINT           NOT NULL REFERENCES replay (id),
    player_id         BIGINT           NOT NULL REFERENCES player (id),

    rating_before     DOUBLE PRECISION NOT NULL,
    rating_after      DOUBLE PRECISION NOT NULL,

    deviation_before  DOUBLE PRECISION NOT NULL,
    deviation_after   DOUBLE PRECISION NOT NULL,

    volatility_before DOUBLE PRECISION NOT NULL,
    volatility_after  DOUBLE PRECISION NOT NULL,

    created_at        TIMESTAMPTZ      NOT NULL DEFAULT NOW()
);