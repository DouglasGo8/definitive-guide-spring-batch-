CREATE OR REPLACE PROCEDURE customer_list(IN cityOption CHAR(16), inout result refcursor)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
	OPEN result FOR
		SELECT
			id,
			firstName,
			middleInitial,
			lastName,
			address,
			city,
			state,
			zipcode
		FROM
			tbl_customer WHERE city = cityOption;
END;
$BODY$;


call public.customer_list('Stamford', 'resultSet');
fetch all in "resultSet";
