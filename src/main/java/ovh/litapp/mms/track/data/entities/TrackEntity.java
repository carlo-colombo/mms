package ovh.litapp.mms.track.data.entities;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ovh.litapp.mms.artist.data.entities.ArtistEntity;

import java.util.List;
import java.util.UUID;

@MappedEntity("track")
public record TrackEntity(
        @Nullable @Id @AutoPopulated
        UUID id,

        @NotBlank
        @NotNull
        String title,
        @NotBlank
        @Nullable
        String genre,

        @NotBlank
        @Nullable
        String album,

        @NotBlank
        @NotNull
        Integer lengthInSeconds,

        @ManyToMany( mappedBy = "tracks")
        @JoinTable(
                name = "artist_track",
                joinColumns = @JoinColumn(
                        name = "track_id",
                        table = "artist_track",
                        referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(
                        name = "artist_id",
                        table = "artist_track",
                        referencedColumnName = "id"))
        List<ArtistEntity> artists
) {
}
