package ovh.litapp.mms.artist.data;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ovh.litapp.mms.artist.data.entities.AliasEntity;
import ovh.litapp.mms.artist.data.entities.ArtistEntity;
import ovh.litapp.mms.artist.data.entities.MainNameEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public abstract class ArtistRepository implements CrudRepository<ArtistEntity, UUID> {
    private final AliasRepository aliasRepository;
    private final MainNameRepository mainNameRepository;

    public ArtistRepository(AliasRepository aliasRepository, MainNameRepository mainNameRepository) {
        this.aliasRepository = aliasRepository;
        this.mainNameRepository = mainNameRepository;
    }

    @Override
    @Join("mainName")
    @Join("mainName.alias")
    @Join("aliases")
    public abstract @NonNull Optional<ArtistEntity> findById(@NonNull UUID uuid);

    @Transactional
    public ArtistEntity save(@NotNull @NotBlank String name) {
        var artist = this.save(new ArtistEntity());
        var alias = aliasRepository.save(new AliasEntity(name, artist));
        mainNameRepository.save(new MainNameEntity(artist.id(), alias));

        return artist;
    }

    @Transactional
    public ArtistEntity addAliasAndUpdateMainName(ArtistEntity artist, @NotNull @NotBlank String name) {
        var mainName = mainNameRepository
                .findById(artist.id())
                .orElseThrow(() -> new ArtistNotFoundException());
        var alias = aliasRepository
                .findByArtistAndName(artist, name)
                .orElseGet(() -> aliasRepository.save(new AliasEntity(name, artist)));

        var updatedMainName = new MainNameEntity(mainName.artist(), alias);
        mainNameRepository.update(updatedMainName);

        return artist;
    }
}
