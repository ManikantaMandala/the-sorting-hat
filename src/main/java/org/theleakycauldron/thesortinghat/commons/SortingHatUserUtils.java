package org.theleakycauldron.thesortinghat.commons;

import org.theleakycauldron.thesortinghat.dtos.SortingHatLoginResponseDTO;

/**
 * @author: Vijaysurya Mandala
 * @github: github/mandalavijaysurya (<a href="https://www.github.com/mandalavijaysurya"> Github</a>)
 */


public class SortingHatUserUtils {
    public static SortingHatLoginResponseDTO convertToLoginResponseDTO(String token, String username, String email) {
        return SortingHatLoginResponseDTO.builder()
                .name(username)
                .email(username)
                .build();

    }
}
