package api.repository.specification.user.provider;

import static api.constant.CriteriaQueryConstantsHolder.EMAIL_COLUMN;
import static api.constant.CriteriaQueryConstantsHolder.PERCENT;

import api.model.User;
import api.repository.specification.provider.LikeSpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class EmailLikeSpecificationProvider
        implements LikeSpecificationProvider<User> {

    @Override
    public Specification<User> getSpecification(String email) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(
                        root.get(EMAIL_COLUMN), PERCENT + email + PERCENT
        );
    }

    @Override
    public String getKey() {
        return EMAIL_COLUMN;
    }
}
