CREATE TABLE IF NOT EXISTS identity.users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE/SUSPENDED/DELETED
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS identity.user_roles (
  user_id UUID NOT NULL,
  role VARCHAR(32) NOT NULL, -- USER/ADMIN/ORG (later)
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, role),
  FOREIGN KEY (user_id) REFERENCES identity.users(id) ON DELETE CASCADE
);

-- Refresh tokens stored server-side (cookie holds token value / session id)
CREATE TABLE IF NOT EXISTS identity.refresh_tokens (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  token_hash VARCHAR(255) NOT NULL,
  user_agent VARCHAR(500),
  ip_address VARCHAR(45),
  device_name VARCHAR(40),
  expires_at TIMESTAMPTZ NOT NULL,
  revoked_at TIMESTAMPTZ NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  FOREIGN KEY (user_id) REFERENCES identity.users(id) ON DELETE CASCADE
);



CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user ON identity.refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires ON identity.refresh_tokens(expires_at);

CREATE TABLE identity.password_reset_challenges (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  channel VARCHAR(16) NOT NULL, -- EMAIL (SMS/TOTP later)
  code_hash VARCHAR(255) NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  verified_at TIMESTAMPTZ NULL,
  attempt_count INT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_prc_user
    FOREIGN KEY (user_id) REFERENCES identity.users(id) ON DELETE CASCADE
);

CREATE INDEX idx_prc_user ON identity.password_reset_challenges(user_id);
CREATE INDEX idx_prc_expires ON identity.password_reset_challenges(expires_at);

CREATE TABLE identity.password_reset_sessions (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  session_hash VARCHAR(255) NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  used_at TIMESTAMPTZ NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_prs_user
    FOREIGN KEY (user_id) REFERENCES identity.users(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX ux_prs_session_hash ON identity.password_reset_sessions(session_hash);
