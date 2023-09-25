package ovh.litapp.mms.artist.data.entities;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@MappedEntity("artist_of_the_day")
public record ArtistOfTheDayEntity(
        @ManyToOne
        @JoinColumn(name = "artist_id", referencedColumnName = "id")
        @Nullable
        ArtistEntity artist,
        @Id
        @NotNull
        LocalDate date
) {
}
