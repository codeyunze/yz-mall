
CREATE TRIGGER `after_insertOrder_postStock`
after insert on `shop_order` for each row
	-- 触发器内容主体，每行用分号结尾
	INSERT INTO shop_stock (id, product_number, product_stock)  VALUES (NEW.id, NEW.product_number, NEW.product_quantity);