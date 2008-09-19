package gov.nih.nci.cabig.caaers.web.fields.validators;

public abstract class FieldValidator {

    public static final FieldValidator NOT_NULL_VALIDATOR;

    public static final FieldValidator EMAIL_VALIDATOR;

    public static final FieldValidator PHONE_VALIDATOR;

    public static final FieldValidator PAST_DATE_VALIDATOR;
    public static final FieldValidator FUTURE_DATE_VALIDATOR;

    public static final FieldValidator DATE_VALUE_VALIDATOR;

    public static final FieldValidator NUMBER_VALIDATOR;
    public static final FieldValidator HOUR_VALIDATOR;
    public static final FieldValidator MINUTE_VALIDATOR;
    public static final FieldValidator ZIP_CODE_VALIDATOR;

    static {
        NOT_NULL_VALIDATOR = new NotNullValidator();
        EMAIL_VALIDATOR = new EmailValidator();
        PHONE_VALIDATOR = new PhoneNumberValidator();
        PAST_DATE_VALIDATOR = new PastDateValidator();
        DATE_VALUE_VALIDATOR = new DateValueValidator();
        NUMBER_VALIDATOR = new NumberValidator();
        HOUR_VALIDATOR = createNumberRangeValidator(1, 12);
        MINUTE_VALIDATOR = createNumberRangeValidator(0, 59);
        ZIP_CODE_VALIDATOR = new ZipCodeValidator();
        FUTURE_DATE_VALIDATOR = new FutureDateValidator();
    }

    static NumberRangeValidator createNumberRangeValidator(int begin, int end) {
        return new NumberRangeValidator(begin, end);
    }

    /**
     * Will validate the input, based on the specific validation rules.
     *
     * @return
     */
    public abstract boolean isValid(Object fieldValue);

    /**
     * This will return the error message prefix. eg: in case of NotNullValidator - it should be
     * "Missing" where as in the case of a PhoneNumberValidator it should be "Invalid"
     *
     * @return
     */
    public abstract String getMessagePrefix();

    public abstract String getValidatorCSSClassName();

    public String stringValue(Object fieldValue) {
        if (fieldValue == null) return null;
        return fieldValue.toString();
    }

}
