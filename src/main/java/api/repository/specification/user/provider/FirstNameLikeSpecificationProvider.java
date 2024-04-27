package api.repository.specification.user.provider;

import static api.constant.CriteriaQueryConstantsHolder.FIRST_NAME_COLUMN;
import static api.constant.CriteriaQueryConstantsHolder.PERCENT;

import api.model.User;
import api.repository.specification.provider.LikeSpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class FirstNameLikeSpecificationProvider
        implements LikeSpecificationProvider<User> {

    @Override
    public Specification<User> getSpecification(String firstName) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(
                        root.get(FIRST_NAME_COLUMN), PERCENT + firstName + PERCENT
        );
    }

    @Override
    public String getKey() {
        return FIRST_NAME_COLUMN;
    }
}
