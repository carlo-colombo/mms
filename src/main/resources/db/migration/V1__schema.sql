CREATE TABLE artist (
  id UUID PRIMARY KEY
);

CREATE TABLE alias (
    id SERIAL PRIMARY KEY,
    artist_id UUID NOT NULL REFERENCES  artist(id),
    name TEXT NOT NULL
);

CREATE TABLE main_name (
    artist_id UUID PRIMARY KEY NOT NULL REFERENCES  artist(id),
    alias_id INT NOT NULL REFERENCES alias(id)
);

CREATE TABLE artist_of_the_day (
    artist_id UUID NOT NULL REFERENCES  artist(id),
    "date" date NOT NULL PRIMARY KEY
);

CREATE TABLE track (
    id UUID PRIMARY KEY,
    genre varchar(255),
    length_in_seconds INT,
    title TEXT,
    album TEXT
);

CREATE TABLE artist_track (
    artist_id UUID NOT NULL REFERENCES  artist(id),
    track_id UUID NOT NULL REFERENCES  track(id),
    UNIQUE (track_id, artist_id)
);