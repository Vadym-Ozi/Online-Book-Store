package example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class FieldMatchValidation implements ConstraintValidator<FieldMatch, Object> {
    private String password;
    private String repeatPassword;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        password = constraintAnnotation.password();
        repeatPassword = constraintAnnotation.repeatPassword();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        Class<?> clazz = value.getClass();
        try {
            Field pass = clazz.getDeclaredField(password);
            Field repeatPass = clazz.getDeclaredField(repeatPassword);
            pass.setAccessible(true);
            repeatPass.setAccessible(true);
            String passValue = (String) pass.get(value);
            String repeatPassValue = (String) repeatPass.get(value);
            return passValue != null && passValue.equals(repeatPassValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
