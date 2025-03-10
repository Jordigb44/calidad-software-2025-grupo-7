package es.codeurjc.web.nitflex.dto.review;

import java.util.Date;

import es.codeurjc.web.nitflex.dto.user.UserSimpleDTO;

public record ReviewSimpleDTO(Long id, String text, int score, Date created_at, UserSimpleDTO user) {}

