package io.github.jumperonjava.imaginebook.resolvers;

import io.github.jumperonjava.imaginebook.image.BookDrawable;

public interface Resolver {
    BookDrawable resolve(String path);
}
