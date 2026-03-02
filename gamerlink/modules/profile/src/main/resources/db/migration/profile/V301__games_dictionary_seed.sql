-- ============================================================
-- GamerLink – Game Dictionary Seed Data
-- Migration: V3__seed_game_dictionary.sql
-- Games: Valorant, League of Legends, Overwatch 2,
--        Apex Legends, CS2, Rocket League, Fortnite
-- ============================================================

-- ============================================================
-- GAMES
-- ============================================================
INSERT INTO profile.games (id, slug, name, icon_url, is_active) VALUES
  ('00000000-0000-0000-0000-000000000001', 'valorant',          'Valorant',          '/icons/games/valorant.png',          TRUE),
  ('00000000-0000-0000-0000-000000000002', 'league-of-legends', 'League of Legends', '/icons/games/league-of-legends.png', TRUE),
  ('00000000-0000-0000-0000-000000000003', 'overwatch-2',       'Overwatch 2',       '/icons/games/overwatch-2.png',       TRUE),
  ('00000000-0000-0000-0000-000000000004', 'apex-legends',      'Apex Legends',      '/icons/games/apex-legends.png',      TRUE),
  ('00000000-0000-0000-0000-000000000005', 'cs2',               'Counter-Strike 2',  '/icons/games/cs2.png',               TRUE),
  ('00000000-0000-0000-0000-000000000006', 'rocket-league',     'Rocket League',     '/icons/games/rocket-league.png',     TRUE),
  ('00000000-0000-0000-0000-000000000007', 'fortnite',          'Fortnite',          '/icons/games/fortnite.png',          TRUE)
ON CONFLICT (slug) DO NOTHING;


-- ============================================================
-- GAME QUEUES
-- ============================================================

-- ── Valorant ─────────────────────────────────────────────────
INSERT INTO profile.game_queues (id, game_id, code, display_name, sort_order, is_active) VALUES
  ('00000000-0001-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'competitive',  'Competitive',  0, TRUE),
  ('00000000-0001-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'premier',      'Premier',      1, TRUE),
  ('00000000-0001-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'unrated',      'Unrated',      2, TRUE),
  ('00000000-0001-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', 'spike_rush',   'Spike Rush',   3, TRUE),
  ('00000000-0001-0000-0000-000000000005', '00000000-0000-0000-0000-000000000001', 'deathmatch',   'Deathmatch',   4, TRUE)
ON CONFLICT (game_id, code) DO NOTHING;

-- ── League of Legends ─────────────────────────────────────────
INSERT INTO profile.game_queues (id, game_id, code, display_name, sort_order, is_active) VALUES
  ('00000000-0002-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'ranked_solo',  'Ranked Solo/Duo', 0, TRUE),
  ('00000000-0002-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', 'ranked_flex',  'Ranked Flex',     1, TRUE),
  ('00000000-0002-0000-0000-000000000003', '00000000-0000-0000-0000-000000000002', 'aram',         'ARAM',            2, TRUE),
  ('00000000-0002-0000-0000-000000000004', '00000000-0000-0000-0000-000000000002', 'normal_draft', 'Normal Draft',    3, TRUE)
ON CONFLICT (game_id, code) DO NOTHING;

-- ── Overwatch 2 ───────────────────────────────────────────────
INSERT INTO profile.game_queues (id, game_id, code, display_name, sort_order, is_active) VALUES
  ('00000000-0003-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'competitive_role',  'Competitive – Role Queue', 0, TRUE),
  ('00000000-0003-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', 'competitive_open',  'Competitive – Open Queue', 1, TRUE),
  ('00000000-0003-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003', 'quick_play',        'Quick Play',               2, TRUE)
ON CONFLICT (game_id, code) DO NOTHING;

-- ── Apex Legends ──────────────────────────────────────────────
INSERT INTO profile.game_queues (id, game_id, code, display_name, sort_order, is_active) VALUES
  ('00000000-0004-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004', 'ranked_br',   'Ranked Battle Royale', 0, TRUE),
  ('00000000-0004-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004', 'pubs_br',     'Pubs',                 1, TRUE),
  ('00000000-0004-0000-0000-000000000003', '00000000-0000-0000-0000-000000000004', 'mixtape',     'Mixtape',              2, TRUE)
ON CONFLICT (game_id, code) DO NOTHING;

-- ── CS2 ───────────────────────────────────────────────────────
INSERT INTO profile.game_queues (id, game_id, code, display_name, sort_order, is_active) VALUES
  ('00000000-0005-0000-0000-000000000001', '00000000-0000-0000-0000-000000000005', 'premier',      'Premier',    0, TRUE),
  ('00000000-0005-0000-0000-000000000002', '00000000-0000-0000-0000-000000000005', 'competitive',  'Competitive', 1, TRUE),
  ('00000000-0005-0000-0000-000000000003', '00000000-0000-0000-0000-000000000005', 'wingman',      'Wingman',    2, TRUE),
  ('00000000-0005-0000-0000-000000000004', '00000000-0000-0000-0000-000000000005', 'deathmatch',   'Deathmatch', 3, TRUE)
ON CONFLICT (game_id, code) DO NOTHING;

-- ── Rocket League ─────────────────────────────────────────────
INSERT INTO profile.game_queues (id, game_id, code, display_name, sort_order, is_active) VALUES
  ('00000000-0006-0000-0000-000000000001', '00000000-0000-0000-0000-000000000006', 'ranked_1v1', 'Ranked 1v1', 0, TRUE),
  ('00000000-0006-0000-0000-000000000002', '00000000-0000-0000-0000-000000000006', 'ranked_2v2', 'Ranked 2v2', 1, TRUE),
  ('00000000-0006-0000-0000-000000000003', '00000000-0000-0000-0000-000000000006', 'ranked_3v3', 'Ranked 3v3', 2, TRUE)
ON CONFLICT (game_id, code) DO NOTHING;

-- ── Fortnite ──────────────────────────────────────────────────
INSERT INTO profile.game_queues (id, game_id, code, display_name, sort_order, is_active) VALUES
  ('00000000-0007-0000-0000-000000000001', '00000000-0000-0000-0000-000000000007', 'ranked_br',         'Ranked Battle Royale', 0, TRUE),
  ('00000000-0007-0000-0000-000000000002', '00000000-0000-0000-0000-000000000007', 'ranked_zero_build', 'Ranked Zero Build',    1, TRUE)
ON CONFLICT (game_id, code) DO NOTHING;


-- ============================================================
-- GAME RANKS
-- sort_order: higher = better rank
-- ============================================================

-- ── Valorant – Competitive ────────────────────────────────────
-- queue: 00000000-0001-0000-0000-000000000001
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url) VALUES
  ('10000000-0001-0001-0000-000000000001', '00000000-0001-0000-0000-000000000001', 'iron_1',        'Iron 1',        1,  '/icons/ranks/valorant/iron_1.png'),
  ('10000000-0001-0001-0000-000000000002', '00000000-0001-0000-0000-000000000001', 'iron_2',        'Iron 2',        2,  '/icons/ranks/valorant/iron_2.png'),
  ('10000000-0001-0001-0000-000000000003', '00000000-0001-0000-0000-000000000001', 'iron_3',        'Iron 3',        3,  '/icons/ranks/valorant/iron_3.png'),
  ('10000000-0001-0001-0000-000000000004', '00000000-0001-0000-0000-000000000001', 'bronze_1',      'Bronze 1',      4,  '/icons/ranks/valorant/bronze_1.png'),
  ('10000000-0001-0001-0000-000000000005', '00000000-0001-0000-0000-000000000001', 'bronze_2',      'Bronze 2',      5,  '/icons/ranks/valorant/bronze_2.png'),
  ('10000000-0001-0001-0000-000000000006', '00000000-0001-0000-0000-000000000001', 'bronze_3',      'Bronze 3',      6,  '/icons/ranks/valorant/bronze_3.png'),
  ('10000000-0001-0001-0000-000000000007', '00000000-0001-0000-0000-000000000001', 'silver_1',      'Silver 1',      7,  '/icons/ranks/valorant/silver_1.png'),
  ('10000000-0001-0001-0000-000000000008', '00000000-0001-0000-0000-000000000001', 'silver_2',      'Silver 2',      8,  '/icons/ranks/valorant/silver_2.png'),
  ('10000000-0001-0001-0000-000000000009', '00000000-0001-0000-0000-000000000001', 'silver_3',      'Silver 3',      9,  '/icons/ranks/valorant/silver_3.png'),
  ('10000000-0001-0001-0000-000000000010', '00000000-0001-0000-0000-000000000001', 'gold_1',        'Gold 1',        10, '/icons/ranks/valorant/gold_1.png'),
  ('10000000-0001-0001-0000-000000000011', '00000000-0001-0000-0000-000000000001', 'gold_2',        'Gold 2',        11, '/icons/ranks/valorant/gold_2.png'),
  ('10000000-0001-0001-0000-000000000012', '00000000-0001-0000-0000-000000000001', 'gold_3',        'Gold 3',        12, '/icons/ranks/valorant/gold_3.png'),
  ('10000000-0001-0001-0000-000000000013', '00000000-0001-0000-0000-000000000001', 'platinum_1',    'Platinum 1',    13, '/icons/ranks/valorant/platinum_1.png'),
  ('10000000-0001-0001-0000-000000000014', '00000000-0001-0000-0000-000000000001', 'platinum_2',    'Platinum 2',    14, '/icons/ranks/valorant/platinum_2.png'),
  ('10000000-0001-0001-0000-000000000015', '00000000-0001-0000-0000-000000000001', 'platinum_3',    'Platinum 3',    15, '/icons/ranks/valorant/platinum_3.png'),
  ('10000000-0001-0001-0000-000000000016', '00000000-0001-0000-0000-000000000001', 'diamond_1',     'Diamond 1',     16, '/icons/ranks/valorant/diamond_1.png'),
  ('10000000-0001-0001-0000-000000000017', '00000000-0001-0000-0000-000000000001', 'diamond_2',     'Diamond 2',     17, '/icons/ranks/valorant/diamond_2.png'),
  ('10000000-0001-0001-0000-000000000018', '00000000-0001-0000-0000-000000000001', 'diamond_3',     'Diamond 3',     18, '/icons/ranks/valorant/diamond_3.png'),
  ('10000000-0001-0001-0000-000000000019', '00000000-0001-0000-0000-000000000001', 'ascendant_1',   'Ascendant 1',   19, '/icons/ranks/valorant/ascendant_1.png'),
  ('10000000-0001-0001-0000-000000000020', '00000000-0001-0000-0000-000000000001', 'ascendant_2',   'Ascendant 2',   20, '/icons/ranks/valorant/ascendant_2.png'),
  ('10000000-0001-0001-0000-000000000021', '00000000-0001-0000-0000-000000000001', 'ascendant_3',   'Ascendant 3',   21, '/icons/ranks/valorant/ascendant_3.png'),
  ('10000000-0001-0001-0000-000000000022', '00000000-0001-0000-0000-000000000001', 'immortal_1',    'Immortal 1',    22, '/icons/ranks/valorant/immortal_1.png'),
  ('10000000-0001-0001-0000-000000000023', '00000000-0001-0000-0000-000000000001', 'immortal_2',    'Immortal 2',    23, '/icons/ranks/valorant/immortal_2.png'),
  ('10000000-0001-0001-0000-000000000024', '00000000-0001-0000-0000-000000000001', 'immortal_3',    'Immortal 3',    24, '/icons/ranks/valorant/immortal_3.png'),
  ('10000000-0001-0001-0000-000000000025', '00000000-0001-0000-0000-000000000001', 'radiant',       'Radiant',       25, '/icons/ranks/valorant/radiant.png')
ON CONFLICT (game_queue_id, code) DO NOTHING;

-- ── League of Legends – Ranked Solo/Duo ───────────────────────
-- queue: 00000000-0002-0000-0000-000000000001
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url) VALUES
  ('10000000-0002-0001-0000-000000000001', '00000000-0002-0000-0000-000000000001', 'iron_4',        'Iron IV',        1,  '/icons/ranks/lol/iron.png'),
  ('10000000-0002-0001-0000-000000000002', '00000000-0002-0000-0000-000000000001', 'iron_3',        'Iron III',       2,  '/icons/ranks/lol/iron.png'),
  ('10000000-0002-0001-0000-000000000003', '00000000-0002-0000-0000-000000000001', 'iron_2',        'Iron II',        3,  '/icons/ranks/lol/iron.png'),
  ('10000000-0002-0001-0000-000000000004', '00000000-0002-0000-0000-000000000001', 'iron_1',        'Iron I',         4,  '/icons/ranks/lol/iron.png'),
  ('10000000-0002-0001-0000-000000000005', '00000000-0002-0000-0000-000000000001', 'bronze_4',      'Bronze IV',      5,  '/icons/ranks/lol/bronze.png'),
  ('10000000-0002-0001-0000-000000000006', '00000000-0002-0000-0000-000000000001', 'bronze_3',      'Bronze III',     6,  '/icons/ranks/lol/bronze.png'),
  ('10000000-0002-0001-0000-000000000007', '00000000-0002-0000-0000-000000000001', 'bronze_2',      'Bronze II',      7,  '/icons/ranks/lol/bronze.png'),
  ('10000000-0002-0001-0000-000000000008', '00000000-0002-0000-0000-000000000001', 'bronze_1',      'Bronze I',       8,  '/icons/ranks/lol/bronze.png'),
  ('10000000-0002-0001-0000-000000000009', '00000000-0002-0000-0000-000000000001', 'silver_4',      'Silver IV',      9,  '/icons/ranks/lol/silver.png'),
  ('10000000-0002-0001-0000-000000000010', '00000000-0002-0000-0000-000000000001', 'silver_3',      'Silver III',     10, '/icons/ranks/lol/silver.png'),
  ('10000000-0002-0001-0000-000000000011', '00000000-0002-0000-0000-000000000001', 'silver_2',      'Silver II',      11, '/icons/ranks/lol/silver.png'),
  ('10000000-0002-0001-0000-000000000012', '00000000-0002-0000-0000-000000000001', 'silver_1',      'Silver I',       12, '/icons/ranks/lol/silver.png'),
  ('10000000-0002-0001-0000-000000000013', '00000000-0002-0000-0000-000000000001', 'gold_4',        'Gold IV',        13, '/icons/ranks/lol/gold.png'),
  ('10000000-0002-0001-0000-000000000014', '00000000-0002-0000-0000-000000000001', 'gold_3',        'Gold III',       14, '/icons/ranks/lol/gold.png'),
  ('10000000-0002-0001-0000-000000000015', '00000000-0002-0000-0000-000000000001', 'gold_2',        'Gold II',        15, '/icons/ranks/lol/gold.png'),
  ('10000000-0002-0001-0000-000000000016', '00000000-0002-0000-0000-000000000001', 'gold_1',        'Gold I',         16, '/icons/ranks/lol/gold.png'),
  ('10000000-0002-0001-0000-000000000017', '00000000-0002-0000-0000-000000000001', 'platinum_4',    'Platinum IV',    17, '/icons/ranks/lol/platinum.png'),
  ('10000000-0002-0001-0000-000000000018', '00000000-0002-0000-0000-000000000001', 'platinum_3',    'Platinum III',   18, '/icons/ranks/lol/platinum.png'),
  ('10000000-0002-0001-0000-000000000019', '00000000-0002-0000-0000-000000000001', 'platinum_2',    'Platinum II',    19, '/icons/ranks/lol/platinum.png'),
  ('10000000-0002-0001-0000-000000000020', '00000000-0002-0000-0000-000000000001', 'platinum_1',    'Platinum I',     20, '/icons/ranks/lol/platinum.png'),
  ('10000000-0002-0001-0000-000000000021', '00000000-0002-0000-0000-000000000001', 'emerald_4',     'Emerald IV',     21, '/icons/ranks/lol/emerald.png'),
  ('10000000-0002-0001-0000-000000000022', '00000000-0002-0000-0000-000000000001', 'emerald_3',     'Emerald III',    22, '/icons/ranks/lol/emerald.png'),
  ('10000000-0002-0001-0000-000000000023', '00000000-0002-0000-0000-000000000001', 'emerald_2',     'Emerald II',     23, '/icons/ranks/lol/emerald.png'),
  ('10000000-0002-0001-0000-000000000024', '00000000-0002-0000-0000-000000000001', 'emerald_1',     'Emerald I',      24, '/icons/ranks/lol/emerald.png'),
  ('10000000-0002-0001-0000-000000000025', '00000000-0002-0000-0000-000000000001', 'diamond_4',     'Diamond IV',     25, '/icons/ranks/lol/diamond.png'),
  ('10000000-0002-0001-0000-000000000026', '00000000-0002-0000-0000-000000000001', 'diamond_3',     'Diamond III',    26, '/icons/ranks/lol/diamond.png'),
  ('10000000-0002-0001-0000-000000000027', '00000000-0002-0000-0000-000000000001', 'diamond_2',     'Diamond II',     27, '/icons/ranks/lol/diamond.png'),
  ('10000000-0002-0001-0000-000000000028', '00000000-0002-0000-0000-000000000001', 'diamond_1',     'Diamond I',      28, '/icons/ranks/lol/diamond.png'),
  ('10000000-0002-0001-0000-000000000029', '00000000-0002-0000-0000-000000000001', 'master',        'Master',         29, '/icons/ranks/lol/master.png'),
  ('10000000-0002-0001-0000-000000000030', '00000000-0002-0000-0000-000000000001', 'grandmaster',   'Grandmaster',    30, '/icons/ranks/lol/grandmaster.png'),
  ('10000000-0002-0001-0000-000000000031', '00000000-0002-0000-0000-000000000001', 'challenger',    'Challenger',     31, '/icons/ranks/lol/challenger.png')
ON CONFLICT (game_queue_id, code) DO NOTHING;

-- ── League of Legends – Ranked Flex (same tier structure) ─────
-- queue: 00000000-0002-0000-0000-000000000002
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url)
SELECT
  gen_random_uuid(),
  '00000000-0002-0000-0000-000000000002',
  code, display_name, sort_order, icon_url
FROM profile.game_ranks
WHERE game_queue_id = '00000000-0002-0000-0000-000000000001'
ON CONFLICT (game_queue_id, code) DO NOTHING;

-- ── Overwatch 2 – Competitive Role Queue ──────────────────────
-- queue: 00000000-0003-0000-0000-000000000001
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url) VALUES
  ('10000000-0003-0001-0000-000000000001', '00000000-0003-0000-0000-000000000001', 'bronze_5',      'Bronze 5',      1,  '/icons/ranks/ow2/bronze.png'),
  ('10000000-0003-0001-0000-000000000002', '00000000-0003-0000-0000-000000000001', 'bronze_4',      'Bronze 4',      2,  '/icons/ranks/ow2/bronze.png'),
  ('10000000-0003-0001-0000-000000000003', '00000000-0003-0000-0000-000000000001', 'bronze_3',      'Bronze 3',      3,  '/icons/ranks/ow2/bronze.png'),
  ('10000000-0003-0001-0000-000000000004', '00000000-0003-0000-0000-000000000001', 'bronze_2',      'Bronze 2',      4,  '/icons/ranks/ow2/bronze.png'),
  ('10000000-0003-0001-0000-000000000005', '00000000-0003-0000-0000-000000000001', 'bronze_1',      'Bronze 1',      5,  '/icons/ranks/ow2/bronze.png'),
  ('10000000-0003-0001-0000-000000000006', '00000000-0003-0000-0000-000000000001', 'silver_5',      'Silver 5',      6,  '/icons/ranks/ow2/silver.png'),
  ('10000000-0003-0001-0000-000000000007', '00000000-0003-0000-0000-000000000001', 'silver_4',      'Silver 4',      7,  '/icons/ranks/ow2/silver.png'),
  ('10000000-0003-0001-0000-000000000008', '00000000-0003-0000-0000-000000000001', 'silver_3',      'Silver 3',      8,  '/icons/ranks/ow2/silver.png'),
  ('10000000-0003-0001-0000-000000000009', '00000000-0003-0000-0000-000000000001', 'silver_2',      'Silver 2',      9,  '/icons/ranks/ow2/silver.png'),
  ('10000000-0003-0001-0000-000000000010', '00000000-0003-0000-0000-000000000001', 'silver_1',      'Silver 1',      10, '/icons/ranks/ow2/silver.png'),
  ('10000000-0003-0001-0000-000000000011', '00000000-0003-0000-0000-000000000001', 'gold_5',        'Gold 5',        11, '/icons/ranks/ow2/gold.png'),
  ('10000000-0003-0001-0000-000000000012', '00000000-0003-0000-0000-000000000001', 'gold_4',        'Gold 4',        12, '/icons/ranks/ow2/gold.png'),
  ('10000000-0003-0001-0000-000000000013', '00000000-0003-0000-0000-000000000001', 'gold_3',        'Gold 3',        13, '/icons/ranks/ow2/gold.png'),
  ('10000000-0003-0001-0000-000000000014', '00000000-0003-0000-0000-000000000001', 'gold_2',        'Gold 2',        14, '/icons/ranks/ow2/gold.png'),
  ('10000000-0003-0001-0000-000000000015', '00000000-0003-0000-0000-000000000001', 'gold_1',        'Gold 1',        15, '/icons/ranks/ow2/gold.png'),
  ('10000000-0003-0001-0000-000000000016', '00000000-0003-0000-0000-000000000001', 'platinum_5',    'Platinum 5',    16, '/icons/ranks/ow2/platinum.png'),
  ('10000000-0003-0001-0000-000000000017', '00000000-0003-0000-0000-000000000001', 'platinum_4',    'Platinum 4',    17, '/icons/ranks/ow2/platinum.png'),
  ('10000000-0003-0001-0000-000000000018', '00000000-0003-0000-0000-000000000001', 'platinum_3',    'Platinum 3',    18, '/icons/ranks/ow2/platinum.png'),
  ('10000000-0003-0001-0000-000000000019', '00000000-0003-0000-0000-000000000001', 'platinum_2',    'Platinum 2',    19, '/icons/ranks/ow2/platinum.png'),
  ('10000000-0003-0001-0000-000000000020', '00000000-0003-0000-0000-000000000001', 'platinum_1',    'Platinum 1',    20, '/icons/ranks/ow2/platinum.png'),
  ('10000000-0003-0001-0000-000000000021', '00000000-0003-0000-0000-000000000001', 'diamond_5',     'Diamond 5',     21, '/icons/ranks/ow2/diamond.png'),
  ('10000000-0003-0001-0000-000000000022', '00000000-0003-0000-0000-000000000001', 'diamond_4',     'Diamond 4',     22, '/icons/ranks/ow2/diamond.png'),
  ('10000000-0003-0001-0000-000000000023', '00000000-0003-0000-0000-000000000001', 'diamond_3',     'Diamond 3',     23, '/icons/ranks/ow2/diamond.png'),
  ('10000000-0003-0001-0000-000000000024', '00000000-0003-0000-0000-000000000001', 'diamond_2',     'Diamond 2',     24, '/icons/ranks/ow2/diamond.png'),
  ('10000000-0003-0001-0000-000000000025', '00000000-0003-0000-0000-000000000001', 'diamond_1',     'Diamond 1',     25, '/icons/ranks/ow2/diamond.png'),
  ('10000000-0003-0001-0000-000000000026', '00000000-0003-0000-0000-000000000001', 'master_5',      'Master 5',      26, '/icons/ranks/ow2/master.png'),
  ('10000000-0003-0001-0000-000000000027', '00000000-0003-0000-0000-000000000001', 'master_4',      'Master 4',      27, '/icons/ranks/ow2/master.png'),
  ('10000000-0003-0001-0000-000000000028', '00000000-0003-0000-0000-000000000001', 'master_3',      'Master 3',      28, '/icons/ranks/ow2/master.png'),
  ('10000000-0003-0001-0000-000000000029', '00000000-0003-0000-0000-000000000001', 'master_2',      'Master 2',      29, '/icons/ranks/ow2/master.png'),
  ('10000000-0003-0001-0000-000000000030', '00000000-0003-0000-0000-000000000001', 'master_1',      'Master 1',      30, '/icons/ranks/ow2/master.png'),
  ('10000000-0003-0001-0000-000000000031', '00000000-0003-0000-0000-000000000001', 'grandmaster_5', 'Grandmaster 5', 31, '/icons/ranks/ow2/grandmaster.png'),
  ('10000000-0003-0001-0000-000000000032', '00000000-0003-0000-0000-000000000001', 'grandmaster_4', 'Grandmaster 4', 32, '/icons/ranks/ow2/grandmaster.png'),
  ('10000000-0003-0001-0000-000000000033', '00000000-0003-0000-0000-000000000001', 'grandmaster_3', 'Grandmaster 3', 33, '/icons/ranks/ow2/grandmaster.png'),
  ('10000000-0003-0001-0000-000000000034', '00000000-0003-0000-0000-000000000001', 'grandmaster_2', 'Grandmaster 2', 34, '/icons/ranks/ow2/grandmaster.png'),
  ('10000000-0003-0001-0000-000000000035', '00000000-0003-0000-0000-000000000001', 'grandmaster_1', 'Grandmaster 1', 35, '/icons/ranks/ow2/grandmaster.png'),
  ('10000000-0003-0001-0000-000000000036', '00000000-0003-0000-0000-000000000001', 'champion',      'Champion',      36, '/icons/ranks/ow2/champion.png')
ON CONFLICT (game_queue_id, code) DO NOTHING;

-- Overwatch 2 – Open Queue shares the same tier structure
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url)
SELECT gen_random_uuid(), '00000000-0003-0000-0000-000000000002', code, display_name, sort_order, icon_url
FROM profile.game_ranks
WHERE game_queue_id = '00000000-0003-0000-0000-000000000001'
ON CONFLICT (game_queue_id, code) DO NOTHING;

-- ── Apex Legends – Ranked Battle Royale ───────────────────────
-- queue: 00000000-0004-0000-0000-000000000001
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url) VALUES
  ('10000000-0004-0001-0000-000000000001', '00000000-0004-0000-0000-000000000001', 'rookie_4',    'Rookie IV',    1,  '/icons/ranks/apex/rookie.png'),
  ('10000000-0004-0001-0000-000000000002', '00000000-0004-0000-0000-000000000001', 'rookie_3',    'Rookie III',   2,  '/icons/ranks/apex/rookie.png'),
  ('10000000-0004-0001-0000-000000000003', '00000000-0004-0000-0000-000000000001', 'rookie_2',    'Rookie II',    3,  '/icons/ranks/apex/rookie.png'),
  ('10000000-0004-0001-0000-000000000004', '00000000-0004-0000-0000-000000000001', 'rookie_1',    'Rookie I',     4,  '/icons/ranks/apex/rookie.png'),
  ('10000000-0004-0001-0000-000000000005', '00000000-0004-0000-0000-000000000001', 'bronze_4',    'Bronze IV',    5,  '/icons/ranks/apex/bronze.png'),
  ('10000000-0004-0001-0000-000000000006', '00000000-0004-0000-0000-000000000001', 'bronze_3',    'Bronze III',   6,  '/icons/ranks/apex/bronze.png'),
  ('10000000-0004-0001-0000-000000000007', '00000000-0004-0000-0000-000000000001', 'bronze_2',    'Bronze II',    7,  '/icons/ranks/apex/bronze.png'),
  ('10000000-0004-0001-0000-000000000008', '00000000-0004-0000-0000-000000000001', 'bronze_1',    'Bronze I',     8,  '/icons/ranks/apex/bronze.png'),
  ('10000000-0004-0001-0000-000000000009', '00000000-0004-0000-0000-000000000001', 'silver_4',    'Silver IV',    9,  '/icons/ranks/apex/silver.png'),
  ('10000000-0004-0001-0000-000000000010', '00000000-0004-0000-0000-000000000001', 'silver_3',    'Silver III',   10, '/icons/ranks/apex/silver.png'),
  ('10000000-0004-0001-0000-000000000011', '00000000-0004-0000-0000-000000000001', 'silver_2',    'Silver II',    11, '/icons/ranks/apex/silver.png'),
  ('10000000-0004-0001-0000-000000000012', '00000000-0004-0000-0000-000000000001', 'silver_1',    'Silver I',     12, '/icons/ranks/apex/silver.png'),
  ('10000000-0004-0001-0000-000000000013', '00000000-0004-0000-0000-000000000001', 'gold_4',      'Gold IV',      13, '/icons/ranks/apex/gold.png'),
  ('10000000-0004-0001-0000-000000000014', '00000000-0004-0000-0000-000000000001', 'gold_3',      'Gold III',     14, '/icons/ranks/apex/gold.png'),
  ('10000000-0004-0001-0000-000000000015', '00000000-0004-0000-0000-000000000001', 'gold_2',      'Gold II',      15, '/icons/ranks/apex/gold.png'),
  ('10000000-0004-0001-0000-000000000016', '00000000-0004-0000-0000-000000000001', 'gold_1',      'Gold I',       16, '/icons/ranks/apex/gold.png'),
  ('10000000-0004-0001-0000-000000000017', '00000000-0004-0000-0000-000000000001', 'platinum_4',  'Platinum IV',  17, '/icons/ranks/apex/platinum.png'),
  ('10000000-0004-0001-0000-000000000018', '00000000-0004-0000-0000-000000000001', 'platinum_3',  'Platinum III', 18, '/icons/ranks/apex/platinum.png'),
  ('10000000-0004-0001-0000-000000000019', '00000000-0004-0000-0000-000000000001', 'platinum_2',  'Platinum II',  19, '/icons/ranks/apex/platinum.png'),
  ('10000000-0004-0001-0000-000000000020', '00000000-0004-0000-0000-000000000001', 'platinum_1',  'Platinum I',   20, '/icons/ranks/apex/platinum.png'),
  ('10000000-0004-0001-0000-000000000021', '00000000-0004-0000-0000-000000000001', 'diamond_4',   'Diamond IV',   21, '/icons/ranks/apex/diamond.png'),
  ('10000000-0004-0001-0000-000000000022', '00000000-0004-0000-0000-000000000001', 'diamond_3',   'Diamond III',  22, '/icons/ranks/apex/diamond.png'),
  ('10000000-0004-0001-0000-000000000023', '00000000-0004-0000-0000-000000000001', 'diamond_2',   'Diamond II',   23, '/icons/ranks/apex/diamond.png'),
  ('10000000-0004-0001-0000-000000000024', '00000000-0004-0000-0000-000000000001', 'diamond_1',   'Diamond I',    24, '/icons/ranks/apex/diamond.png'),
  ('10000000-0004-0001-0000-000000000025', '00000000-0004-0000-0000-000000000001', 'master',      'Master',       25, '/icons/ranks/apex/master.png'),
  ('10000000-0004-0001-0000-000000000026', '00000000-0004-0000-0000-000000000001', 'predator',    'Predator',     26, '/icons/ranks/apex/predator.png')
ON CONFLICT (game_queue_id, code) DO NOTHING;

-- ── CS2 – Premier ─────────────────────────────────────────────
-- Premier uses a CS Rating number but is displayed in colour bands.
-- queue: 00000000-0005-0000-0000-000000000001
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url) VALUES
  ('10000000-0005-0001-0000-000000000001', '00000000-0005-0000-0000-000000000001', 'grey',         'Grey    (0–4,999)',      1, '/icons/ranks/cs2/grey.png'),
  ('10000000-0005-0001-0000-000000000002', '00000000-0005-0000-0000-000000000001', 'light_blue',   'Light Blue (5k–9,999)',  2, '/icons/ranks/cs2/light_blue.png'),
  ('10000000-0005-0001-0000-000000000003', '00000000-0005-0000-0000-000000000001', 'blue',         'Blue    (10k–14,999)',   3, '/icons/ranks/cs2/blue.png'),
  ('10000000-0005-0001-0000-000000000004', '00000000-0005-0000-0000-000000000001', 'purple',       'Purple  (15k–19,999)',   4, '/icons/ranks/cs2/purple.png'),
  ('10000000-0005-0001-0000-000000000005', '00000000-0005-0000-0000-000000000001', 'pink',         'Pink    (20k–24,999)',   5, '/icons/ranks/cs2/pink.png'),
  ('10000000-0005-0001-0000-000000000006', '00000000-0005-0000-0000-000000000001', 'red',          'Red     (25k–29,999)',   6, '/icons/ranks/cs2/red.png'),
  ('10000000-0005-0001-0000-000000000007', '00000000-0005-0000-0000-000000000001', 'gold',         'Gold    (30,000+)',      7, '/icons/ranks/cs2/gold.png')
ON CONFLICT (game_queue_id, code) DO NOTHING;

-- CS2 – Competitive (classic Elo ranks)
-- queue: 00000000-0005-0000-0000-000000000002
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url) VALUES
  ('10000000-0005-0002-0000-000000000001', '00000000-0005-0000-0000-000000000002', 'silver_1',             'Silver I',                     1,  '/icons/ranks/cs2/silver1.png'),
  ('10000000-0005-0002-0000-000000000002', '00000000-0005-0000-0000-000000000002', 'silver_2',             'Silver II',                    2,  '/icons/ranks/cs2/silver2.png'),
  ('10000000-0005-0002-0000-000000000003', '00000000-0005-0000-0000-000000000002', 'silver_3',             'Silver III',                   3,  '/icons/ranks/cs2/silver3.png'),
  ('10000000-0005-0002-0000-000000000004', '00000000-0005-0000-0000-000000000002', 'silver_4',             'Silver IV',                    4,  '/icons/ranks/cs2/silver4.png'),
  ('10000000-0005-0002-0000-000000000005', '00000000-0005-0000-0000-000000000002', 'silver_elite',         'Silver Elite',                 5,  '/icons/ranks/cs2/silver_elite.png'),
  ('10000000-0005-0002-0000-000000000006', '00000000-0005-0000-0000-000000000002', 'silver_elite_master',  'Silver Elite Master',          6,  '/icons/ranks/cs2/silver_elite_master.png'),
  ('10000000-0005-0002-0000-000000000007', '00000000-0005-0000-0000-000000000002', 'gold_nova_1',          'Gold Nova I',                  7,  '/icons/ranks/cs2/gold_nova1.png'),
  ('10000000-0005-0002-0000-000000000008', '00000000-0005-0000-0000-000000000002', 'gold_nova_2',          'Gold Nova II',                 8,  '/icons/ranks/cs2/gold_nova2.png'),
  ('10000000-0005-0002-0000-000000000009', '00000000-0005-0000-0000-000000000002', 'gold_nova_3',          'Gold Nova III',                9,  '/icons/ranks/cs2/gold_nova3.png'),
  ('10000000-0005-0002-0000-000000000010', '00000000-0005-0000-0000-000000000002', 'gold_nova_master',     'Gold Nova Master',             10, '/icons/ranks/cs2/gold_nova_master.png'),
  ('10000000-0005-0002-0000-000000000011', '00000000-0005-0000-0000-000000000002', 'master_guardian_1',    'Master Guardian I',            11, '/icons/ranks/cs2/mg1.png'),
  ('10000000-0005-0002-0000-000000000012', '00000000-0005-0000-0000-000000000002', 'master_guardian_2',    'Master Guardian II',           12, '/icons/ranks/cs2/mg2.png'),
  ('10000000-0005-0002-0000-000000000013', '00000000-0005-0000-0000-000000000002', 'master_guardian_elite','Master Guardian Elite',        13, '/icons/ranks/cs2/mge.png'),
  ('10000000-0005-0002-0000-000000000014', '00000000-0005-0000-0000-000000000002', 'distinguished_master', 'Distinguished Master Guardian', 14, '/icons/ranks/cs2/dmg.png'),
  ('10000000-0005-0002-0000-000000000015', '00000000-0005-0000-0000-000000000002', 'legendary_eagle',      'Legendary Eagle',              15, '/icons/ranks/cs2/le.png'),
  ('10000000-0005-0002-0000-000000000016', '00000000-0005-0000-0000-000000000002', 'legendary_eagle_master','Legendary Eagle Master',      16, '/icons/ranks/cs2/lem.png'),
  ('10000000-0005-0002-0000-000000000017', '00000000-0005-0000-0000-000000000002', 'supreme',              'Supreme Master First Class',   17, '/icons/ranks/cs2/supreme.png'),
  ('10000000-0005-0002-0000-000000000018', '00000000-0005-0000-0000-000000000002', 'global_elite',         'Global Elite',                 18, '/icons/ranks/cs2/global_elite.png')
ON CONFLICT (game_queue_id, code) DO NOTHING;

-- CS2 Wingman shares the same rank structure as Competitive
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url)
SELECT gen_random_uuid(), '00000000-0005-0000-0000-000000000003', code, display_name, sort_order, icon_url
FROM profile.game_ranks
WHERE game_queue_id = '00000000-0005-0000-0000-000000000002'
ON CONFLICT (game_queue_id, code) DO NOTHING;

-- ── Rocket League – Ranked 1v1 ────────────────────────────────
-- queue: 00000000-0006-0000-0000-000000000001
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url) VALUES
  ('10000000-0006-0001-0000-000000000001', '00000000-0006-0000-0000-000000000001', 'bronze_1',         'Bronze I',           1,  '/icons/ranks/rl/bronze_1.png'),
  ('10000000-0006-0001-0000-000000000002', '00000000-0006-0000-0000-000000000001', 'bronze_2',         'Bronze II',          2,  '/icons/ranks/rl/bronze_2.png'),
  ('10000000-0006-0001-0000-000000000003', '00000000-0006-0000-0000-000000000001', 'bronze_3',         'Bronze III',         3,  '/icons/ranks/rl/bronze_3.png'),
  ('10000000-0006-0001-0000-000000000004', '00000000-0006-0000-0000-000000000001', 'silver_1',         'Silver I',           4,  '/icons/ranks/rl/silver_1.png'),
  ('10000000-0006-0001-0000-000000000005', '00000000-0006-0000-0000-000000000001', 'silver_2',         'Silver II',          5,  '/icons/ranks/rl/silver_2.png'),
  ('10000000-0006-0001-0000-000000000006', '00000000-0006-0000-0000-000000000001', 'silver_3',         'Silver III',         6,  '/icons/ranks/rl/silver_3.png'),
  ('10000000-0006-0001-0000-000000000007', '00000000-0006-0000-0000-000000000001', 'gold_1',           'Gold I',             7,  '/icons/ranks/rl/gold_1.png'),
  ('10000000-0006-0001-0000-000000000008', '00000000-0006-0000-0000-000000000001', 'gold_2',           'Gold II',            8,  '/icons/ranks/rl/gold_2.png'),
  ('10000000-0006-0001-0000-000000000009', '00000000-0006-0000-0000-000000000001', 'gold_3',           'Gold III',           9,  '/icons/ranks/rl/gold_3.png'),
  ('10000000-0006-0001-0000-000000000010', '00000000-0006-0000-0000-000000000001', 'platinum_1',       'Platinum I',         10, '/icons/ranks/rl/platinum_1.png'),
  ('10000000-0006-0001-0000-000000000011', '00000000-0006-0000-0000-000000000001', 'platinum_2',       'Platinum II',        11, '/icons/ranks/rl/platinum_2.png'),
  ('10000000-0006-0001-0000-000000000012', '00000000-0006-0000-0000-000000000001', 'platinum_3',       'Platinum III',       12, '/icons/ranks/rl/platinum_3.png'),
  ('10000000-0006-0001-0000-000000000013', '00000000-0006-0000-0000-000000000001', 'diamond_1',        'Diamond I',          13, '/icons/ranks/rl/diamond_1.png'),
  ('10000000-0006-0001-0000-000000000014', '00000000-0006-0000-0000-000000000001', 'diamond_2',        'Diamond II',         14, '/icons/ranks/rl/diamond_2.png'),
  ('10000000-0006-0001-0000-000000000015', '00000000-0006-0000-0000-000000000001', 'diamond_3',        'Diamond III',        15, '/icons/ranks/rl/diamond_3.png'),
  ('10000000-0006-0001-0000-000000000016', '00000000-0006-0000-0000-000000000001', 'champion_1',       'Champion I',         16, '/icons/ranks/rl/champion_1.png'),
  ('10000000-0006-0001-0000-000000000017', '00000000-0006-0000-0000-000000000001', 'champion_2',       'Champion II',        17, '/icons/ranks/rl/champion_2.png'),
  ('10000000-0006-0001-0000-000000000018', '00000000-0006-0000-0000-000000000001', 'champion_3',       'Champion III',       18, '/icons/ranks/rl/champion_3.png'),
  ('10000000-0006-0001-0000-000000000019', '00000000-0006-0000-0000-000000000001', 'grand_champion_1', 'Grand Champion I',   19, '/icons/ranks/rl/gc_1.png'),
  ('10000000-0006-0001-0000-000000000020', '00000000-0006-0000-0000-000000000001', 'grand_champion_2', 'Grand Champion II',  20, '/icons/ranks/rl/gc_2.png'),
  ('10000000-0006-0001-0000-000000000021', '00000000-0006-0000-0000-000000000001', 'grand_champion_3', 'Grand Champion III', 21, '/icons/ranks/rl/gc_3.png'),
  ('10000000-0006-0001-0000-000000000022', '00000000-0006-0000-0000-000000000001', 'supersonic_legend','Supersonic Legend',  22, '/icons/ranks/rl/ssl.png')
ON CONFLICT (game_queue_id, code) DO NOTHING;

-- RL 2v2 and 3v3 share the same rank structure as 1v1
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url)
SELECT gen_random_uuid(), '00000000-0006-0000-0000-000000000002', code, display_name, sort_order, icon_url
FROM profile.game_ranks WHERE game_queue_id = '00000000-0006-0000-0000-000000000001'
ON CONFLICT (game_queue_id, code) DO NOTHING;

INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url)
SELECT gen_random_uuid(), '00000000-0006-0000-0000-000000000003', code, display_name, sort_order, icon_url
FROM profile.game_ranks WHERE game_queue_id = '00000000-0006-0000-0000-000000000001'
ON CONFLICT (game_queue_id, code) DO NOTHING;

-- ── Fortnite – Ranked Battle Royale ───────────────────────────
-- queue: 00000000-0007-0000-0000-000000000001
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url) VALUES
  ('10000000-0007-0001-0000-000000000001', '00000000-0007-0000-0000-000000000001', 'bronze_1',   'Bronze 1',   1,  '/icons/ranks/fortnite/bronze_1.png'),
  ('10000000-0007-0001-0000-000000000002', '00000000-0007-0000-0000-000000000001', 'bronze_2',   'Bronze 2',   2,  '/icons/ranks/fortnite/bronze_2.png'),
  ('10000000-0007-0001-0000-000000000003', '00000000-0007-0000-0000-000000000001', 'bronze_3',   'Bronze 3',   3,  '/icons/ranks/fortnite/bronze_3.png'),
  ('10000000-0007-0001-0000-000000000004', '00000000-0007-0000-0000-000000000001', 'silver_1',   'Silver 1',   4,  '/icons/ranks/fortnite/silver_1.png'),
  ('10000000-0007-0001-0000-000000000005', '00000000-0007-0000-0000-000000000001', 'silver_2',   'Silver 2',   5,  '/icons/ranks/fortnite/silver_2.png'),
  ('10000000-0007-0001-0000-000000000006', '00000000-0007-0000-0000-000000000001', 'silver_3',   'Silver 3',   6,  '/icons/ranks/fortnite/silver_3.png'),
  ('10000000-0007-0001-0000-000000000007', '00000000-0007-0000-0000-000000000001', 'gold_1',     'Gold 1',     7,  '/icons/ranks/fortnite/gold_1.png'),
  ('10000000-0007-0001-0000-000000000008', '00000000-0007-0000-0000-000000000001', 'gold_2',     'Gold 2',     8,  '/icons/ranks/fortnite/gold_2.png'),
  ('10000000-0007-0001-0000-000000000009', '00000000-0007-0000-0000-000000000001', 'gold_3',     'Gold 3',     9,  '/icons/ranks/fortnite/gold_3.png'),
  ('10000000-0007-0001-0000-000000000010', '00000000-0007-0000-0000-000000000001', 'platinum_1', 'Platinum 1', 10, '/icons/ranks/fortnite/platinum_1.png'),
  ('10000000-0007-0001-0000-000000000011', '00000000-0007-0000-0000-000000000001', 'platinum_2', 'Platinum 2', 11, '/icons/ranks/fortnite/platinum_2.png'),
  ('10000000-0007-0001-0000-000000000012', '00000000-0007-0000-0000-000000000001', 'platinum_3', 'Platinum 3', 12, '/icons/ranks/fortnite/platinum_3.png'),
  ('10000000-0007-0001-0000-000000000013', '00000000-0007-0000-0000-000000000001', 'diamond_1',  'Diamond 1',  13, '/icons/ranks/fortnite/diamond_1.png'),
  ('10000000-0007-0001-0000-000000000014', '00000000-0007-0000-0000-000000000001', 'diamond_2',  'Diamond 2',  14, '/icons/ranks/fortnite/diamond_2.png'),
  ('10000000-0007-0001-0000-000000000015', '00000000-0007-0000-0000-000000000001', 'diamond_3',  'Diamond 3',  15, '/icons/ranks/fortnite/diamond_3.png'),
  ('10000000-0007-0001-0000-000000000016', '00000000-0007-0000-0000-000000000001', 'elite',      'Elite',      16, '/icons/ranks/fortnite/elite.png'),
  ('10000000-0007-0001-0000-000000000017', '00000000-0007-0000-0000-000000000001', 'champion',   'Champion',   17, '/icons/ranks/fortnite/champion.png'),
  ('10000000-0007-0001-0000-000000000018', '00000000-0007-0000-0000-000000000001', 'unreal',     'Unreal',     18, '/icons/ranks/fortnite/unreal.png')
ON CONFLICT (game_queue_id, code) DO NOTHING;

-- Fortnite Zero Build shares the same rank structure as BR
INSERT INTO profile.game_ranks (id, game_queue_id, code, display_name, sort_order, icon_url)
SELECT gen_random_uuid(), '00000000-0007-0000-0000-000000000002', code, display_name, sort_order, icon_url
FROM profile.game_ranks WHERE game_queue_id = '00000000-0007-0000-0000-000000000001'
ON CONFLICT (game_queue_id, code) DO NOTHING;