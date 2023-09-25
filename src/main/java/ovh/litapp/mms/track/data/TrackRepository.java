package ovh.litapp.mms.track.data;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import ovh.litapp.mms.track.data.entities.TrackEntity;

import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TrackRepository extends CrudRepository<TrackEntity, UUID> {}
