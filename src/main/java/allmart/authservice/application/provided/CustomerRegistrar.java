package allmart.authservice.application.provided;

public interface CustomerRegistrar {
    void register(String email, String rawPassword, String name);
}
