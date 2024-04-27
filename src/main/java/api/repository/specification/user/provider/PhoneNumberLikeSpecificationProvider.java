package api.repository.specification.user.provider;

import static api.constant.CriteriaQueryConstantsHolder.PERCENT;
import static api.constant.CriteriaQueryConstantsHolder.PHONE_NUMBER_COLUMN;

import api.model.User;
import api.repository.specification.provider.LikeSpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberLikeSpecificationProvider
        implements LikeSpecificationProvider<User> {

    @Override
    public Specification<User> getSpecification(String phoneNumber) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(
                        root.get(PHONE_NUMBER_COLUMN), PERCENT + phoneNumber + PERCENT
        );
    }

    @Override
    public String getKey() {
        return PHONE_NUMBER_COLUMN;
    }
}
