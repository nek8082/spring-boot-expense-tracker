CREATE TABLE saas.transaction_record (
    id BIGSERIAL NOT NULL,
    user_id BIGINT NOT NULL,
    date DATE NOT NULL,
    voucher_number VARCHAR(20) NOT NULL,
    description VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    counter_account VARCHAR(50) NOT NULL,
    income DOUBLE PRECISION NOT NULL,
    expenses DOUBLE PRECISION NOT NULL,
    tax_rate DOUBLE PRECISION,
    remarks VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES saas.app_user (user_id) ON DELETE CASCADE
);