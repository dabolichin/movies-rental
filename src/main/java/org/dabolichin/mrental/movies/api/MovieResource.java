package org.dabolichin.mrental.movies.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javaslang.control.Option;
import org.dabolichin.mrental.movies.MovieType;

@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,
        getterVisibility= JsonAutoDetect.Visibility.NONE,
        isGetterVisibility= JsonAutoDetect.Visibility.NONE,
        setterVisibility= JsonAutoDetect.Visibility.NONE)
public class MovieResource {

    public Option<String> id;

    public String title;

    public MovieType type;

    private MovieResource() {}

    public MovieResource(Option id, String title, MovieType type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }
}
