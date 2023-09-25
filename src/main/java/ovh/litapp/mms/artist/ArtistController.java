package ovh.litapp.mms.artist;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import ovh.litapp.mms.api.ArtistApi;
import ovh.litapp.mms.artist.data.ArtistNotFoundException;
import ovh.litapp.mms.artist.data.ArtistOfTheDayRepository;
import ovh.litapp.mms.artist.data.ArtistRepository;
import ovh.litapp.mms.artist.data.entities.ArtistEntity;
import ovh.litapp.mms.artist.data.entities.ArtistOfTheDayEntity;
import ovh.litapp.mms.model.AddAliasRequest;
import ovh.litapp.mms.model.Artist;
import ovh.litapp.mms.model.CreateArtist201Response;
import ovh.litapp.mms.model.CreateArtistRequest;

import java.time.LocalDate;
import java.util.UUID;

@Controller
public class ArtistController implements ArtistApi {
    private final ArtistRepository artistRepository;
    private final ArtistOfTheDayRepository artistOfTheDayRepository;

    public ArtistController(ArtistRepository artistRepository, ArtistOfTheDayRepository artistOfTheDayRepository) {
        this.artistRepository = artistRepository;
        this.artistOfTheDayRepository = artistOfTheDayRepository;
    }


    @Override
    public HttpResponse<Void> addAlias(String artistId, AddAliasRequest addAliasRequest) {
        var artist = new ArtistEntity(UUID.fromString(artistId), null, null, null);
        artistRepository.addAliasAndUpdateMainName(artist, addAliasRequest.getAlias());

        return HttpResponse.accepted();
    }

    @Override
    public HttpResponse<CreateArtist201Response> createArtist(CreateArtistRequest createArtistRequest) {
        var artist = artistRepository.save(createArtistRequest.getName());

        CreateArtist201Response response = new CreateArtist201Response();
        response.setId(artist.id().toString());
        return HttpResponse.created(response);
    }

    @Override
    @Get
    public Artist getArtistById(String artistId) {
        var artistEntity = artistRepository.findById(UUID.fromString(artistId)).orElseThrow(() -> new ArtistNotFoundException());

        return artistEntity.toArtist();
    }

    @Override
    public Artist getArtistOfTheDay(LocalDate date) {

        var artistEntity = artistOfTheDayRepository
                .findByDate(date)
                .or(() -> {
                    var newArtist = artistOfTheDayRepository.findNewArtistOfTheDay();
                    var artistOfTheDay = new ArtistOfTheDayEntity(newArtist.orElseThrow(), date);
                    artistOfTheDayRepository.save(artistOfTheDay);
                    return newArtist;
                })
                .orElseThrow();


        return artistRepository
                .findById(artistEntity.id())
                .orElseThrow().toArtist();
    }
}