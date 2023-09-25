package ovh.litapp.mms.artist.data.entities;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.persistence.*;
import ovh.litapp.mms.model.Artist;
import ovh.litapp.mms.model.Track;
import ovh.litapp.mms.track.data.entities.TrackEntity;

import java.util.List;
import java.util.UUID;

@MappedEntity("artist")
public record ArtistEntity(
        @Nullable @Id @AutoPopulated UUID id,

        @Nullable
        @OneToMany(fetch = FetchType.EAGER, mappedBy = "artist")
        List<AliasEntity> aliases,

        @Nullable
        @OneToOne(fetch = FetchType.EAGER, mappedBy = "artist")
        MainNameEntity mainName,

        @ManyToMany(mappedBy = "artists")
        List<TrackEntity> tracks
) {
    public ArtistEntity() {
        this(null, null, null, null);
    }

    public Artist toArtist() {
        var artist = new Artist();
        artist.id(this.id().toString());
        artist.mainName(this.mainName().alias().name());
        this
                .aliases()
                .forEach(aliasEntity -> artist.addNamesItem(aliasEntity.name()));
        return artist;
    }
}
