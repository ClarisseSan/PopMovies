/*
* Copyright 2016 Angela Sanchez

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
* */

package com.itweeti.isse.popmovies.utils;

/**
 * Created by isse on 27 Jun 2016.
 */
public final class Config {

    // Suppress default constructor for noninstantiability
    private Config() {
        throw new AssertionError();
    }

    // Google Console APIs developer key
    public static final String DEVELOPER_KEY = "AIzaSyBfS1diO_HpBW0eT2GLQDIwAnNyh1msNGM";

    //moviedb.org API key
    public static final String API_KEY = "6d369d4e0676612d2d046b7f3e8424bd";
}
