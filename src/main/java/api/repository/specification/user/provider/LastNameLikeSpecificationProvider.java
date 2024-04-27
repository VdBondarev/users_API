package api.repository.specification.user.provider;

import static api.constant.CriteriaQueryConstantsHolder.LAST_NAME_COLUMN;
import static api.constant.CriteriaQueryConstantsHolder.PERCENT;

import api.model.User;
import api.repository.specification.provider.LikeSpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class LastNameLikeSpecificationProvider
        implements LikeSpecificationProvider<User> {

    @Override
    public Specification<User> getSpecification(String lastName) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(
                        root.get(LAST_NAME_COLUMN), PERCENT + lastName + PERCENT
        );
    }

    @Override
    public String getKey() {
        return LAST_NAME_COLUMN;
    }
}
