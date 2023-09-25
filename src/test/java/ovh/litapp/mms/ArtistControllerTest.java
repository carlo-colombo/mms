package ovh.litapp.mms;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ovh.litapp.mms.artist.data.AliasRepository;
import ovh.litapp.mms.artist.data.ArtistOfTheDayRepository;
import ovh.litapp.mms.artist.data.ArtistRepository;
import ovh.litapp.mms.artist.data.MainNameRepository;
import ovh.litapp.mms.model.AddAliasRequest;
import ovh.litapp.mms.model.Artist;
import ovh.litapp.mms.model.CreateArtist201Response;
import ovh.litapp.mms.model.CreateArtistRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class ArtistControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    ArtistRepository artistRepository;
    @Inject
    AliasRepository aliasRepository;

    @Inject
    MainNameRepository mainNameRepository;

    @Inject
    ArtistOfTheDayRepository artistOfTheDayRepository;

    @BeforeEach
    void setup() {
        mainNameRepository.deleteAll();
        aliasRepository.deleteAll();
        artistOfTheDayRepository.deleteAll();
        artistRepository.deleteAll();
    }

    @Test
    public void testCreation() throws Exception {
        CreateArtistRequest createArtistRequest = new CreateArtistRequest();
        createArtistRequest.name("SUper awesome band");
        var response = client
                .toBlocking()
                .exchange(HttpRequest.POST("/artist", createArtistRequest), CreateArtist201Response.class);

        assertEquals(HttpStatus.CREATED, response.status());
        assertNotNull(response.body().getId());
        assertNotEquals("", response.body().getId());
    }

    @Test
    void testRetrieval() throws Exception {
        var artistId =
                createArtist("SUper awesome band, a new return");

        var response = client
                .toBlocking()
                .exchange(HttpRequest.GET("/artist/" + artistId), Artist.class);

        assertEquals(HttpStatus.OK, response.status());
        assertEquals("SUper awesome band, a new return", response.body().getMainName());
        assertIterableEquals(Collections.singleton("SUper awesome band, a new return"), response.body().getNames());
    }


    @Test
    void testAddAlias() throws Exception {
        var artistId = createArtist("Prince");

        // add an alias
        AddAliasRequest request = new AddAliasRequest();
        request.alias("Love Symbol");

        var response = client
                .toBlocking()
                .exchange(HttpRequest.PUT("/artist/" + artistId + "/addAlias", request));

        assertEquals(HttpStatus.ACCEPTED, response.status());

        // retrieve the artist and check if aliases and main name are updated
        var artist = getArtist(artistId);
        assertIterableEquals(Arrays.asList(
                "Prince", "Love Symbol"
        ), artist.getNames());
        assertEquals("Love Symbol", artist.getMainName());

        // try to add again a past alias and check if main name is updated and alias does not change
        AddAliasRequest requestPrince = new AddAliasRequest();
        requestPrince.alias("Prince");

        response = client
                .toBlocking()
                .exchange(HttpRequest.PUT("/artist/" + artistId + "/addAlias", requestPrince));
        assertEquals(HttpStatus.ACCEPTED, response.status());

        artist = getArtist(artistId);

        assertIterableEquals(Arrays.asList(
                "Prince", "Love Symbol"
        ), artist.getNames());
        assertEquals("Prince", artist.getMainName());
    }

    @Test
    void testDoesNotCreateAliasForNonExistingArtist() throws Exception {
        var artistId = UUID.randomUUID().toString();
        AddAliasRequest request = new AddAliasRequest();
        request.alias("no artist");

        assertEquals(HttpStatus.NOT_FOUND, assertThrows(HttpClientResponseException.class, () -> client
                .toBlocking()
                .exchange(HttpRequest.PUT("/artist/" + artistId + "/addAlias", request))).getStatus());

        assertEquals(HttpStatus.NOT_FOUND, assertThrows(HttpClientResponseException.class, () -> client
                .toBlocking()
                .exchange(HttpRequest.GET("/artist/" + artistId))).getStatus());
    }

    @Test
    void testArtistOfTheDayWith1ArtistOnly() throws Exception {
        var artist = createArtist("the artist");

        var resp = client
                .toBlocking()
                .exchange("/artist/ofTheDay/2023-10-24", Artist.class);

        assertEquals(HttpStatus.OK, resp.status());
        assertEquals(artist, resp.body().getId());
    }

    @Test
    void testArtistOfTheDayWith2ArtistReturnsTheSameArtistForTheSameDate() throws Exception {
        createArtist("the artist");
        createArtist("the other one");

        var resp1 = client
                .toBlocking()
                .exchange("/artist/ofTheDay/2023-10-24", Artist.class);

        assertEquals(HttpStatus.OK, resp1.status());

        var resp2 = client
                .toBlocking()
                .exchange("/artist/ofTheDay/2023-10-24", Artist.class);

        assertEquals(HttpStatus.OK, resp2.status());

        assertEquals(resp1.body().getId(), resp2.body().getId());
    }

    @Test
    void testArtistOfTheDayWith2ArtistReturnsDifferentArtistForDifferentDate() throws Exception {
        var artists = Arrays.asList(
                createArtist("the artist"),
                createArtist("the other one")
        );
        artists.sort(String::compareTo);

        var resp1 = client
                .toBlocking()
                .exchange("/artist/ofTheDay/2023-10-24", Artist.class);

        assertEquals(HttpStatus.OK, resp1.status());

        var resp2 = client
                .toBlocking()
                .exchange("/artist/ofTheDay/2023-10-25", Artist.class);

        assertEquals(HttpStatus.OK, resp2.status());

        var artistsOfTheDay = Arrays.asList(
                resp2.body().getId(), resp1.body().getId()
        );
        artistsOfTheDay.sort(String::compareTo);

        assertIterableEquals(
                artists, artistsOfTheDay
        );
    }

    @Test
    void testArtistOfTheDayWith2ArtistReturnsRestartReturningArtistsWithMoreThan2Dates() throws Exception {
        var a1 = createArtist("the artist");
        var a2 = createArtist("the other one");
        var a3 = createArtist("does not really matter");

        var artists = Arrays.asList(
                a1, a2, a3, a1, a2, a3
        );
        artists.sort(String::compareTo);

        var artistsOfTheDay = IntStream.range(0, 6).mapToObj(i -> {
                    HttpResponse<Artist> response = client
                            .toBlocking()
                            .exchange("/artist/ofTheDay/2023-10-2" + i, Artist.class);
                    assertEquals(HttpStatus.OK, response.status());
                    return response
                            .body().getId();
                }
        ).sorted().toList();

        assertIterableEquals(artists, artistsOfTheDay);
    }

    private @Nullable Artist getArtist(String artistId) {
        var artistHttpResponse2 = client
                .toBlocking()
                .exchange(HttpRequest.GET("/artist/" + artistId), Artist.class);

        assertEquals(HttpStatus.OK, artistHttpResponse2.status());
        return artistHttpResponse2.body();
    }


    private String createArtist(String bandName) {
        CreateArtistRequest createArtistRequest = new CreateArtistRequest();

        createArtistRequest.name(bandName);
        var creationResponse = client
                .toBlocking()
                .exchange(HttpRequest.POST("/artist", createArtistRequest), CreateArtist201Response.class);
        return creationResponse.body().getId();
    }

}
