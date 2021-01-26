package julia.books.error;

import lombok.Value;

@Value
@SuppressWarnings("PMD.DefaultPackage")
class ErrorRepresentation {
    String message;

    public ErrorRepresentation(String message) {
        this.message = message;
    }
}
