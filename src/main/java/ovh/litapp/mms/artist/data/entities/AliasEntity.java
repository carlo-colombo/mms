package ovh.litapp.mms.artist.data.entities;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@MappedEntity("alias")
public record AliasEntity(
        @Nullable
        @Id
        @GeneratedValue(GeneratedValue.Type.AUTO)
        Long id,
        @ManyToOne
        @JoinColumn(name = "artist_id", referencedColumnName = "id")
        @Nullable
        ArtistEntity artist,
        @NotBlank
        @NotNull
        String name
) {
    public AliasEntity(String name, ArtistEntity artist) {
        this(null, artist, name);
    }
}
