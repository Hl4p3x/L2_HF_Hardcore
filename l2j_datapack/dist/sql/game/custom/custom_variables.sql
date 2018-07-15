DROP IF EXISTS custom_variables;

CREATE TABLE custom_variables (
    var  VARCHAR2(255),
    value VARCHAR2(255)
);

INSERT INTO custom_variables VALUES ('MaxOnline', '0');
