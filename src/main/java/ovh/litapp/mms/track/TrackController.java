package ovh.litapp.mms.track;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import jakarta.inject.Inject;
import ovh.litapp.mms.api.TrackApi;
import ovh.litapp.mms.artist.data.entities.ArtistEntity;
import ovh.litapp.mms.model.CreateArtist201Response;
import ovh.litapp.mms.model.Track;
import ovh.litapp.mms.model.TrackGet200Response;
import ovh.litapp.mms.track.data.TrackRepository;
import ovh.litapp.mms.track.data.entities.TrackEntity;

import java.util.ArrayList;
import java.util.UUID;

@Controller
public class TrackController implements TrackApi {

    @Inject
    TrackRepository trackRepository;

    @Override
    public HttpResponse<CreateArtist201Response> createTrack(Track track) {
        var trackEntity = new TrackEntity(
                null,
                track.getTitle(),
                track.getGenre(),
                track.getAlbum(),
                track.getLengthInSeconds(),
                track.getArtists().stream().map(artist -> {
                    return new ArtistEntity(
                            UUID.fromString(artist.getId()),
                            null,
                            null,
                            new ArrayList<>());
                }).toList()
        );

        trackEntity.artists().forEach(artistEntity -> artistEntity.tracks().add(trackEntity));

        trackRepository.save(trackEntity);

        return HttpResponse.created(new CreateArtist201Response());
    }

    @Override
    public TrackGet200Response trackGet() {
        var resp = new TrackGet200Response();

        resp.setTracks(trackRepository.findAll().stream().map(trackEntity -> {
            var track = new Track(trackEntity.title(), trackEntity.lengthInSeconds(), trackEntity
                    .artists()
                    .stream().map(artistEntity -> artistEntity.toArtist()).toList());

            track.setAlbum(trackEntity.album());
            track.setGenre(trackEntity.genre());

            return track;
        }).toList());

        return resp;
    }
}
