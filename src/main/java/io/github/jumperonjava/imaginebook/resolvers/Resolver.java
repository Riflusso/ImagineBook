package io.github.jumperonjava.imaginebook.resolvers;

import io.github.jumperonjava.imaginebook.Image;

import java.util.Arrays;

public interface Resolver {
    Image resolve(String path);
}
