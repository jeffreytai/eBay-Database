LOAD DATA LOCAL INFILE './User.del'
	INTO TABLE User
	FIELDS TERMINATED BY ' |*| ';

LOAD DATA LOCAL INFILE './Item.del'
	INTO TABLE Item
	FIELDS TERMINATED BY ' |*| '
	LINES TERMINATED BY "\n"
	(ItemID, Name, Description, Currently, @buy_price, First_Bid, Number_of_bids, SellerID, Started, Ends)
		SET Buy_Price = nullif(@buy_price, 0.000000);

LOAD DATA LOCAL INFILE './Locate.del'
	INTO TABLE Locate
	FIELDS TERMINATED BY ' |*| '
	(ItemID, Location, Country, @latitude, @longitude) SET Latitude = nullif(@latitude, 0.000000),
													Longitude = nullif(@longitude, 0.000000);

LOAD DATA LOCAL INFILE './Bids.del'
	INTO TABLE Bids
	FIELDS TERMINATED BY ' |*| ';

LOAD DATA LOCAL INFILE './Category.del'
	INTO TABLE Category
	FIELDS TERMINATED BY ' |*| ';

LOAD DATA LOCAL INFILE './ItemCategory.del'
	INTO TABLE ItemCategory
	FIELDS TERMINATED BY ' |*| ';