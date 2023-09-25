package ovh.litapp.mms.artist;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import ovh.litapp.mms.artist.data.ArtistNotFoundException;

@Produces
@Singleton
@Requires(classes = {ArtistNotFoundException.class, ExceptionHandler.class})
public class CustomExceptionHandler
        implements ExceptionHandler<ArtistNotFoundException, HttpResponse<Void>> {

    @Override
    public HttpResponse<Void>
    handle(HttpRequest request, ArtistNotFoundException exception) {
        return HttpResponse.status(HttpStatus.NOT_FOUND);
    }
}