package api.repository.specification.user.provider;

import static api.constant.CriteriaQueryConstantsHolder.BIRTH_DATE_COLUMN;
import static api.constant.OtherConstantsHolder.ONE;
import static api.constant.OtherConstantsHolder.ZERO;

import api.model.User;
import api.repository.specification.provider.BetweenSpecificationProvider;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BirthDateBetweenSpecificationProvider
        implements BetweenSpecificationProvider<User, List<LocalDate>> {

    @Override
    public Specification<User> getSpecification(List<LocalDate> birthDate) {
        LocalDate from = birthDate.get(ZERO);
        LocalDate to = birthDate.get(ONE);
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("""
                    First date of searching by birthDate should be before the second date
                    """);
        }
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.between(
                        root.get(BIRTH_DATE_COLUMN), from, to
        );
    }

    @Override
    public String getKey() {
        return BIRTH_DATE_COLUMN;
    }
}
