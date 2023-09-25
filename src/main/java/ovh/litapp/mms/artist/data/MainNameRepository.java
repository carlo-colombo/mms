package ovh.litapp.mms.artist.data;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import ovh.litapp.mms.artist.data.entities.MainNameEntity;

import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MainNameRepository extends CrudRepository<MainNameEntity, UUID> { }
