package com.example.treaders.videoFormat;


import org.springframework.lang.NonNull;

public record VideoFormat(
        @NonNull
        String title,
        @NonNull
        String decription,
        @NonNull
        String filename
) {
}
