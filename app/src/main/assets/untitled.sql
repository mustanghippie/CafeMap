SELECT * FROM salesman;

SELECT ord_date, salesmen_id, ord_no, purch_amt FROM orders;

SELECT salesman.salesman_id, orders.customer_id FROM salesman
INNER JOIN orders ON salesman.salesman_id = orders.salesman_id
GROUP BY orders.customer_id;

SELECT Distinct salesman.salesman_id,orders.customer_id FROM salesman
INNER JOIN orders ON salesman.salesman_id = orders.salesman_id

SELECT salesman.name, salesman.city FROM salesman
WHERE city = "Paris";

SELECT * FROM customer
WHERE grade = 200;

SELECT orders.ord_no, orders.ord_date, orders.purch_amt FROM orders
INNER JOIN salesman ON orders.salesman_id = salesman.salesman_id
WHERE salesman.salesman_id = 5001;

SELECT * FROM customer
WHERE cust_name LIKE 'B*';

SELECT * FROM customer
WHERE cust_name LIKE '*n';

SELECT * FROM salesman
WHERE name LIKE 'N*' AND name LIKE '___I*';

SELECT salesman.name, customer.cust_name FROM salesman
INNER JOIN customer ON salesman.city = customer.cust_name;

SELECT salesman.name, customer.cust_name FROM salesman
INNER JOIN customer ON salesman.salesman_id = customer.salesman_id;

SELECT customer.cust_name FROM customer
INNER JOIN salesman ON customer.salesman_id = salesman.salesman_id
WHERE salesman.commision >= 0.12;

SELECT orders.ord_no, orders.ord_date, orders.purch_amt, customer.cust_name,salesman.name,salesman.commision
FROM orders 
INNER JOIN customer ON orders.customer_id = orders.customer_id
INNER JOIN salesman ON customer.salesman_id = salesman.salesman_id;

SELECT name FROM salesman
WHERE city = 'New York' AND commision >= 0.13;

SELECT grade, count(grade) FROM customer
GROUP BY grade;

INSERT INTO customer VALUES(3002, 'Nick Rimando', 'New York', 100,5001);
INSERT INTO customer VALUES(3005, 'Graham Zusi', 'California', 200,5002);
INSERT INTO customer VALUES(3007, 'Brad Guzan', 'London', 200,5001);

CREATE TABLE salesman(
salesman_id integer, name text, city text, commision real
);

CREATE TABLE orders(
ord_no integer, purch_amt real, ord_date text, customer_id integer, salesman_id integer
);

CREATE TABLE customer(
customer_id integer, cust_name text, city text, grade integer, salesman_id integer
);