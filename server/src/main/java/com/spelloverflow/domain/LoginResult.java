package com.spelloverflow.domain;

import com.spelloverflow.models.User;

public record LoginResult(User user, String token) {
}
