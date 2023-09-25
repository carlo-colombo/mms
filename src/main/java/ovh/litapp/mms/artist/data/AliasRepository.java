package ovh.litapp.mms.artist.data;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import ovh.litapp.mms.artist.data.entities.AliasEntity;
import ovh.litapp.mms.artist.data.entities.ArtistEntity;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AliasRepository extends CrudRepository<AliasEntity, Long> {
    Optional<AliasEntity> findByArtistAndName(ArtistEntity artist, String alias);
}
