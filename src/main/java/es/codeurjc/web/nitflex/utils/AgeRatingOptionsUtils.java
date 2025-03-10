package es.codeurjc.web.nitflex.utils;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgeRatingOptionsUtils {

    public enum AgeRating {
		GENERAL_AUDIENCES("+0"),
		AGES_7_AND_UP("+7"),
		AGES_12_AND_UP("+12"),
		AGES_16_AND_UP("+16"),
		AGES_18_AND_UP("+18");
	
		private final String description;
	
		AgeRating(String description) {
			this.description = description;
		}
	
		public String getDescription() {
			return description;
		}
	}

	public record AgeRatingOption(String description, boolean selected) {}

    public static Collection<AgeRatingOption> getAgeRatingOptions(String selectedAgeRating) {
        return Stream.of(AgeRating.values())
			.map(
				ageRating -> new AgeRatingOption(
					ageRating.getDescription(), 
					ageRating.getDescription().equals(selectedAgeRating)
			))
			.collect(Collectors.toList());
    }
    
}
