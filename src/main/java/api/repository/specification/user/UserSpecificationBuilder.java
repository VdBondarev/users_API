package api.repository.specification.user;

import static api.constant.CriteriaQueryConstantsHolder.ADDRESS_COLUMN;
import static api.constant.CriteriaQueryConstantsHolder.BIRTH_DATE_COLUMN;
import static api.constant.CriteriaQueryConstantsHolder.EMAIL_COLUMN;
import static api.constant.CriteriaQueryConstantsHolder.FIRST_NAME_COLUMN;
import static api.constant.CriteriaQueryConstantsHolder.LAST_NAME_COLUMN;
import static api.constant.CriteriaQueryConstantsHolder.PHONE_NUMBER_COLUMN;

import api.dto.UserSearchParametersRequestDto;
import api.model.User;
import api.repository.specification.SpecificationBuilder;
import api.repository.specification.manager.BetweenSpecificationProviderManager;
import api.repository.specification.manager.LikeSpecificationProviderManager;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSpecificationBuilder
        implements SpecificationBuilder<User, UserSearchParametersRequestDto> {
    private final BetweenSpecificationProviderManager<User, List<LocalDate>>
            betweenSpecificationProviderManager;
    private final LikeSpecificationProviderManager<User>
            likeSpecificationProviderManager;

    @Override
    public Specification<User> build(UserSearchParametersRequestDto searchParametersDto) {
        Specification<User> specification = Specification.where(null);
        if (notEmpty(searchParametersDto.firstName())) {
            specification = getLikeSpecification(
                    FIRST_NAME_COLUMN,
                    specification,
                    searchParametersDto.firstName()
            );
        }
        if (notEmpty(searchParametersDto.lastName())) {
            specification = getLikeSpecification(
                    LAST_NAME_COLUMN,
                    specification,
                    searchParametersDto.lastName()
            );
        }
        if (notEmpty(searchParametersDto.address())) {
            specification = getLikeSpecification(
                    ADDRESS_COLUMN,
                    specification,
                    searchParametersDto.address()
            );
        }
        if (notEmpty(searchParametersDto.email())) {
            specification = getLikeSpecification(
                    EMAIL_COLUMN,
                    specification,
                    searchParametersDto.email()
            );
        }
        if (notEmpty(searchParametersDto.phoneNumber())) {
            specification = getLikeSpecification(
                    PHONE_NUMBER_COLUMN,
                    specification,
                    searchParametersDto.phoneNumber()
            );
        }
        if (searchParametersDto.birthDate() != null) {
            specification = specification
                    .and(betweenSpecificationProviderManager
                            .getBetweenSpecificationProvider(BIRTH_DATE_COLUMN)
                            .getSpecification(searchParametersDto.birthDate())
                    );
        }
        return specification;
    }

    private boolean notEmpty(String field) {
        return field != null && !field.isEmpty();
    }

    private Specification<User> getLikeSpecification(
            String column,
            Specification<User> specification,
            String param) {
        return specification.and(
                likeSpecificationProviderManager
                        .getLikeSpecificationProvider(column)
                        .getSpecification(param)
        );
    }
}
