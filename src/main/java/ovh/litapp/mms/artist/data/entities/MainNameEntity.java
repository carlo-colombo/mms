package ovh.litapp.mms.artist.data.entities;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

@MappedEntity("main_name")
public record MainNameEntity(
        @ManyToOne
        @JoinColumn(name="artist_id", referencedColumnName = "id")
        @Nullable
        @Id
        UUID artist,

        @ManyToOne
        @JoinColumn(name="alias_id", referencedColumnName = "id")
        @Nullable AliasEntity alias) {
}
