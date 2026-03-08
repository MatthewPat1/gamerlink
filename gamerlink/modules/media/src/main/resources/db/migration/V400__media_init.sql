-- ============================================================
-- GamerLink – Media Module Schema
-- Migration: V4__create_media_schema.sql
-- ============================================================

CREATE SCHEMA IF NOT EXISTS media;

-- ── Enums ────────────────────────────────────────────────────────────────────

CREATE TYPE media.asset_type AS ENUM (
    'AVATAR',
    'BANNER',
    'SCREENSHOT',
    'CLIP',
    'EXTERNAL_LINK'
);

CREATE TYPE media.asset_status AS ENUM (
    'PENDING',
    'ACTIVE',
    'DELETED',
    'EXPIRED'
);

-- ── Core assets table ─────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS media.assets (

    -- Identity
    id                  UUID        PRIMARY KEY,
    owner_id            UUID        NOT NULL,

    -- Classification
    type                media.asset_type    NOT NULL,
    status              media.asset_status  NOT NULL DEFAULT 'PENDING',

    -- Storage (null for EXTERNAL_LINK)
    storage_key         VARCHAR(512),
    bucket              VARCHAR(128),
    cdn_url             TEXT,

    -- External links (null for uploaded assets)
    external_url        TEXT,

    -- File metadata — populated at completion, null while PENDING
    original_filename   VARCHAR(255),
    mime_type           VARCHAR(100),
    size_bytes          BIGINT,
    checksum_md5        VARCHAR(64),

    -- Reference counting — prevents deletion of in-use assets
    reference_count     INT         NOT NULL DEFAULT 0,

    -- Presigned URL expiry — used by the orphan cleanup scheduler
    expires_at          TIMESTAMPTZ,

    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- ── Constraints ──────────────────────────────────────────────────────────

    CONSTRAINT fk_assets_owner
        FOREIGN KEY (owner_id) REFERENCES identity.users(id) ON DELETE CASCADE,

    -- External links must always have a URL
    CONSTRAINT chk_external_url_required
        CHECK (type != 'EXTERNAL_LINK' OR external_url IS NOT NULL),

    -- Uploaded assets must have a storage key once they leave PENDING
    CONSTRAINT chk_storage_key_required
        CHECK (type = 'EXTERNAL_LINK' OR storage_key IS NOT NULL OR status = 'PENDING'),

    -- Reference count can never go negative
    CONSTRAINT chk_reference_count_non_negative
        CHECK (reference_count >= 0)
);

-- ── Indexes ───────────────────────────────────────────────────────────────────

-- Listing assets by owner (GET /media?ownerId=...)
CREATE INDEX IF NOT EXISTS idx_assets_owner_id
    ON media.assets(owner_id);

-- Filtering by status (scheduler queries, admin views)
CREATE INDEX IF NOT EXISTS idx_assets_status
    ON media.assets(status);

-- Filtering by type within an owner's assets
CREATE INDEX IF NOT EXISTS idx_assets_owner_type
    ON media.assets(owner_id, type);

-- Partial index for the orphan cleanup scheduler.
-- Only indexes PENDING rows with an expiry — the scheduler's exact query.
-- This avoids scanning the full table on every scheduler run.
CREATE INDEX IF NOT EXISTS idx_assets_pending_expires_at
    ON media.assets(expires_at)
    WHERE status = 'PENDING';