package com.gamerlink.profile.util;

import com.gamerlink.profile.dto.Profiledtos.*;
import com.gamerlink.profile.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfileMapper {

    public ProfileSummaryResponse toSummary(Profile p) {
        return new ProfileSummaryResponse(
                p.getHandle(),
                p.getDisplayName(),
                p.getAvatarUrl(),
                p.getHeadline()
        );
    }

    public ProfileResponse toFullResponse(Profile p) {
        List<ProfileGameResponse> games = p.getGameEntries().stream()
                .map(this::toGameResponse)
                .toList();

        return new ProfileResponse(
                p.getId(),
                p.getHandle(),
                p.getDisplayName(),
                p.getAvatarUrl(),
                p.getHeadline(),
                p.getBio(),
                p.getRegion(),
                p.getSocials(),
                games,
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    public ProfileGameResponse toGameResponse(ProfileGame pg) {
        Game g = pg.getGame();
        GameQueue q = pg.getGameQueue();
        GameRank r = pg.getRank();

        return new ProfileGameResponse(
                pg.getId(),
                new GameRef(g.getId(), g.getSlug(), g.getName(), g.getIconUrl()),
                new QueueRef(q.getId(), q.getCode(), q.getDisplayName()),
                r == null ? null : new RankRef(r.getId(), r.getCode(), r.getDisplayName(), r.getIconUrl(), r.getSortOrder()),
                pg.getPlatform(),
                pg.getRole(),
                pg.getCreatedAt(),
                pg.getUpdatedAt()
        );
    }

    public void applyUpdate(Profile profile, UpdateProfileRequest req) {
        if (req.displayName() != null) profile.setDisplayName(req.displayName());
        if (req.avatarUrl()   != null) profile.setAvatarUrl(req.avatarUrl());
        if (req.headline()    != null) profile.setHeadline(req.headline());
        if (req.bio()         != null) profile.setBio(req.bio());
        if (req.region()      != null) profile.setRegion(req.region());
        if (req.socials()     != null) profile.setSocials(req.socials());
    }
}