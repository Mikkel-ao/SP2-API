package app.dtos;

import lombok.*;
import java.util.Set;

/*
Already have a userDTO from Thomas' Jitpack import which is tied to tokens.
UserProfileDTO is used for API data shape - Displaying user without security coupling
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private String username;
    private Set<String> roles;
}
