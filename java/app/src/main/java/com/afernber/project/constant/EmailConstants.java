package com.afernber.project.constant;

public final class EmailConstants {
    private EmailConstants() {}

    // --- Templates ---
    public static final String TEMPLATE_GENERAL = "general-template";
    public static final String TEMPLATE_ERROR = "failed-event";
    public static final String TEMPLATE_PAYMENT = "payment-template";

    // --- Configuration ---
    public static final String FROM = "desalaj311@dubokutv.com";
    public static final String MAINTENANCE_EMAIL = "test-email@gmail.com";
    public static final String BUSINESS = "Payments Corporation";
    public static final String ATTACHMENT_FILE = "stacktrace.txt";

    // --- Email Subjects ---
    public static final String SUBJECT_CRITICAL_ERROR = "CRITICAL: System Failure";
    public static final String SUBJECT_ACTIVITY = "Activity in your account";
    public static final String SUBJECT_PAYMENT_RECEIPT = "Your Payment Receipt";

    // --- Template Placeholder Keys (Variable Names) ---
    public static final String TITLE = "title";
    public static final String BUSINESS_NAME = "businessName";
    public static final String RECIPIENT_NAME = "recipientName";
    public static final String MESSAGE = "message";

    // Error Template Keys
    public static final String EVENT_TYPE = "eventType";
    public static final String TOPIC = "topic";
    public static final String ERROR_MESSAGE = "errorMessage";

    // Payment Template Keys
    public static final String TRANSACTION_ID = "transactionId";
    public static final String AMOUNT = "amount";
    public static final String DATE = "date";

    // --- Activity Message Content ---

    // Member Activity
    public static final String CREATE_USER_ACTIVITY = "Welcome to our application, we have cookies!";
    public static final String UPDATE_USER_ACTIVITY = "Some changes were made to your account information.";
    public static final String DELETED_USER_ACTIVITY = "Your account has been deleted, see you soon!";

    // Payment Activity
    public static final String CREATE_PAYMENT_ACTIVITY = "Your transaction was successful. Thank you for your business!";
    public static final String UPDATE_PAYMENT_ACTIVITY = "A modification was made to one of your existing payment records.";
    public static final String DELETED_PAYMENT_ACTIVITY = "A payment record has been removed from your history.";
}