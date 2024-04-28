package api.constant;

public interface SecurityConstantsHolder {
    String AUTHORIZATION = "Authorization";
    String BEARER_AUTH = "BearerAuth";
    String BEARER = "Bearer";
    String JWT = "JWT";
    int TOKEN_START = 7;
    String JWT_PATTERN = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$";
}
