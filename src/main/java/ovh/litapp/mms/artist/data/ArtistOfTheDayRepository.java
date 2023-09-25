package ovh.litapp.mms.artist.data;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import ovh.litapp.mms.artist.data.entities.ArtistEntity;
import ovh.litapp.mms.artist.data.entities.ArtistOfTheDayEntity;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ArtistOfTheDayRepository extends CrudRepository<ArtistOfTheDayEntity, LocalDate> {
    //    @Join("artist.mainName")
//    @Join("artist.mainName.alias")
//    @Join("artist.aliases")
    @Query("""
            SELECT artist_id as id
            FROM artist_of_the_day
            WHERE "date" = :date
            """)
    Optional<ArtistEntity> findByDate(LocalDate date);

    @Query("""
            SELECT a.id
            FROM artist a
            LEFT OUTER JOIN artist_of_the_day ad 
              on a.id = ad.artist_id
            GROUP BY a.id
            ORDER BY COUNT(date) ASC, random()
            LIMIT 1  
            """)
    Optional<ArtistEntity> findNewArtistOfTheDay();
}
