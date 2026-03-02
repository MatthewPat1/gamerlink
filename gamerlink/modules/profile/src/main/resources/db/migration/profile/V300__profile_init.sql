CREATE SCHEMA IF NOT EXISTS profile;

-- =========================
-- Profiles (1 per user)
-- =========================
CREATE TABLE IF NOT EXISTS profile.profiles (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL UNIQUE,

  handle VARCHAR(32) UNIQUE,
  display_name VARCHAR(64),
  avatar_url TEXT,
  headline VARCHAR(120),
  bio TEXT,
  region VARCHAR(64),
  socials JSONB NOT NULL DEFAULT '{}'::jsonb,

  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_profiles_user
    FOREIGN KEY (user_id) REFERENCES identity.users(id) ON DELETE CASCADE,

  CONSTRAINT chk_profiles_handle
    CHECK (handle IS NULL OR handle ~ '^[a-zA-Z0-9_]{3,32}$')
);

CREATE INDEX IF NOT EXISTS idx_profiles_user_id ON profile.profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_profiles_handle ON profile.profiles(handle);

-- =========================
-- Game dictionary
-- =========================
CREATE TABLE IF NOT EXISTS profile.games (
  id UUID PRIMARY KEY,
  slug VARCHAR(64) NOT NULL UNIQUE,  -- valorant, league-of-legends
  name VARCHAR(80) NOT NULL,
  icon_url TEXT,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,

  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_games_slug ON profile.games(slug);

-- =========================
-- Queue types per game
-- =========================
CREATE TABLE IF NOT EXISTS profile.game_queues (
  id UUID PRIMARY KEY,
  game_id UUID NOT NULL,
  code VARCHAR(48) NOT NULL,         -- ranked_solo, ranked_flex, arena_2v2
  display_name VARCHAR(80) NOT NULL, -- "Ranked Solo/Duo"
  sort_order INT NOT NULL DEFAULT 0,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,

  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_game_queues_game
    FOREIGN KEY (game_id) REFERENCES profile.games(id) ON DELETE CASCADE,

  CONSTRAINT uq_game_queue UNIQUE (game_id, code)
);

CREATE INDEX IF NOT EXISTS idx_game_queues_game_id ON profile.game_queues(game_id);

-- =========================
-- Ranks per queue (centralized rank appearance)
-- =========================
CREATE TABLE IF NOT EXISTS profile.game_ranks (
  id UUID PRIMARY KEY,
  game_queue_id UUID NOT NULL,

  code VARCHAR(48) NOT NULL,         -- bronze_1, silver_2, etc. (or whatever the game uses)
  display_name VARCHAR(80) NOT NULL, -- "Bronze I"
  sort_order INT NOT NULL,           -- for comparisons/sorting
  icon_url TEXT,

  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_game_ranks_queue
    FOREIGN KEY (game_queue_id) REFERENCES profile.game_queues(id) ON DELETE CASCADE,

  CONSTRAINT uq_game_rank UNIQUE (game_queue_id, code)
);

CREATE INDEX IF NOT EXISTS idx_game_ranks_queue_id ON profile.game_ranks(game_queue_id);

-- =========================
-- User's game entries
-- Supports multiple queues + platforms
-- =========================
CREATE TABLE IF NOT EXISTS profile.profile_games (
  id UUID PRIMARY KEY,
  profile_id UUID NOT NULL,
  game_id UUID NOT NULL,
  game_queue_id UUID NOT NULL,

  rank_id UUID NULL,                 -- nullable (some games/modes may not be ranked)
  platform VARCHAR(32) NOT NULL,     -- PC, PS5, Xbox, Switch, Mobile, etc.
  role VARCHAR(32),                  -- free text for now

  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_profile_games_profile
    FOREIGN KEY (profile_id) REFERENCES profile.profiles(id) ON DELETE CASCADE,

  CONSTRAINT fk_profile_games_game
    FOREIGN KEY (game_id) REFERENCES profile.games(id),

  CONSTRAINT fk_profile_games_queue
    FOREIGN KEY (game_queue_id) REFERENCES profile.game_queues(id),

  CONSTRAINT fk_profile_games_rank
    FOREIGN KEY (rank_id) REFERENCES profile.game_ranks(id),

  -- key part: allow multiple queues
  CONSTRAINT uq_profile_game_entry UNIQUE (profile_id, game_id, game_queue_id, platform)
);

CREATE INDEX IF NOT EXISTS idx_profile_games_profile_id ON profile.profile_games(profile_id);
CREATE INDEX IF NOT EXISTS idx_profile_games_game_id ON profile.profile_games(game_id);
CREATE INDEX IF NOT EXISTS idx_profile_games_queue_id ON profile.profile_games(game_queue_id);
CREATE INDEX IF NOT EXISTS idx_profile_games_rank_id ON profile.profile_games(rank_id);